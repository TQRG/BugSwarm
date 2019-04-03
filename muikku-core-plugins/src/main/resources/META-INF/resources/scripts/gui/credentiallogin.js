(function() {
  
  $(document).ready(function () {
    var form = $(".credentialLoginWidget form");
    form.submit(function (event) {
      $('.notification-queue').notificationQueue('notification', 'loading', getLocaleText('plugin.credentialLogin.loggingIn'));
      
      var formName = $(this).attr('name');
      var password = $(this).find('input[name="' + formName + ':password"]');
      var passwordView = $(this).find('input[name="' + formName + ':password-view"]');
      
      password.val(hex_md5(passwordView.val()));
    });
  });

}).call(this);