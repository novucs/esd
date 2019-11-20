function maintainFormat(){
  var inpObj = document.getElementById("claim-value");
  inpObj.value = parseFloat(inpObj.value).toFixed(2);
}