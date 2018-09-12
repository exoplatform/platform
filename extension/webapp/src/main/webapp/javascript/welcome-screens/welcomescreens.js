$(document).ready(function() {
    $('#userNameAccount').change(function() {
        WelcomeScreens.USERNAME_EXIST = false;
        var usernameOnBlur = $.trim($("#userNameAccount").val());
        if(usernameOnBlur != "") {
            $.get("/portal/rest/welcomeScreen/checkUsername", { "username": usernameOnBlur }, function(data) {
            	// Remove old message when user change the text in the input field
            	$("#usernameExistErrorId").remove();
            	var userExists = data.userExists;
                if(userExists == true) {
                	$(".createAccountError").remove();
                    WelcomeScreens.USERNAME_EXIST = true;
                    WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
                    $('#usernameId').before('<div id="usernameExistErrorId"><div  class ="accountSetupError"><i class="uiIconError"></i>Username already exists.</div></div>');
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
// Allow hyphen, single quote, spaces and all alpha characters (using unicode values for accented characters)
WelcomeScreens.FIRSTNAME_REGEXP = new RegExp(/^[a-zA-Z-' \u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF]+$/);
WelcomeScreens.LASTNAME_REGEXP = new RegExp(/^[a-zA-Z-' \u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF]+$/);
WelcomeScreens.FormatError = 'Only lowercase letters, digits, dot and underscore characters are allowed for the field "User Name".';

//Modify by SONDN PLF August 16, 2013
WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT = 0;
WelcomeScreens.IsNullOrEmpty = function(){};
WelcomeScreens.IsAllFieldNullOrEmpty = function(){};
WelcomeScreens.FieldsRequired = function(){};

WelcomeScreens.exit = function() {
    WelcomeScreens.ACCOUNT_SETUP_ERROR = false;
    WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT = 0;
    
    $(".createAccountError").remove();
    $('.createAdminAccountError').remove();
    var username = $.trim($("#userNameAccount").val());
    var usernameReg = $.trim($("#usernameRegExpid").val());
    if(usernameReg != "") {
        WelcomeScreens.USERNAME_REGEXP = new RegExp(usernameReg);
        WelcomeScreens.FormatError = $.trim($("#formatMsgid").val());
    }
    var max = $.trim($("#usernameMaxLengthid").val());
    var min = $.trim($("#usernameMinLengthid").val());
    if(min != 0) {
    	WelcomeScreens.USERNAME_INPUT_MIN_SIZE = min;
    }
    if(max != 0) {
    	WelcomeScreens.USERNAME_INPUT_MAX_SIZE = max;
    }
    var firstname = $.trim($("#firstNameAccount").val());
    var lastname = $.trim($("#lastNameAccount").val());
    var email = $.trim($("#emailAccount").val());
    var password = $.trim($("#userPasswordAccount").val());
    var confirmPassword = $.trim($("#confirmUserPasswordAccount").val());
    var adminPassword = $.trim($("#adminPassword").val());
    var confirmAdminPassword = $.trim($("#confirmAdminPassword").val());
    
    // Modify by SONDN PLF August 16, 2013
    WelcomeScreens.IsNullOrEmpty = function() {
    	return username && firstname && lastname && email && password && confirmPassword ? false : true;
    };
    WelcomeScreens.IsAllFieldNullOrEmpty = function() {
    	return (username === '' && firstname === '' && lastname === '' && email === '' && password === '' && confirmPassword === '') ? true : false;
    };
    WelcomeScreens.FieldsRequired = function() {
        if (WelcomeScreens.IsNullOrEmpty() && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
        	$('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconWarning"></i>Please input all fields.</div></div>');
        	WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        	WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        }
    };
    
    // Check all field first time
    if (WelcomeScreens.IsAllFieldNullOrEmpty()) {
    	$('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconWarning"></i>Please input all fields.</div></div>');
    	WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    	WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 2;
    }
    
    if ((username != "") && WelcomeScreens.USERNAME_EXIST == false && (WelcomeScreens.USERNAME_REGEXP.test(username) == false) && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
        $('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>' + WelcomeScreens.FormatError + '</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        WelcomeScreens.FieldsRequired();
    }
    
    if (username != "" && WelcomeScreens.USERNAME_EXIST == false && (username.length < WelcomeScreens.USERNAME_INPUT_MIN_SIZE || username.length > WelcomeScreens.USERNAME_INPUT_MAX_SIZE) && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
    	$('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>The length of Username must be between '+ WelcomeScreens.USERNAME_INPUT_MIN_SIZE +' and ' +WelcomeScreens.USERNAME_INPUT_MAX_SIZE+ ' characters.</div></div>');
    	WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        WelcomeScreens.FieldsRequired();
    }
    
    if ((firstname != "") && WelcomeScreens.USERNAME_EXIST == false && WelcomeScreens.FIRSTNAME_REGEXP.test(firstname) == false && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
        $('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>Only letters, spaces, hyphen or apostrophe are allowed for the field First Name.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        WelcomeScreens.FieldsRequired();
    }

    if (firstname != "" && WelcomeScreens.USERNAME_EXIST == false && ((firstname.length < WelcomeScreens.FIRSTLASTNAME_INPUT_MIN_SIZE) || (firstname.length > WelcomeScreens.FIRSTLASTNAME_INPUT_MAX_SIZE)) && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
        $('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>The length of First Name must be between '+ WelcomeScreens.FIRSTLASTNAME_INPUT_MIN_SIZE +' and ' + WelcomeScreens.FIRSTLASTNAME_INPUT_MAX_SIZE + ' characters.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        WelcomeScreens.FieldsRequired();
    }
    
    if ((lastname != "") && WelcomeScreens.USERNAME_EXIST == false && (WelcomeScreens.LASTNAME_REGEXP.test(lastname) == false) && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
        $('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>Only letters, spaces, hyphen or apostrophe are allowed for the field Last Name.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        WelcomeScreens.FieldsRequired();
    }

    if (lastname != "" && WelcomeScreens.USERNAME_EXIST == false && ((lastname.length < WelcomeScreens.FIRSTLASTNAME_INPUT_MIN_SIZE) || (lastname.length > WelcomeScreens.FIRSTLASTNAME_INPUT_MAX_SIZE)) && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
        $('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>The length of Last Name must be between '+ WelcomeScreens.FIRSTLASTNAME_INPUT_MIN_SIZE +' and ' + WelcomeScreens.FIRSTLASTNAME_INPUT_MAX_SIZE + ' characters.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        WelcomeScreens.FieldsRequired();
    }
    
    if(email != "" && WelcomeScreens.USERNAME_EXIST == false && WelcomeScreens.EMAIL_REGEXP.test(email) == false && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
        $('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>Please input a valid email format such as yourname@example.com.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        WelcomeScreens.FieldsRequired();
    }

    if (password != "" && WelcomeScreens.USERNAME_EXIST == false && (password.length < WelcomeScreens.PASSWORD_INPUT_MIN_SIZE || password.length > WelcomeScreens.PASSWORD_INPUT_MAX_SIZE) && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
        $('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>The length of Password must be between 6 and 30 characters.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        WelcomeScreens.FieldsRequired();
    }
    
    if ((password != "") && WelcomeScreens.USERNAME_EXIST == false && (confirmPassword != "") && (password != confirmPassword) && WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT < 2) {
    	$('#usernameId').before('<div class="createAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>Password does not match the Confirm password.</div></div>');
    	WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
        WelcomeScreens.ACCOUNT_SETUP_ERROR_COUNT += 1;
        WelcomeScreens.FieldsRequired();
    }
    
    // Ask again
    WelcomeScreens.FieldsRequired();

    if (adminPassword === "" && confirmAdminPassword === "") {
    	$('#adminUsernameId').before('<div class="createAdminAccountError"><div class ="accountSetupError"><i class="uiIconWarning"></i>Please input all fields.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    } else if (adminPassword.length < WelcomeScreens.PASSWORD_INPUT_MIN_SIZE || adminPassword.length > WelcomeScreens.PASSWORD_INPUT_MAX_SIZE) {
    	$('#adminUsernameId').before('<div class="createAdminAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>The length of Password must be between 6 and 30 characters.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }

    //check Admin Password
    if ((adminPassword != "" && confirmAdminPassword === "") || (adminPassword === "" && confirmAdminPassword != "")) {
    	$('#adminUsernameId').before('<div class="createAdminAccountError"><div class ="accountSetupError"><i class="uiIconWarning"></i>Please input all fields.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }
    
    if ((adminPassword != "") && (confirmAdminPassword != "") && (adminPassword != confirmAdminPassword)) {
    	$('#adminUsernameId').before('<div class="createAdminAccountError"><div class ="accountSetupError"><i class="uiIconError"></i>Password does not match the Confirm password.</div></div>');
        WelcomeScreens.ACCOUNT_SETUP_ERROR = true;
    }

    if ((WelcomeScreens.ACCOUNT_SETUP_ERROR == false) && (WelcomeScreens.USERNAME_EXIST == false)) {
        $('#AccountSetup1').css("display", "none");
        $('#Greetings').css("display", "block");
    }
}


