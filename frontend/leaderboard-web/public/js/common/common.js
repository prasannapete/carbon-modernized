var eventId = null;
var selectedColor;
var uploadedImg = null;
var selectedScoreType = 0;

$(document).ready(function () {
    initCommonEvents();
    getAllEvents();
    if(selectedColor!=null){
        $("html").css("background-color",selectedColor);
        // $(".content-wrap").css("background-color",selectedColor);
    }
})

function promptLogout(){
    $("#modal-logout-pop-up").modal('show');

    $(".log-me-out-icon").unbind("click").on("click", function (e) {
        document.location = "/logout";
    });

    $(".no-model-close").unbind("click").on("click", function (e) {
        $("#modal-logout-pop-up").modal('hide');
    });
}

function initCommonEvents(){
    $(".dropdown-dashboard-section").on("mouseover", function () {
        $("#header-dashboard-selection-menu").show();
    });
    $(".dropdown-dashboard-section").on("mouseleave", function () {
        $("#header-dashboard-selection-menu").hide();
    });
}

function getAllEvents() {
    $(".loading-records").show();
    $(".no-records-found").hide();
    document.body.className = "loading";

    $.ajax({
        url: '/events/get-all',
        type: 'POST',
        dataType: "json",

        success: function (response) {
            document.body.className = "";
            $(".loading-records").hide();
//                    if (resetList) {
//                        vueListRecords.records = [];
//                    }
            if (response.success) {
                selectedEventId = null;
                // vueUsers.recordsFiltered = response.recordsFiltered;
                // vueUsers.records = response.data
                let htmlContent = "";
                let selectedUser = "";
                selectedEventId = "";
                let selectedId = "";
                $.each(response.data, function (i, val) {
                    if (selectedEventId.length == 0) {
                        selectedEventId = val.id
                        selectedUser = val.fullName
                        selectedId = val.id
                        selectedColor = val.primaryColor
                        uploadedImg = val.logoPath;
                        selectedScoreType = val.scoreType;
                    }
                    eventId = selectedEventId;
                    htmlContent += "<li class='event-name-list' id='" + val.id + "' data-index='" + i + "' data-score-type='" + val.scoreType + "'data-primary-color='" + val.primaryColor + "'data-uploaded-logo='" + val.logoPath + "'>" + val.eventName + "</li>";
                });
                if(uploadedImg!=null) {
                    $(".uploaded-logo-img").attr("src", uploadedImg);
                }else{
                    $(".uploaded-logo-img").attr("src", "");
                }
                $(".event-name-wrapper").html(htmlContent);
                $('.dropdown-toggle').dropdown()
//                $(".received-by-user-text").text(selectedUser)
//                $(".received-by-user-wrapper").attr("value", selectedUser)
                $(".event-name-wrapper li").unbind("click").on("click", function () {
                    document.location = "/event-scores/single-event-display-score?id=" + $(this).attr('id');
                    $(this).parent().parent().find(".event-name-wrapper").attr("value", $(this).attr('id'))
                    $(this).parent().parent().find(".event-name-text").text($(this).text())
                    selectedEventId = $(this).attr('id');
                    localStorage.setItem("selectedEventId", $(this).attr('id'));
                    selectedScoreType = $(this).attr("data-score-type");
                     selectedColor = $(this).attr("data-primary-color");
                    uploadedImg = $(this).attr("data-uploaded-logo");
                    if (selectedScoreType == 0) {
                        sortDirection = "desc"
                        $(".in-time").hide();
                    } else {
                        $(".in-time").show();
                        sortDirection = "asc"
                    }
                    $("html").css("background-color",selectedColor);
                    if(uploadedImg!=null) {
                        $(".uploaded-logo-img").attr("src", uploadedImg);
                    }else{
                        $(".uploaded-logo-img").attr("src", "");
                    }
                    $(".content-wrap").css("background-color",selectedColor);
                    // getAllParticipantsEventScore();
                })
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

setInterval(keepSession, 30000); // every 30 seconds

function keepSession() {
    $.ajax({
        url: '/events/keep-session',
        type: 'POST',
        dataType: "json",
        success: function (response) {
            console.log(response);
        },
        error: function (data) {
        }
    });
}