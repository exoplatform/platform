$(document).ready(function(){
    $('.infoTip[title]').qtip({

        position: {
            my: 'bottom left',
            at: 'right top'

        },
        style: {
            classes: 'qtip-light qtip-rounded'
        }
    });
});

var WelcomeScreens = {};

WelcomeScreens.USERNAME_INPUT_MIN_SIZE = 3;
WelcomeScreens.USERNAME_INPUT_MAX_SIZE = 30;
WelcomeScreens.PASSWORD_INPUT_MIN_SIZE = 6;
WelcomeScreens.PASSWORD_INPUT_MAX_SIZE = 30;
WelcomeScreens.EMAIL_REGEXP = new RegExp(/^(("[\w-+\s]+")|([\w-+]+(?:\.[\w-+]+)*)|("[\w-+\s]+")([\w-+]+(?:\.[\w-+]+)*))(@((?:[\w-+]+\.)*\w[\w-+]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][\d]\.|1[\d]{2}\.|[\d]{1,2}\.))((25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\.){2}(25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\]?$)/i);


WelcomeScreens.validateTermsAndCondition = function(event) {
    if(event != undefined) {
        event.preventDefault();
    }
    if($('input[name=checktc]').is(':checked')) {
        $('#TermsAndCondition').fadeToggle("slow", "linear");
    }
    return false;
}
WelcomeScreens.exit = function() {
    $('#usernameErrorId').remove();
    $('#usernameErrorLengthId').remove();
    $('#firstnameErrorId').remove();
    $('#lastnameErrorId').remove();
    $('#emailInvalidErrorId').remove();
    $('#emailErrorId').remove();
    $('#passwordErrorId').remove();
    $('#confirmPasswordErrorId').remove();
    $('#passwordErrorLengthId').remove();
    $('#PasswordNotMatchId').remove();
    $('#AdminPasswordErrorId').remove();
    $('#AdminConfirmPasswordErrorId').remove();
    $('#AdminpasswordErrorLengthId').remove();
    $('#AdminPasswordNotMatchId').remove();

    var username = $.trim($("#userNameAccount").val());
    var firstname = $.trim($("#firstNameAccount").val());
    var lastname = $.trim($("#lastNameAccount").val());
    var email = $.trim($("#emailAccount").val());
    var password = $.trim($("#userPasswordAccount").val());
    var confirmPassword = $.trim($("#confirmUserPasswordAccount").val());
    var adminPassword = $.trim($("#adminPassword").val());
    var confirmAdminPassword = $.trim($("#confirmAdminPassword").val());
    var error = false;
    //Check User Name
    if(username == ""){
        $('#usernameId').after('<tr id ="usernameErrorId" ><td colspan="4" class ="accountSetupError"><b>Username</b> is required.</td></tr>');
        error = true;
    }
    else
    if (username.length < WelcomeScreens.USERNAME_INPUT_MIN_SIZE || username.length > WelcomeScreens.USERNAME_INPUT_MAX_SIZE){
        $('#usernameId').after('<tr id ="usernameErrorLengthId"><td colspan="4" class ="accountSetupError">The length of <b>Username</b> must be between 3 and 30 characters.</td></tr>');
        error = true;
    }

    //check Last Name
    if(lastname == ""){
        $('#fullnameId').after('<tr id ="lastnameErrorId"><td colspan="4" class ="accountSetupError"><b>Last name</b> is required.</td></tr>');
        error = true;
    }

    //check First Name
    if(firstname == ""){
        $('#fullnameId').after('<tr id ="firstnameErrorId"><td colspan="4" class ="accountSetupError"><b>First name</b> is required.</td></tr>');
        error = true;
    }

    //check Email
    if(email == ""){
        $('#emailId').after('<tr id ="emailErrorId"><td colspan="4" class ="accountSetupError"><b>Email</b> is required.</td></tr>');
        error = true;
    }
    else
    if(WelcomeScreens.EMAIL_REGEXP.test(email) == false){
        $('#emailId').after('<tr id ="emailInvalidErrorId"><td colspan="4" class ="accountSetupError"><b>Email</b> is invalid.</td></tr>');
        error = true;
    }

    //check Password
    if (confirmPassword == "") {
        $('#passwordId').after('<tr id ="confirmPasswordErrorId"><td colspan="4" class ="accountSetupError"><b>Confirm</b> is required.</td></tr>');
        error = true;
    }
    if (password == "") {
        $('#passwordId').after('<tr id ="passwordErrorId" class ="accountSetupError"><td colspan="4" class ="accountSetupError"><b>Password</b> is required.</td></tr>');
        error = true;
    }
    else
    if (password.length < WelcomeScreens.PASSWORD_INPUT_MIN_SIZE || password.length > WelcomeScreens.PASSWORD_INPUT_MAX_SIZE){
        $('#passwordId').after('<tr id ="passwordErrorLengthId"><td colspan="4" class ="accountSetupError">The length of <b>Password</b> must be between 6 and 30 characters.</td></tr>');
        error = true;
    }

    if ((password != "") && (confirmPassword != "") && (password != confirmPassword)){
        $('#passwordId').after('<tr id ="PasswordNotMatchId"><td colspan="4" class ="accountSetupError"><b>Password</b> and <b>Confirm</b> must be the same.</td></tr>');
        error = true;
    }

    //check Admin Password
    if (confirmAdminPassword == "") {
        $('#adminPasswordId').after('<tr id ="AdminConfirmPasswordErrorId"><td colspan="4" class ="accountSetupError"><b>Confirm</b> is required.</td></tr>');
        error = true;
    }
    if (adminPassword == "") {
        $('#adminPasswordId').after('<tr id ="AdminPasswordErrorId"><td colspan="4" class ="accountSetupError"><b>Password</b> is required.</td></tr>');
        error = true;
    }
    else
    if (adminPassword.length < WelcomeScreens.PASSWORD_INPUT_MIN_SIZE || adminPassword.length > WelcomeScreens.PASSWORD_INPUT_MAX_SIZE){
        $('#adminPasswordId').after('<tr id ="AdminpasswordErrorLengthId"><td colspan="4" class ="accountSetupError">The length of <b>Password</b> must be between 6 and 30 characters.</td></tr>');
        error = true;
    }

    if ((adminPassword != "") && (confirmAdminPassword != "") && (adminPassword != confirmAdminPassword)){
        $('#adminPasswordId').after('<tr id ="AdminPasswordNotMatchId"><td colspan="4" class ="accountSetupError"><b>Password</b> and <b>Confirm</b> must be the same.</td></tr>');
        error = true;
    }

    if (error == false) {
        $("form:first").submit();
    }
}
WelcomeScreens.toggleState = function () {
    if($('input[name=checktc]').is(':checked')) {
        $('input[name=checktc]').attr ( "checked" ,"checked" );

        WelcomeScreens.setActive();
    } else {
        $('input[name=checktc]').removeAttr('checked');
        WelcomeScreens.setInactive();
    }
}

WelcomeScreens.setInactive = function () {
    // add active class to the element
    $('#continueButton').addClass("inactive");
    // remove a class
    $('#continueButton').removeClass("active");
}
WelcomeScreens.setActive = function () {
    // add active class to the element
    $('#continueButton').addClass("active");
    // remove a class
    $('#continueButton').removeClass("inactive");
}