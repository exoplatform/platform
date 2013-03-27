$(document).ready(function(){
    $('#userNameAccount').blur(function() {
        $('#usernameExistErrorId').remove();
        WelcomeScreens.USERNAME_EXIST = false;
        var usernameOnBlur = $.trim($("#userNameAccount").val());
        if(usernameOnBlur != ""){
            $.get(
                "/portal/rest/welcomeScreen/checkUsername",
                { "username": usernameOnBlur },
                function(data) {
                    var userExists = data.userExists;
                    if(userExists == true){
                        WelcomeScreens.USERNAME_EXIST = true;
                        $('#usernameId').after('<tr id ="usernameExistErrorId" ><td colspan="4" class ="accountSetupError"><b>Username</b> already exists.</td></tr>');
                    }
                });
        }
    });
});

var WelcomeScreens = {};

WelcomeScreens.FIRSTLASTNAME_INPUT_MIN_SIZE = 1;
WelcomeScreens.FIRSTLASTNAME_INPUT_MAX_SIZE = 45;
WelcomeScreens.USERNAME_INPUT_MAX_SIZE = 30;
WelcomeScreens.USERNAME_INPUT_MIN_SIZE = 3;
WelcomeScreens.PASSWORD_INPUT_MIN_SIZE = 6;
WelcomeScreens.PASSWORD_INPUT_MAX_SIZE = 30;
WelcomeScreens.ACCOUNT_SETUP_ERROR = false;
WelcomeScreens.USERNAME_EXIST = false;
WelcomeScreens.EMAIL_REGEXP = new RegExp(/^(("[\w-+\s]+")|([\w-+]+(?:\.[\w-+]+)*)|("[\w-+\s]+")([\w-+]+(?:\.[\w-+]+)*))(@((?:[\w-+]+\.)*\w[\w-+]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][\d]\.|1[\d]{2}\.|[\d]{1,2}\.))((25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\.){2}(25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\]?$)/i);
WelcomeScreens.USERNAME_REGEXP = new RegExp(/^[0-9a-z_.]+$/);
WelcomeScreens.FIRSTNAME_REGEXP = new RegExp(/^[a-zA-Z-' אבגדהועףפץצרטיךכחלםמןשתûüÿס]+$/);
WelcomeScreens.LASTNAME_REGEXP = new RegExp(/^[a-zA-Z-' אבגדהועףפץצרטיךכחלםמןשתûüÿס]+$/);
WelcomeScreens.FormatError = 'Only <b>lowercase letters, digits, dot and underscore</b> characters are allowed for the field </b>"User Name".</b>'

WelcomeScreens.exit = function() {
    WelcomeScreens.ACCOUNT_SETUP_ERROR = false;
    $('#usernameErrorId').remove();
    $('#usernameErrorLengthId').remove();
    $('#firstnameErrorLengthId').remove();
    $('#lastnameErrorLengthId').remove();
    $('#firstnameErrorId').remove();
    $('#lastnameErrorId').remove();
    $('#emailInvalidErrorId').remove();
    $('#emailErrorId').remove();
    $('#passwordErrorId').remove();
    $('#confirmPasswordErrorId').remove();
    $('#passwordErrorLengthId').remove();
    $('#PasswordNotMatchId').remove();
    $('#AdminPasswordErrorId').remove();
    $('#firstnameErrorFormatId').remove();
    $('#lastnameErrorFormatId').remove();
    $('#AdminConfirmPasswordErrorId').remove();
    $('#AdminpasswordErrorLengthId').remove();
    $('#AdminPasswordNotMatchId').remove();
    $('#usernameErrorFormatId').remove();
    var username = $.trim($("#userNameAccount").val());
    var usernameReg = $.trim($("#usernameRegExpid").val());
    if(usernameReg!="")
    {
        WelcomeScreens.USERNAME_REGEXP = new RegExp(usernameReg);
        WelcomeScreens.FormatError = $.trim($("#formatMsgid").val());
    }
    var max = $.trim($("#usernameMaxLengthid").val());
    var min = $.trim($("#usernameMinLengthid").val());
    if(min!=0) WelcomeScreens.USERNAME_INPUT_MIN_SIZE = min;
    if(max!=0) WelcomeScreens.USERNAME_INPUT_MAX_SIZE = max;
    var firstname = $.trim($("#firstNameAccount").val());
    var lastname = $.trim($("#lastNameAccount").val());
    var email = $.trim($("#emailAccount").val());
    var password = $.trim($("#userPasswordAccount").val());
    var confirmPassword = $.trim($("#confirmUserPasswordAccount").val());
    var adminPassword = $.trim($("#adminPassword").val());
    var confirmAdminPassword = $.trim($("#confirmAdminPassword").val());
    //Check User Name
    if(username == ""){
        $('#usernameId').after('<tr id ="usernameErrorId" ><td colspan="4" class ="accountSetupError"><b>Username</b> is required.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    else
    if (username.length < WelcomeScreens.USERNAME_INPUT_MIN_SIZE || username.length > WelcomeScreens.USERNAME_INPUT_MAX_SIZE){
        $('#usernameId').after('<tr id ="usernameErrorLengthId"><td colspan="4" class ="accountSetupError">The length of <b>Username</b> must be between '+ WelcomeScreens.USERNAME_INPUT_MIN_SIZE +' and ' +WelcomeScreens.USERNAME_INPUT_MAX_SIZE+ ' characters.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    if((username!="")&&(WelcomeScreens.USERNAME_REGEXP.test(username) == false)){
        $('#usernameId').after('<tr id ="usernameErrorFormatId"><td colspan="4" class ="accountSetupError">'+WelcomeScreens.FormatError+'</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    //check Last Name
    if(lastname == ""){
        $('#fullnameId').after('<tr id ="lastnameErrorId"><td colspan="4" class ="accountSetupError"><b>Last name</b> is required.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    else if((lastname.length<WelcomeScreens.FIRSTLASTNAME_INPUT_MIN_SIZE)||(lastname.length>WelcomeScreens.FIRSTLASTNAME_INPUT_MAX_SIZE)){
        $('#fullnameId').after('<tr id ="lastnameErrorLengthId"><td colspan="4" class ="accountSetupError">The length of <b>Last Name</b> must be between '+ WelcomeScreens.FIRSTLASTNAME_INPUT_MIN_SIZE +' and ' +WelcomeScreens.FIRSTLASTNAME_INPUT_MAX_SIZE+ ' characters.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    if((lastname!="")&&(WelcomeScreens.LASTNAME_REGEXP.test(lastname) == false)){
        $('#fullnameId').after('<tr id ="lastnameErrorFormatId"><td colspan="4" class ="accountSetupError">'+'Only letters, spaces, hyphen or apostrophe are allowed for the field <b>Last Name</b>'+'</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    if((firstname!="")&&WelcomeScreens.FIRSTNAME_REGEXP.test(firstname) == false){
        $('#fullnameId').after('<tr id ="firstnameErrorFormatId"><td colspan="4" class ="accountSetupError">'+'Only letters, spaces, hyphen or apostrophe are allowed for the field <b>First Name</b>'+'</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }

    //check First Name
    if(firstname == ""){
        $('#fullnameId').after('<tr id ="firstnameErrorId"><td colspan="4" class ="accountSetupError"><b>First name</b> is required.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    else if((firstname.length<WelcomeScreens.FIRSTLASTNAME_INPUT_MIN_SIZE)||(firstname.length>WelcomeScreens.FIRSTLASTNAME_INPUT_MAX_SIZE)){
        $('#fullnameId').after('<tr id ="firstnameErrorLengthId"><td colspan="4" class ="accountSetupError">The length of <b>First Name</b> must be between '+ WelcomeScreens.FIRSTLASTNAME_INPUT_MIN_SIZE +' and ' +WelcomeScreens.FIRSTLASTNAME_INPUT_MAX_SIZE+ ' characters.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    //check Email
    if(email == ""){
        $('#emailId').after('<tr id ="emailErrorId"><td colspan="4" class ="accountSetupError"><b>Email</b> is required.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    else
    if(WelcomeScreens.EMAIL_REGEXP.test(email) == false){
        $('#emailId').after('<tr id ="emailInvalidErrorId"><td colspan="4" class ="accountSetupError"><b>Email</b> is invalid.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }

    //check Password
    if (confirmPassword == "") {
        $('#passwordId').after('<tr id ="confirmPasswordErrorId"><td colspan="4" class ="accountSetupError"><b>Confirm</b> is required.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    if (password == "") {
        $('#passwordId').after('<tr id ="passwordErrorId" class ="accountSetupError"><td colspan="4" class ="accountSetupError"><b>Password</b> is required.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    else
    if (password.length < WelcomeScreens.PASSWORD_INPUT_MIN_SIZE || password.length > WelcomeScreens.PASSWORD_INPUT_MAX_SIZE){
        $('#passwordId').after('<tr id ="passwordErrorLengthId"><td colspan="4" class ="accountSetupError">The length of <b>Password</b> must be between 6 and 30 characters.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    if ((password != "") && (confirmPassword != "") && (password != confirmPassword)){
        $('#passwordId').after('<tr id ="PasswordNotMatchId"><td colspan="4" class ="accountSetupError"><b>Password</b> does not match the <b>Confirm password</b></td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }

    //check Admin Password
    if (confirmAdminPassword == "") {
        $('#adminPasswordId').after('<tr id ="AdminConfirmPasswordErrorId"><td colspan="4" class ="accountSetupError"><b>Confirm</b> is required.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    if (adminPassword == "") {
        $('#adminPasswordId').after('<tr id ="AdminPasswordErrorId"><td colspan="4" class ="accountSetupError"><b>Password</b> is required.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    else
    if (adminPassword.length < WelcomeScreens.PASSWORD_INPUT_MIN_SIZE || adminPassword.length > WelcomeScreens.PASSWORD_INPUT_MAX_SIZE){
        $('#adminPasswordId').after('<tr id ="AdminpasswordErrorLengthId"><td colspan="4" class ="accountSetupError">The length of <b>Password</b> must be between 6 and 30 characters.</td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }

    if ((adminPassword != "") && (confirmAdminPassword != "") && (adminPassword != confirmAdminPassword)){
        $('#adminPasswordId').after('<tr id ="AdminPasswordNotMatchId"><td colspan="4" class ="accountSetupError"><b>Password</b> does not match the <b>Confirm password</b></td></tr>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }

    if ((WelcomeScreens.ACCOUNT_SETUP_ERROR == false)&&(WelcomeScreens.USERNAME_EXIST == false)) {
        $('#AccountSetup1').css("display", "none");
        $('#Greetings').css("display", "block");
    }
}


