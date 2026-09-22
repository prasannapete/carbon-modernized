var startFrom = 0;
var pageLength = 25;
var reset = 0;
var sortField = "creationTime";
var sortDirection = "asc";
var eventId = null;
var ALERT_DISPLAY_TIME = 1500;
var excelPageLength = 30;

$(document).ready(function () {
     initEvents();
    getPagedEvents(reset,startFrom,pageLength);
});

function leadPagination( pageSize){
    excelPageLength = pageSize;
}

function initEvents(){
    $("#btn-create-new-event").unbind("click").on("click",function (){
        document.location = "/events/create-event";
    })

    $(".items-load-more").unbind("click").on("click", function () {
        if (vueListRecords.records.length < vueListRecords.recordsFiltered) {
            startFrom = startFrom + pageLength;
            getPagedEvents(false, startFrom, pageLength);
        }
    });
 $(".btn-add-participant").click(function () {
        document.location = "/event-participants/new-participant/"+$(this).attr("eventId");
  });

 $(".btn-download-excel").click(function () {
     var eventId = $(this).attr("eventId")
     var scoreType = $(this).attr("scoreType")
     var excelSortDir;
     var excelSort = "score";
     if (scoreType == 0) {
         excelSort = "score";
         excelSortDir = "desc"
     } else {
         excelSort = "time";
         excelSortDir = "asc"
     }
     $.ajax({
         url: '/event-scores/export-score-to-excel',
         type: "POST",
         dataType: "json",
         data: {
             eventId: eventId,
             order: excelSortDir,
             sort: excelSort,
             scoreType: scoreType,
             length: excelPageLength,
         },
         success: function (response) {
             if (response.success) {
                 window.location.href=response.excelUploadPath;
             } else {

             }
         },
         error: function (data) {
             Swal.fire({
//                showCloseButton: true,
//                closeButtonHtml: '<img class="pop-up-close-icon" src="/v1.1/images/icons/article-card/article-card-close.svg" width="30px" height="30px"></img>',
                 confirmButtonText: '<img src="/v1.1/images/icons/article-card/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                 title: "Something went wrong!",
                 text: data.d,
                 timer: 1500,
                 type: "error"
             });
         }
     });
  });
  $(".cancel-btn").click(function () {
      $("#modal-add-participant-pop-up").modal().hide();
      resetForm();
  });

  $(".delete-event").unbind("click").on("click",function (){
      var eventId = $(this).attr("eventId")
      $("#modal-delete-pop-up").modal('show');

      $('.yes-delete-it').unbind("click").on("click", function (e) {
          deleteEvent(eventId);
          $("#modal-delete-pop-up").modal('hide');
      });

  })

  $(".reset-btn").click(function (){
      resetForm();
  });

  $(".btn-update-score").click(function () {
       document.location = "/events/update-score/"+$(this).attr("eventId");
  });
  $(".update-score-section-add-btn").click(function () {
  $("#modal-update-score-pop-up").modal().hide();
     $("#modal-add-participant-pop-up").modal().show();
  });

  $(".edit-event").unbind("click").on("click",function (){
      document.location = "/events/create-event/"+$(this).attr("eventId");
  })

$("#btn-event-name .dropdown-menu a").unbind("click").click(function () {
    $("#btn-event-name .dropdown-menu a").removeClass("active");
    $(this).addClass("active");
    sortField = "eventName";
    sortDirection = $(this).attr('data-order');
    getPagedEvents(true, 0, pageLength ,sortField, sortDirection);
});

$("#btn-start-date .dropdown-menu a").unbind("click").click(function () {
    $("#btn-start-date .dropdown-menu a").removeClass("active");
    $(this).addClass("active");
    sortField = "startDate";
    sortDirection = $(this).attr('data-order');
    getPagedEvents(true, 0, pageLength ,sortField, sortDirection);
});

$("#btn-end-date .dropdown-menu a").unbind("click").click(function () {
    $("#btn-end-date .dropdown-menu a").removeClass("active");
    $(this).addClass("active");
    sortField = "endDate";
    sortDirection = $(this).attr('data-order');
    getPagedEvents(true, 0, pageLength ,sortField, sortDirection);
});

$("#is-live .dropdown-menu a").unbind("click").click(function () {
    $("#is-live .dropdown-menu a").removeClass("active");
    $(this).addClass("active");
    sortField = "isLive";
    sortDirection = $(this).attr('data-order');
    getPagedEvents(true, 0, pageLength ,sortField, sortDirection);
});

}
var globalDateFormat = "YYYY-MM-DD";

Vue.filter('formatDate', function (value) {
    if (value) {
        return moment(String(value)).format(globalDateFormat);
    }
})

var vueListRecords = new Vue({
    el: "#list-records",
    data: {
        records: [],
        recordsFiltered: 0,
        recordsTotal: 0,
    },
    beforeUpdate: function () {

    },
    updated: function () {
        this.$nextTick(function () {
            initEvents();
            initFormValidation();
            if (vueListRecords.records.length >= vueListRecords.recordsFiltered) {
                $(".items-load-more").hide();
            } else {
                $(".items-load-more").show();
            }
        });
    }
    , mounted: function () {
        $("#list-records .no-records-found").hide();
        $("#list-records .loading-records").show();
    }
});

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
            formdata["eventId"] = eventId;
            saveParticipantsToEvents(formdata);
            // getPagedPendingOrders(true, 0, pageLength);
        }
    });
}

function getPagedEvents(resetList, startFrom, pageLength ,sortField, sortDirection) {
    if (resetList) {
        startFrom = 0;
        vueListRecords.records = [];
    }
    $(".loading-records").show();
    $(".no-records-found").hide();
    document.body.className = "loading";

    $.ajax({
        url: '/events/get-events',
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
            search: {
                "value": "",
                "regex": false
            },
        },
        success: function (response) {
            document.body.className = "";
            $(".loading-records").hide();
            if (resetList) {
                vueListRecords.records = [];
            }
            if (response.success) {
                $(".loading-records").hide();
                $(".supplier-order-card").show();
                vueListRecords.recordsFiltered = response.recordsFiltered;
                vueListRecords.recordsTotal = response.recordsTotal;
                $.each(response.data.data, function (i, val) {
                    vueListRecords.records.push(val);
                });
                if (vueListRecords.records.length < 1) {
                    $(".no-records-found").show();
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
                timer: ALERT_DISPLAY_TIME,
                type: "error"
            });
        }
    });
}

function resetForm(){
    $("#name").val("");
    $("#age").val("");
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
                    confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                    allowOutsideClick: false,
                    // timer: 1500,
                    preConfirm: () => {
                        $("#modal-add-participant-pop-up").modal().hide();
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
                confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                text: data.d,
                type: "error"
            });
        }
    });
}

function deleteEvent(id) {
    $.ajax({
        url: '/events/trash',
        type: 'POST',
        data: {
            "id": id
        },
        success: function (data) {
            if (data.success === true) {
                Swal.fire({
                    title: 'Success',
                    text: 'Event deleted successfully',
                    type: "Success",
                    confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                    allowOutsideClick: false,
                    timer: 1500,
                });
                getPagedEvents(1,startFrom,pageLength)
            } else {
                Swal.fire({
//                    showCloseButton: true,
//                    closeButtonHtml: '<img class="pop-up-close-icon" src="/v1.1/images/icons/article-card/article-card-close.svg" width="30px" height="30px"></img>',
                    title: "Something went wrong",
                    text: data.error,
                    confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                    type: "error"
                });
            }
        },
        error: function (response) {
            Swal.fire({
//                showCloseButton: true,
//                closeButtonHtml: '<img class="pop-up-close-icon" src="/v1.1/images/icons/article-card/article-card-close.svg" width="30px" height="30px"></img>',
                title: "Something went wrong",
                text: response.d,
                confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                type: "error"
            });
        }
    });
}
