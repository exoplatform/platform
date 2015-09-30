$(document).ready(function() {
    var frmSoftwareRegistration = $("#frmSoftwareRegistration");
    var btnContinue = $("input[name=btnContinue]");
    var btnSkip = $("input[name=btnSkip]");
    var btnCompleteRegistration = $("input[name=btnCompleteRegistration]");

    var txtValue = $("input[name=value]");

    btnContinue.click(function(){
        txtValue.val("continue");
        frmSoftwareRegistration.submit();
    });

    btnSkip.click(function(){
        txtValue.val("skip");
        frmSoftwareRegistration.submit();
    });

    btnCompleteRegistration.click(function(){
        txtValue.val("completeRegistration");
        frmSoftwareRegistration.submit();
    });
});