const errorModule = (function() {

    const displayError = (errorText) => {
        M.toast({
            html: errorText,
            classes: 'red darken-4',
            displayLength: 2500
        });
    };

    return {
        displayError
    };
})();