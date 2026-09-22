Dropzone.autoDiscover = false;
var dzLogo;
var logoPath;
var id = $("#id").val();
var startDate = new Date();
var endDate = new Date();
var isEditForm = 0;
$(document).ready(function () {
    initEvents();
    initFormValidation();
    initComponent();
    if (id != '') {
        getEvent();
        isEditForm = 1;
    }

    // getPagedEvents(reset,startFrom,pageLength);
})

function formatDate(value) {
    if (value) {
        return moment((value)).format(globalDateFormat);
    }
}

var globalDateFormat = "YYYY-MM-DD";

Vue.filter('formatDate', function (value) {
    if (value) {
        return moment(String(value)).format(globalDateFormat);
    }
})

function initEvents() {


    $("#btn-select-order-status .dropdown-menu a").click(function () {
        $("#btn-select-order-status .dropdown-menu a").removeClass("active");
        $(this).addClass("active");
        $(this).parent().parent().find(".current-selection").attr("value", $(this).attr('value'));
        $(this).parent().parent().find(".current-selection").text($(this).text())
    });

    $(".cancel-btn").unbind("click").on("click", function () {
            document.location = "/events/list"
            resetForm();
    });
}

function resetForm() {
    $("#eventName").val("");
    $("#description").val("");
    $("#startDate").val("");
    $("#endDate").val("");
    $(".current-selection").attr("value", -1);
    $(".current-selection").text("");
    $("#primaryColor").val("");
    $("#logoPath").val("");
    logoPath = null;
    $("#isLive").prop("checked", false);

}

function initComponent() {
    $("#eventStartDate").datetimepicker({
        pickTime: false,
        minuteStep: 15,
        pickerPosition: 'bottom-left',
        format: 'yyyy-MM-dd',
        startDate: new Date(Date.now()),
        autoclose: true,
        showMeridian: true,
        forceParse: false,
        startView: 1,
        maxView: 0,
        pick12HourFormat: false,
    }).on("changeDate", function (e) {
        $("#eventStartDate").datetimepicker('hide');
        // $("#orderStatusDate-error").text("")
    });

    $("#eventEndDate").datetimepicker({
        pickTime: false,
        minuteStep: 15,
        pickerPosition: 'bottom-left',
        format: 'yyyy-MM-dd',
        startDate: new Date(Date.now()),
        autoclose: true,
        showMeridian: true,
        forceParse: false,
        startView: 1,
        maxView: 0,
        pick12HourFormat: false,
    }).on("changeDate", function (e) {
        $("#eventEndDate").datetimepicker('hide');
        // $("#orderStatusDate-error").text("")
    });

    dzLogo = new Dropzone("div#import-logo-photo", {
        url: "/events/upload-logo"
        , autoQueue: false
        , maxFiles: 1
        , addRemoveLinks: true
        , maxFilesize: 10 //10MB
        , acceptedFiles: "image/*"
        , init: function () {

        }
    }).on("thumbnail", function (file) {
        console.log('Creating Thumbnail!');
    }).on("success", function (file, response) {
        console.log("Success");
        if (response[0].success) {
            logoPath = response[0].excelUploadPath;
        }
        console.log(logoPath)
    }).on("addedfile", function (file, response) {
        console.log('Attachment is added to upload queue!');
        dzLogo.processQueue();
    }).on("maxfilesexceeded", function (file, response) {
    }).on("uploadprogress", function (file, response) {
        console.log('Attachment is getting uploaded to server!');
    }).on("sending", function (file, response) {
        console.log('Attachment is getting uploaded to server!');
    }).on("complete", function (file, response) {
        console.log('Attachment is uploaded to server!');
    }).on("canceled", function (file, response) {
        console.log('Attachment uploaded was stopped!');
    }).on("totaluploadprogress", function (progress) {

    }).on("sending", function (file, xhr, formData) {
    });
}

function initFormValidation() {
    $("#create-events-form").validate({
        errorClass: "field-validation-error",
        errorElementClass: 'form-control-danger',
        errorElement: "div",
        highlight: function (element) {

        },
        unhighlight: function (element) {

        },
        success: function (element) {

        },
        errorPlacement: function (error, element) {
            error.appendTo($(element.parent().parent()).find(".append-validation-error"));
        },
        rules: {
            eventName: {
                required: true,
            },
            primaryColor: {
                required: true,
            }
        },
        messages: {
            eventName: {
                required: "Event name is required"
            },
            primaryColor: {
                required: "Primary color is required",
            }
        },
        onfocusout: function (element) {
            element.value = $.trim(element.value);
            this.element(element);
        },
        submitHandler: function (form) {
            var formfields = $(form).serializeArray();
            var formdata = {};
            $.each(formfields, function (i, v) {
                formdata[v.name] = (v.value).trim();
            });
            if ($("#isLive").prop("checked")) {
                formdata["isLive"] = 1;
            } else {
                formdata["isLive"] = 0;
            }
            formdata["scoreType"] = $(".current-selection").attr("value")
            startDate = $("#startDate").val();
            endDate = $("#endDate").val();
            formdata["logoPath"] = logoPath;
            if (id != '') {
                formdata["id"] = id;
            }
            console.log(formdata);
            saveEvents(formdata);
            // getPagedPendingOrders(true, 0, listPageLength);
        }
    });

}

function saveEvents(formdata) {
    $("#modal-add-participant-pop-up").modal().hide();
    $.ajax({
        url: '/events/save',
        type: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify(formdata),
        success: function (data) {
            document.body.className = "";
            $("#create-events-form [type='submit']").prop("disabled", false);
            if (data.success) {
                Swal.fire({
                    title: 'Success',
                    text: 'Saved successfully',
                    type: "Success",
                    confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px">',
                    allowOutsideClick: false,
                    timer: 1500,
                });
                document.location = "/events/list"
                // resetForm();

            } else {
                Swal.fire({
                    title: "Something went wrong",
                    text: data.error,
                    type: "error",
                    confirmButtonText: '<img src="/v1.1/images/icons/article-card/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                });
            }
        },
        error: function (data) {
            document.body.className = "";
            $("#role-form [type='submit']").prop("disabled", false);
            Swal.fire({
                title: "Something went wrong",
                text: data.d,
                type: "error",
                confirmButtonText: '<img src="/v1.1/images/icons/article-card/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
            });
        }
    });
}

function getEvent() {
    $(".loading-records").show();
    $(".no-records-found").hide();
    document.body.className = "loading";

    $.ajax({
        url: '/events/get',
        type: 'POST',
        dataType: "json",
        data: {
            id: id,
        },
        success: function (response) {
            document.body.className = "";
            $(".loading-records").hide();
            if (response.success) {
                $("#eventName").val(response.data.eventName);
                $("#eventType").val(response.data.eventType);
                $("#description").val(response.data.description);
                $("#startDate").val(formatDate(response.data.startDate));
                $("#endDate").val(formatDate(response.data.endDate));
                $(".current-selection").attr("value", response.data.scoreType);
                if (response.data.scoreType == 0) {
                    $(".current-selection").text("Points");
                } else {
                    $(".current-selection").text("Time");
                }
                $("#primaryColor").val(response.data.primaryColor);
                $("#logoPath").val(response.data.logoPath);
                logoPath = response.data.logoPath;
                if(logoPath!=null){
                    $(".icon-uploaded").show();
                    $(".icon-not-uploaded").hide();
                    $(".uploaded-logo").attr("src",logoPath);
                }
                if (response.data.isLive) {
                    $("#isLive").prop("checked", true);
                } else {
                    $("#isLive").prop("checked", false);
                }
            } else {
                document.body.className = "";
            }
        },
        error: function (data) {
            document.body.className = "";
            $("#supplier-articles .loading-records").hide();
            Swal.fire({
                title: "Something went wrong",
//                showCloseButton: true,
//                closeButtonHtml: '<img class="pop-up-close-icon" src="/v1.1/images/icons/article-card/article-card-close.svg" width="30px" height="30px"></img>',
                confirmButtonText: '<img src="/v1.1/images/icons/article-card/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                text: data.d,
                timer: ALERT_DISPLAY_TIME,
                type: "error"
            });
        }
    });
}