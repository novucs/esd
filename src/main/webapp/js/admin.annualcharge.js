(function(){
  let init = () => {
    document.addEventListener('DOMContentLoaded', function () {
      document.getElementById("range")
        .addEventListener("input", function() {
        const sliderValue = parseInt(document.getElementById("range").value);
        const maxCharge = parseInt(document.getElementById("max-charge").value);
        const updatedLabel = maxCharge * sliderValue / 100;
        console.log(updatedLabel);
        document.getElementById("amount-to-charge").textContent = updatedLabel.toString();
      });
    });
  }

  return {
    init
  };

})().init();

