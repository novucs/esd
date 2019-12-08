(function(){
  let init = () => {
    document.addEventListener('DOMContentLoaded', function () {
      document.getElementById("percentage-charge")
        .addEventListener("input", function() {
        const sliderValue = document.getElementById("percentage-charge").value;
        const maxCharge = document.getElementById("slider-form").getAttribute("data-max-charge");
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

