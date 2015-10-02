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

    $.ajax({
        url: "/rest/plf/checkConnection",
        beforeSend: function( xhr ) {
            console.log("loading....");
            $(".loading").show();
            $(".plf-registration").hide();
        }
    })
    .done(function( data ) {
        if(data==="true"){
            $(".plf-registration").show();
        }else{
            $("body").append("<div> Error, Could not contact with http://www.exoplatform.com </div>");
        }
        $(".loading").hide();

    });
});