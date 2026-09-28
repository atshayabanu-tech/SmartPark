document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            var message = form.getAttribute('data-confirm') || 'Are you sure?';
            if (!window.confirm(message)) {
                e.preventDefault();
            }
        });
    });

    document.querySelectorAll('.alert').forEach(function (el) {
        setTimeout(function () {
            var alertInstance = bootstrap.Alert.getOrCreateInstance(el);
            if (alertInstance) {
                alertInstance.close();
            }
        }, 6000);
    });
});
