$(document).ready(function (e){
    initEvents();
    initFormValidation();

})

function initEvents(){
    $(".toggle-password").click(function() {
        $(this).parent().find(".toggle-password").toggleClass("fa-eye fa-eye-slash");
        var input = $("#password");
        if (input.attr("type") == "password") {
            input.attr("type", "text");
        } else {
            input.attr("type", "password");
        }
    });

}

function initFormValidation(){
    $("#form").validate({
        ignore: [],
        errorClass: "field-validation-error",
        errorElementClass: 'form-control-danger',
        errorElement: "div",
        highlight: function (element) {
            var elem = $(element);
            if (elem.hasClass("select2-hidden-accessible")) {
                $("#select2-" + elem.attr("id") + "-container").parent().removeClass('form-control-success').addClass('form-control-danger');
            } else {
                $(element).removeClass('form-control-success').addClass('form-control-danger');
            }
        },
        unhighlight: function (element) {
            var elem = $(element);
            if (elem.hasClass("select2-hidden-accessible")) {
                $("#select2-" + elem.attr("id") + "-container").parent().removeClass('form-control-danger').addClass('form-control-success');
            } else {
                $(element).removeClass('form-control-danger').addClass('form-control-success');
            }
        },
        success: function (element) {
            $(element).removeClass('form-control-danger').addClass('form-control-success');
        },
        errorPlacement: function (error, element) {
            error.appendTo($(element).closest(".form-group"));
        },
        rules: {
            username: {
                required: true,
            },
            password: {
                required: true,
            },
        },
        messages: {
            username: {
                required: "User name is required"
            },
            password: {
                required: "Paasword is required"
            },
        },
        onfocusout: function(element) {
            element.value = $.trim(element.value);
            this.element(element);
        },
        submitHandler: function (form) {
            form.submit();
        }
    });
}