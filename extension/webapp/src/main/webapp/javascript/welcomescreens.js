/**
 * Module used to manage cloud login wizard JS features
 *
 */
var WelcomeScreens = {};

WelcomeScreens.validateTermsAndCondition = function(event) {
    if(event != undefined) {
        event.preventDefault();
    }
    $('#TermsAndCondition').fadeToggle("slow", "linear");
    /**$('#wrapper').scrollTo('#AccountSetup', 800);*/
    return false;
}
WelcomeScreens.exit = function() {
    document.tcForm.submit();
}