function round2decimals (number) {
  return Math.round(number * 100) / 100;
}

function formatExecTime (number) {
    return $().number_format(number, {numberOfDecimals:2, decimalSeparator: ',',thousandSeparator: ' '});
}
      
function formatInteger (number) {
    return $().number_format(number, {numberOfDecimals:0, decimalSeparator: ',',thousandSeparator: ' '});
}

