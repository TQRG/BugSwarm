from spamhandling import *
import pytest
import os

test_data_inputs = []
with open("test/data_test_spamhandling.txt", "r") as f:
    test_data_inputs = f.readlines()

@pytest.mark.parametrize("title, body, username, site, match", [
     ('18669786819 gmail customer service number 1866978-6819 gmail support number', '', '', '', True),
     ('Is there any http://www.hindawi.com/ template for Cloud-Oriented Data Center Networking?', '', '', '', True),
     ('', '', 'bagprada', '', True),
     ('HOW DO YOU SOLVE THIS PROBLEM?', '', '', '', True),
     ('12 Month Loans quick @ http://www.quick12monthpaydayloans.co.uk/Elimination of collateral pledging', '', '', '', True),
     ('support for yahoo mail 18669786819 @call for helpline number', '', '', '', True),
     ('yahoo email tech support 1 866 978 6819 Yahoo Customer Phone Number ,Shortest Wait', '', '', '', True),
     ('What is the value of MD5 checksums if the MD5 hash itself could potentially also have been manipulated?', '', '', '', False),
     ('Probability: 6 Dice are rolled. Which is more likely, that you get exactly one 6, or that you get 6 different numbers?', '', '', '', False),
     ('The Challenge of Controlling a Powerful AI', '', 'Serban Tanasa', '', False),
     ('Reproducing image of a spiral using TikZ', '', 'Kristoffer Ryhl', '', False),
     ('What is the proper way to say "queryer"', '', 'jedwards', '', False),
     ('What\'s a real-world example of "overfitting"?', '', 'user3851283', '', False),
     ('How to avoid objects when traveling at greater than .75 light speed. or How Not to Go SPLAT?', '', 'bowlturner', '', False),
     ('Is it unfair to regrade prior work after detecting cheating?', '', 'Village', '', False),
     ('Inner workings of muscles', '', '', 'fitness.stackexchange.com', False),
     ('Cannot access http://stackoverflow.com/ with proxy enabled', '', '', 'superuser.com', False),
     ('kkkkkkkkkkkkkkkkkkkkkkkkkkkk', '<p>bbbbbbbbbbbbbbbbbbbbbb</p>', '', 'stackoverflow.com', True)
])

def test_check_if_spam(title, body, username, site, match):
    # We can't check blacklists/whitelists in tests, so these are set to their default values
    user_url = ""
    post_id = 0
    # If we want to test answers separatly, this should be changed
    is_answer = False
    is_spam, reason = check_if_spam(title, body, username, user_url, site, post_id, is_answer)
    print title
    assert match == is_spam

@pytest.mark.parametrize("data, match", [
     (test_data_inputs[0], False)
])

def test_check_if_spam_json(data, match):
    is_spam, reason = check_if_spam_json(data)
    assert match == is_spam

@pytest.mark.skipif(os.path.isfile("blacklistedUsers.txt"),
                    reason="shouldn't overwrite file")
def test_blacklisted_user():
    user_url = 'http://stackoverflow.com/users/1/jeff-atwood'
    user = get_user_from_url(user_url)
    add_blacklisted_user(user)
    is_spam, reason = check_if_spam("", "", "", user_url, "stackoverflow.com", "1", False)
    assert is_spam == True
    # cleanup
    os.remove("blacklistedUsers.txt")

@pytest.mark.skipif(os.path.isfile("whitelistedUsers.txt"),
                reason="shouldn't overwrite file")
def test_whitelisted_user():
    user_url = 'http://stackoverflow.com/users/2/geoff-dalgas'
    user = get_user_from_url(user_url)
    add_whitelisted_user(user)
    is_spam, reason = check_if_spam("", "", "", user_url, "stackoverflow.com", "1", False)
    assert is_spam == False
    # cleanup
    os.remove("whitelistedUsers.txt")
