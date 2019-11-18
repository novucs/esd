(function () {
  document.addEventListener("DOMContentLoaded", () => {
    let password = document.querySelector("input#password1");
    let passwordConfirm = document.querySelector("input#password2");
    if (!password || !passwordConfirm) {
      return;
    }

    // Confirm the user entered password
    passwordConfirm.addEventListener("keyup", (e) => {
      if (password.value === "" || !password.value) {
        password.className = "invalid";
      }
      else {
        password.className = (passwordConfirm.value !== password.value) ?
            "invalid" : "valid";
      }
      passwordConfirm.className = password.className;
    });

    // Initialise Materialize CSS Multi-select
    let elems = document.querySelectorAll('select');
    M.FormSelect.init(elems, {});
  });
})();