var startFrom = 0;
var pageLength = 25;
var reset = 0;
var sortField = "creationTime";
var sortDirection = "asc";
var id = $("#eventId").val();
var ALERT_DISPLAY_TIME = 1500;
var timeoutId;

$(document).ready(function () {
    initEvents();
    getAllParticipants();
});

var vueListParticipants = new Vue({
    el: "#update-score-list",
    data: {
        records: [],
        scoreType: 0,
        recordsFiltered: 0,
    },
    beforeUpdate: function () {

    },
    updated: function () {
        this.$nextTick(function () {
            initEvents();
            initFormValidation();
        });
    }
    , mounted: function () {
        $("#list-records .no-records-found").hide();
        $("#list-records .loading-records").show();
    }
});
function validateTime(inputTime) {
    // Regular expression to match hh:mm:ss format
    var regex = /^(?:[01]\d|2[0-3]):(?:[0-5]\d):(?:[0-9]\d)\d{1,3}$/;

    // Check if the input matches the regular expression
    if (regex.test(inputTime)) {
        // Split the time string into hours, minutes, and seconds
        var parts = inputTime.split(":");
        var hours = parseInt(parts[0], 10);
        var minutes = parseInt(parts[1], 10);
        var secondsAndMilliseconds = parts[2];

        var seconds = parseInt(secondsAndMilliseconds.slice(0, 2), 10);
        var milliseconds = parseInt(secondsAndMilliseconds.slice(2), 10);

        // Check if hours, minutes, and seconds are within valid ranges
        if (hours < 24 && minutes < 60 && secondsAndMilliseconds < 1000) {
            return true; // Time format and values are valid
        }
    }

    return false; // Time format or values are invalid

}

function initEvents(){
    $(".score-input").keyup(function (){
        // var isValid = 0;
        var participantId = $(this).attr("participantId");
        var scoreId = null;
        $.each(vueListParticipants.records, function (i, val) {
            if(val.id == participantId){
                scoreId = val.eventScoreId;
            }

        });
        var formdata = {
            "id":  scoreId,
            "participantId": participantId,
            "eventId": id,
        }
        if(vueListParticipants.scoreType==0){
            formdata["score"] = $(this).val()
        }else{
            formdata["time"] = $(this).val()
        }
        console.log(formdata);
        clearTimeout(timeoutId);

        // Set a new timeout for the update

        timeoutId = setTimeout(function () {
            if(vueListParticipants.scoreType==0) {
                updateScore(formdata)
            }else{
                if (validateTime($(this).val())) {
                    $(this).parent().parent().parent().find(".append-validation-error").hide();
                    updateScore(formdata)
                } else {
                    $(this).parent().parent().parent().find(".append-validation-error").css("display", "flex");
                    $(this).parent().parent().parent().find(".append-validation-error").show();
                }
            }
        }.bind(this), 1000);

        // getByParticipantsIdAndEventId($(this).attr("participantId"), eventId,$(this).val())
    });

    $("#btn-create-new-participants").unbind("click").on("click",function (){
        $("#modal-add-participant-pop-up").modal().show();
    });

    $(".cancel-btn").click(function () {
        $("#modal-add-participant-pop-up").modal().hide();
        resetForm();
    });

    $(".reset-btn").click(function (){
        resetForm();
    });

}

function resetForm(){
    $("#name").val("");
    $("#age").val("");
}

function initFormValidation(){
    $("#add-participant-form").validate({
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
            name:{
                required: true,
            },
            age:{
                required: true,
            }
        },
        messages: {
            name: {
                required: "Please enter name"
            },
            age:{
                required: "Please enter age",
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
            formdata["eventId"] = id;
            saveParticipantsToEvents(formdata);
            // getPagedPendingOrders(true, 0, listPageLength);
        }
    });
}

function getAllParticipants(){
    $.ajax({
        url: '/event-participants/get-event-participants-by-event-id',
        type: 'POST',
        dataType: "json",
        data: {
            start: startFrom,
            length: pageLength,
            order: [{
                "column": sortField,
                "dir": sortDirection
            }],
            sort : sortField,
            eventId: id,
        },
        success: function (response) {
            document.body.className = "";
            $(".loading-records").hide();
            if (response.success) {
                vueListParticipants.records = [];
                $(".loading-records").hide();
                $(".supplier-order-card").show();
                vueListParticipants.recordsFiltered = response.recordsFiltered;
                if(response.data.length>0) {
                    var scoreType = response.data[0].eventScoreType;
                    $.each(response.data, function (i, val) {
                        if (scoreType == 1 && val.eventTime == null) {
                            val.eventTime = "00:00:000"
                        }
                        vueListParticipants.records.push(val);
                    });
                    vueListParticipants.scoreType = response.data[0].eventScoreType;
                }
                if (vueListParticipants.records.length < 1) {
                    $(".no-records-found").show();
                }else{
                    $(".no-records-found").hide();
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
                confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                text: data.d,
                type: "error"
            });
        }
    });
}

function saveParticipantsToEvents(formdata){
    $("#modal-add-participant-pop-up").modal().hide();
    $.ajax({
        url: '/event-participants/save',
        type: "POST",
        dataType: "json",
        data: formdata,
        success: function (data) {
            document.body.className = "";
            $("#add-participant-form [type='submit']").prop("disabled", false);
            if(data.success){
                Swal.fire({
                    title: 'Success',
                    text: 'Saved successfully',
                    type: "Success",
                    allowOutsideClick: false,
                    confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                    timer: 1500,
                    preConfirm: () => {
                        $("#modal-add-participant-pop-up").modal().hide();
                    }
                });
                getAllParticipants();
                resetForm();

            } else {
                Swal.fire({
                    title: "Something went wrong",
                    text: data.error,
                    confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                    type: "error"
                });
            }
        },
        error: function (data) {
            document.body.className = "";
            $("#role-form [type='submit']").prop("disabled", false);
            Swal.fire({
                title: "Something went wrong",
                confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                text: data.d,
                type: "error"
            });
        }
    });
}

function getByParticipantsIdAndEventId(participantId,eventId,score){
    $.ajax({
        url: '/event-scores/get-event-score-by-event-id-and-participant-id',
        type: 'POST',
        dataType: "json",
        data: {
            start: 0,
            length: 100,
            order: [{
                "column": "asc",
                "dir": "creationTime"
            }],
            sort : "creationTime",
            eventId: id,
            participantId: participantId
        },
        success: function (response) {
            document.body.className = "";
            $(".loading-records").hide();
            var formdata = {
                "eventId": id,
                "participantId": participantId,
                "score": score
            }
            if (response.success) {
               if(response.data != null){
                   formdata["id"] = response.data[0].id;
               }
            }
            updateScore(formdata)

        },
        error: function (data) {
            document.body.className = "";
            $("#supplier-articles .loading-records").hide();
            Swal.fire({
                title: "Something went wrong",
//                showCloseButton: true,
//                closeButtonHtml: '<img class="pop-up-close-icon" src="/v1.1/images/icons/article-card/article-card-close.svg" width="30px" height="30px"></img>',
                confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                text: data.d,
                type: "error"
            });
        }
    });
}

function updateScore(formdata){
    $.ajax({
        url: '/event-scores/save',
        type: "POST",
        dataType: "json",
        data: formdata,
        success: function (data) {
            document.body.className = "";
            $("#add-participant-form [type='submit']").prop("disabled", false);
            if(data.success){
                // Swal.fire({
                //     title: 'Success',
                //     text: 'Saved successfully',
                //     type: "Success",
                //     allowOutsideClick: false,
                //     timer: 1500,
                //     icon: 'success',
                //     preConfirm: () => {
                //         $("#modal-add-participant-pop-up").modal().hide();
                //     }
                // });
                $.each(vueListParticipants.records, function (i, val) {
                    if(val.id == data.eventScoresDTO.participantId){
                        vueListParticipants.records[i].eventScoreId = data.eventScoresDTO.id;
                        vueListParticipants.records[i].eventScores = data.eventScoresDTO.score;
                        vueListParticipants.records[i].eventTime = data.eventScoresDTO.time;
                    }
                });
                resetForm();

            } else {
                Swal.fire({
                    title: "Something went wrong",
                    text: data.error,
                    confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                    type: "error"
                });
            }
        },
        error: function (data) {
            document.body.className = "";
            $("#role-form [type='submit']").prop("disabled", false);
            Swal.fire({
                title: "Something went wrong",
                text: data.d,
                confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                type: "error"
            });
        }
    });
}