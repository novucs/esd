let { initStripe } = (function() {

  let choosePaymentMethod = (e, type) => {
    console.log("bruh", e, type);
    // Variables
    let tabContent, tabLinks;

    // Get all elements with class="tabContent" and hide them
    tabContent = document.querySelectorAll(".tabcontent");
    tabContent.forEach((e, i) => {
      e.style.display = "none";
    });

    // Get all elements with class="tabLinks" and remove the class "active"
    tabLinks = document.querySelectorAll(".tablinks");
    tabLinks.forEach((e, i) => {
      console.log("Replaced ", e);
      e.className = e.className.replace(" active", "");
    });

    // Swap our viewport
    let isCardPayment = (type === "card-payment");
    document.getElementById("card").style.display =
        (isCardPayment ? 'block' : 'none');
    document.getElementById("offline").style.display =
        (!isCardPayment ? 'block' : 'none');
    e.currentTarget.className += " active";
  };

  function initStripe(amount, reference) {
    let stripe = Stripe('pk_test_b51qKB6WZmKC9I9vmLiKUAgK00zFG7dhQ2');
    // Custom styling can be passed to options when creating an Element.
    let style = {
      base: {
        // Add your base input styles here. For example:
        fontSize: '16px',
        lineHeight: '30px',
        color: "#32325d",
      },
      complete: {
        fontSize: '20px',
        lineHeight: '30px',
        color: "#32325d",
      },
      empty: {
        fontSize: '16px',
        color: "#32325d",
      },
      invalid: {
        fontSize: '20px',
        color: "red",
      }
    };
    let elements = stripe.elements();

    // Create an instance of the card Element.
    let cardNumber = elements.create('cardNumber', {style: style});

    // Add an instance of the card Element into the `card-element` <div>.
    cardNumber.mount('#card-number');

    // Create an instance of the card Element.
    let cardExpiry = elements.create('cardExpiry', {style: style});

    // Add an instance of the card Element into the `card-element` <div>.
    cardExpiry.mount('#card-expiry');

    // Create an instance of the card Element.
    let cardCvc = elements.create('cardCvc', {style: style});

    // Add an instance of the card Element into the `card-element` <div>.
    cardCvc.mount('#card-cvc');

    let stripeTokenHandler = ((token) => {
      // Insert the token ID into the form so it gets submitted to the server
      let form = document.getElementById('stripe-payment-form');

      let amountInput = document.createElement('input');
      amountInput.setAttribute('type', 'hidden');
      amountInput.setAttribute('name', 'amount');
      amountInput.setAttribute('value', amount * 100);
      form.appendChild(amountInput);
      let referenceInput = document.createElement('input');
      referenceInput.setAttribute('type', 'hidden');
      referenceInput.setAttribute('name', 'reference');
      referenceInput.setAttribute('value', reference);
      form.appendChild(referenceInput);

      let hiddenInput = document.createElement('input');
      hiddenInput.setAttribute('type', 'hidden');
      hiddenInput.setAttribute('name', 'stripeToken');
      hiddenInput.setAttribute('value', token.id);
      form.appendChild(hiddenInput);

      // Submit the form
      form.submit();
    });

    // Create a token or display an error when the form is submitted.
    let form = document.getElementById('stripe-payment-form');
    form.addEventListener('submit', function (event) {
      event.preventDefault();

      stripe.createToken(cardNumber).then(function (result) {
        if (result.error) {
          // Inform the customer that there was an error.
          let errorElement = document.getElementById('card-errors');
          errorElement.textContent = result.error.message;
        } else {
          // Send the token to your server.
          stripeTokenHandler(result.token);
        }
      });
    });
  }

  document.addEventListener("DOMContentLoaded", () => {
    let tabLinks = document.querySelectorAll(".tablinks");
    tabLinks.forEach((e, i) => {
      e.addEventListener("click", (e) => choosePaymentMethod(e, e.target.id));
    });
    tabLinks[0].click();
  });

  return {
    initStripe
  };
})();