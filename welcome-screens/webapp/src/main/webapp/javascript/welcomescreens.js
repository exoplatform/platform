/**
 * Module used to manage cloud login wizard JS features
 *
 */
var WelcomeScreens = {};


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
    $("form:first").submit();
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