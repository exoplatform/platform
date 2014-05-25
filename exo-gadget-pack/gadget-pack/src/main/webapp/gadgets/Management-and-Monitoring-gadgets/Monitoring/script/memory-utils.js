/* 
 * Convert a number of octets to the appropriate unit
 */
function formatOctets (octets) {
  var K = 1024;
  var M = 1024 * K;
  var G = 1024 * M;
  if (octets < K) {
    return octets + " Bytes";
  } else if (octets < M) {
    return round2decimals(octets / K) + " KB" ;
  } else if (octets < G) {
    return round2decimals(octets / M) + " MB" ;
  } else {
    return round2decimals(octets / G) + " GB";
  }
}

function round2decimals (number) {
  return Math.round(number * 100) / 100;
}
