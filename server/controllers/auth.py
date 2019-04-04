"""
There are two ways to authenticate a request:
* Present the session cookie returned after logging in
* Send a Google access token as the access_token query parameter
"""
from flask import abort, Blueprint, current_app, flash, redirect, \
    render_template, request, session, url_for, make_response
from flask_oauthlib.client import OAuth
from flask.ext.login import LoginManager, login_user, logout_user, login_required

import pickle

from server.models import db, User

auth = Blueprint('auth', __name__)

auth.config = {}

oauth = OAuth()
google_auth = oauth.remote_app(
    'google',
    app_key='GOOGLE',
    request_token_params={
        'scope': 'email'
    },
    base_url='https://www.googleapis.com/oauth2/v1/',
    request_token_url=None,
    access_token_method='POST',
    access_token_url='https://accounts.google.com/o/oauth2/token',
    authorize_url='https://accounts.google.com/o/oauth2/auth',
)

oauth_client = OAuth()

client_auth = oauth_client.remote_app(
    'google',
    consumer_key='931757735585-vb3p8g53a442iktc4nkv5q8cbjrtuonv.apps.googleusercontent.com',
    consumer_secret='zGY9okExIBnompFTWcBmOZo4', # not actually a secret, from ok-client.
    request_token_params={
        'scope': 'email',
        'access_type':'offline',
        'approval_prompt':'force'
    },
    base_url='https://www.googleapis.com/oauth2/v1/',
    request_token_url=None,
    access_token_method='POST',
    access_token_url='https://accounts.google.com/o/oauth2/token',
    authorize_url='https://accounts.google.com/o/oauth2/auth',
)


@auth.record
def record_params(setup_state):
    """ Load used app configs into local config on registration from
    server/__init__.py """
    app = setup_state.app
    oauth.init_app(app)

@google_auth.tokengetter
def google_oauth_token(token=None):
    return session.get('google_token', None)

def user_from_email(email):
    """Get a User with the given email, or create one."""
    user = User.lookup(email)
    if not user:
        user = User(email=email)
        db.session.add(user)
        db.session.commit()
    return user

def user_from_access_token(token):
    """
    Get a User with the given Google access token, or create one if no User with
    this email is found. If the token is invalid, return None.
    """
    if use_testing_login():
        return user_from_email("okstaff@okpy.org")
    resp = google_auth.get('userinfo', token=(token, ''))
    if resp.status != 200:
        return None
    return user_from_email(resp.data['email'])

login_manager = LoginManager()

@login_manager.user_loader
def load_user(userid):
    return User.query.get(userid)

@login_manager.request_loader
def load_user_from_request(request):
    token = request.args.get('access_token', None)
    if token is None:
        return None
    return user_from_access_token(token)

@login_manager.unauthorized_handler
def unauthorized():
    session['after_login'] = request.url
    return redirect(url_for('auth.login'))

def authorize_user(user):
    login_user(user)
    after_login = session.pop('after_login', None)
    return redirect(after_login or url_for('student.index'))

def use_testing_login():
    """
    Return True if we use the unsecure testing login instead of Google OAuth.
    Requires TESTING_LOGIN = True in the config and the environment is not prod.
    """
    return current_app.config.get('TESTING_LOGIN', False) and \
        current_app.config.get('ENV') != 'prod'

@auth.route("/login/")
def login():
    """
    Authenticates a user with an access token using Google APIs.
    """
    return google_auth.authorize(callback=url_for('.authorized', _external=True))

@auth.route("/login/refresh")
def login_refresh():
    """
    Provides a user with .ok_refresh token after logging in with Google
    """
    return client_auth.authorize(callback=url_for('.refresh_token', _external=True))


@auth.route('/login/refresh/authorized/')
@client_auth.authorized_handler
def refresh_token(resp):
    if resp is None or 'access_token' not in resp:
        error = 'Access denied: reason=%s error=%s' % (
            request.args['error_reason'],
            request.args['error_description']
        )
        flash(error, "error")
        return redirect(url_for("main.home"))

    refresh_file = {'access_token': resp['access_token'],
     'refresh_token': resp['refresh_token'],
     'expires_at': resp['expires_in']}
    response = make_response(pickle.dumps(refresh_file))
    response.headers['Content-Disposition'] = (
        'attachment; filename=ok_refresh')
    response.headers['Content-Type'] = 'application/octet-stream'
    return response


@auth.route('/login/authorized/')
@google_auth.authorized_handler
def authorized(resp):
    if resp is None or 'access_token' not in resp:
        error = 'Access denied: reason=%s error=%s' % (
            request.args['error_reason'],
            request.args['error_description']
        )
        flash(error, "error")
        # TODO Error Page
        return redirect(url_for('student.index'))
    access_token = resp['access_token']
    user = user_from_access_token(access_token)
    session['google_token'] = (access_token, '')  # (access_token, secret)
    return authorize_user(user)

# Backdoor log in if you want to impersonate a user.
# Will not give you a Google auth token.
# Requires that TESTING_LOGIN = True in the config and we must not be running in prod.
@auth.route('/testing-login/')
def testing_login():
    if not use_testing_login():
        abort(404)
    return render_template('testing-login.html', callback=url_for(".testing_authorized"))

@auth.route('/testing-login/authorized/', methods=['POST'])
def testing_authorized():
    if not use_testing_login():
        abort(404)
    user = user_from_email(request.form['email'])
    return authorize_user(user)

@auth.route("/logout/")
def logout():
    logout_user()
    session.pop('google_token', None)
    return redirect(url_for('student.index'))
