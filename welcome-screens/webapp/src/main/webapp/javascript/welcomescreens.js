
function validate() {
    var eltAgreement = document.getElementById("agreement");
    if(eltAgreement.checked == true) {
    document.tcForm.submit();
    }
}
function toggleState() {
    var eltAgreement = document.getElementById("agreement");

    if(eltAgreement.checked == false) {
    // Uncheck
    eltAgreement.value = false;
    setInactive();
    }
else {
    // Check
    eltAgreement.value = true;
    setActive();
    }
}
function setInactive() {
    var elt = document.getElementById("continueButton");
    elt.setAttribute("disabled","disabled");
    var classValue = elt.className;
    var newClassValue = classValue.replace("active", "inactive");
    elt.className = newClassValue;
    }
function setActive() {
    var elt = document.getElementById("continueButton");
    var classValue = elt.className;
    elt.removeAttribute("disabled");
    var newClassValue = classValue.replace("inactive", "active");
    elt.className = newClassValue;
    }
