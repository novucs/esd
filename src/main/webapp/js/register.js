(function() {
  let getAddress = ((postCode) => {
    if (postCode === "") {
      alert("Please enter a valid Postal Code!");
      return;
    }
    
    // Elements
    let adrStreet = document.querySelector("#address-street");
    let adrCity = document.querySelector("#address-city");
    let adrCounty = document.querySelector("#address-county");
    let adrPostCode = document.querySelector("#address-postcode");
    
    let request = new XMLHttpRequest();
    postCode = postCode.replace(/\s/g, ""); // Remove whitespaces
    request.open("GET", "./api/address?pc=" + postCode, true);
    request.send();
    request.onload = (() => {
      try {
        let json = JSON.parse(request.responseText);
      
        if (json) {
          updateElement(adrStreet, json.street);
          updateElement(adrCity, json.town);
          updateElement(adrCounty, json.county);
          updateElement(adrPostCode, json.postcode);
        }
      } catch (e) {
        alert("Unable to retrieve Post Code details.");
      }
    });
  });
  
  let updateElement = ((ele, text) => {
    ele.value = text;
    ele.focus();
  });
  
  document.addEventListener('DOMContentLoaded', () => {
    let lookupAddressBtn = document.querySelector("#lookup-btn");
    lookupAddressBtn.addEventListener('click', (e) => {
      let postCode = document.querySelector("#address-postcode");
      getAddress(postCode.value);
    });
  });
})();