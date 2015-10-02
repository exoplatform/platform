$(document).ready(function() {
    var frmSoftwareRegistration = $("#frmSoftwareRegistration");
    var btnContinue = $("input[name=btnContinue]");
    var btnSkip = $("input[name=btnSkip]");
    var btnCompleteRegistration = $("input[name=btnCompleteRegistration]");

    var txtValue = $("input[name=value]");

    btnContinue.click(function(){
        if(txtValue.val()===undefined || txtValue.val()==="") {
            txtValue.val("continue");
        }
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
            $(".loading-text").show();
            //$(".plf-registration").hide();
        }
    })
    .done(function( data ) {
        if(data==="true"){
            $(".plf-registration").show();
        }else{
            $(".plf-registration .signin-title").hide();
            $(".plf-registration .registrationURL").hide();
            $(".plf-registration input[name=btnSkip]").hide();
            $(".plf-registration input[name=btnContinue]").removeAttr("disabled");
            $(".plf-registration input[name=value]").val("notReacheble");

        }
        $(".loading-text").hide();

    });
});