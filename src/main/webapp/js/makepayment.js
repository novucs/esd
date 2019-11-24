function initStripe(amount, reference){
  var stripe = Stripe('pk_test_b51qKB6WZmKC9I9vmLiKUAgK00zFG7dhQ2');
  // Custom styling can be passed to options when creating an Element.
  var style = {
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
  var elements = stripe.elements();

  // Create an instance of the card Element.
  var cardNumber = elements.create('cardNumber', {style: style});

  // Add an instance of the card Element into the `card-element` <div>.
  cardNumber.mount('#card-number');

  // Create an instance of the card Element.
  var cardExpiry = elements.create('cardExpiry', {style: style});

  // Add an instance of the card Element into the `card-element` <div>.
  cardExpiry.mount('#card-expiry');

  // Create an instance of the card Element.
  var cardCvc = elements.create('cardCvc', {style: style});

  // Add an instance of the card Element into the `card-element` <div>.
  cardCvc.mount('#card-cvc');


  function stripeTokenHandler(token) {
    // Insert the token ID into the form so it gets submitted to the server
    var form = document.getElementById('payment-form');

    var amountInput = document.createElement('input');
            amountInput.setAttribute('type', 'hidden');
            amountInput.setAttribute('name', 'amount');
            amountInput.setAttribute('value', amount*100);
            form.appendChild(amountInput);
    var referenceInput = document.createElement('input');
            referenceInput.setAttribute('type', 'hidden');
            referenceInput.setAttribute('name', 'reference');
            referenceInput.setAttribute('value', reference);
            form.appendChild(referenceInput);

    var hiddenInput = document.createElement('input');
    hiddenInput.setAttribute('type', 'hidden');
    hiddenInput.setAttribute('name', 'stripeToken');
    hiddenInput.setAttribute('value', token.id);
    form.appendChild(hiddenInput);

    // Submit the form
    form.submit();
  };

  // Create a token or display an error when the form is submitted.
  var form = document.getElementById('payment-form');
  form.addEventListener('submit', function(event) {
    event.preventDefault();

    stripe.createToken(cardNumber).then(function(result) {
      if (result.error) {
        // Inform the customer that there was an error.
        var errorElement = document.getElementById('card-errors');
        errorElement.textContent = result.error.message;
      } else {
        // Send the token to your server.
        stripeTokenHandler(result.token);
      }
    });
  });
}

