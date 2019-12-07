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

  let generateUsername = ((nameInput) => {
    let usernameInput = document.querySelector("input#username");
    let name = nameInput.value;
    let names = name.split(" ");
    let lastName = names.pop();
    let initials = names.map((e, i) => e.charAt(0)).filter((e) => e.length > 0);

    if (names.length === 0) {
      usernameInput.value = lastName;
    } else if (lastName === "") {
      usernameInput.value = names[0];
    } else {
      usernameInput.value = (initials.join("") + "-" + lastName);
    }
    usernameInput.value = usernameInput.value.toLowerCase();

    updateElement(usernameInput, usernameInput.value);
    updateElement(nameInput, nameInput.value);
  });

  document.addEventListener('DOMContentLoaded', () => {
    let lookupAddressBtn = document.querySelector("#lookup-btn");
    let fullNameInput = document.querySelector("input#full-name");

    if (lookupAddressBtn) {
      lookupAddressBtn.addEventListener('click', (e) => {
        let postCode = document.querySelector("#address-postcode");
        getAddress(postCode.value);
      });
    }

    if (fullNameInput) {
      fullNameInput.addEventListener('keyup', (e) => {
        generateUsername(fullNameInput);
      });
    }
  });
})();