var urlParams = new URLSearchParams(window.location.search);
var selectedEventId = null;
selectedEventId = urlParams.get('id');
var startFrom = 0;
var pageLength = 10;
var sortField = "score";
var sortDirection = "asc";
var eventId = null;
var selectedColor = null;
var selectedScoreType = 0;
var uploadedImg = null;

$(document).ready(function () {
    getAllEvents();
    initEvents();
    getAllParticipantsEventScore();
});

var vueListEventScore = new Vue({
    el: "#list-event-score-records",
    data: {
        records: [],
        recordsFiltered: 0,
    },
    beforeUpdate: function () {

    },
    updated: function () {
        this.$nextTick(function () {
            initEvents();
        });
    }
    , mounted: function () {
        $("#list-records .no-records-found").hide();
        $("#list-records .loading-records").show();
    }
});

function initEvents() {
    $(".btn-pagination").unbind("click").click(function (){
        pageLength = $(this).attr("value");
        getAllParticipantsEventScore();

    })

    $(".btn-download-excel").unbind("click").click(function(){
        // document.location.href= "/event-scores/export-score-to-excel/"+selectedEventId+"/"+sortDirection;
        $.ajax({
            url: '/event-scores/export-score-to-excel',
            type: "POST",
            dataType: "json",
            data: {
                eventId: selectedEventId,
                order: sortDirection,
                sort: sortField,
                scoreType: selectedScoreType
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
    })

}

function getAllEvents() {
    $(".loading-records").show();
    $(".no-records-found").hide();
    document.body.className = "loading";

    $.ajax({
        url: '/events/get-all',
        type: 'POST',
        dataType: "json",
        data:{
            "start":0,
            "length":10
        },

        success: function (response) {
            document.body.className = "";
            $(".loading-records").hide();
//                    if (resetList) {
//                        vueListRecords.records = [];
//                    }
            if (response.success) {
                // if(localStorage.getItem("selectedEventId")==null){
                //     localStorage.setItem("selectedEventId",response.data[0].id);
                // }
                // selectedEventId = ;
                // vueUsers.recordsFiltered = response.recordsFiltered;
                // vueUsers.records = response.data
                let htmlContent = "";
                let selectedUser = "";
                let selectedId = "";
                $.each(response.data, function (i, val) {
                    if (selectedEventId == val.id) {
                        selectedEventId = val.id
                        selectedUser = val.eventName
                        selectedId = val.id
                        selectedColor = val.primaryColor
                        uploadedImg = val.logoPath
                        selectedScoreType = val.scoreType;
                    }
                    if (selectedScoreType == 0) {
                        sortField = "score"
                        sortDirection = "desc"
                        $(".in-time").hide();
                        $(".in-score").show();
                    } else {
                        sortField = "time"
                        sortDirection = "asc"
                        $(".in-time").show();
                        $(".in-score").hide();
                    }
                    $(".main-logo-icon").hide();
                    if (uploadedImg != null && uploadedImg !== "") {
                        $(".uploaded-logo-img").attr("src", uploadedImg);
                        $("html").css({
                            "background-image": "url('" + uploadedImg + "')",
                            "background-size": "cover",
                            "background-repeat": "no-repeat",
                            "background-position": "center center",
                            "background-color": ""
                        });
                        $("body").css({
                            "background": "transparent"
                        })
                    } else {
                        $(".uploaded-logo-img").attr("src", "");
                        $("html").css({
                            "background-image": "none",
                            "background-color": selectedColor
                        });
                        $("body").css({
                            "background": "transparent"
                        })
                    }
                    $(".event-name-display").text(selectedUser);
                    $("html").css("background-color",selectedColor);
                    // $(".content-wrap").css("background-color",selectedColor);
                    htmlContent += "<li class='event-name-list' id='" + val.id + "' data-index='" + i + "' data-score-type='" + val.scoreType + "' data-primary-color='" + val.primaryColor + "'data-uploaded-logo='" + val.logoPath + "'>" + val.eventName + "</li>";
                });
                $(".event-name-wrapper").html(htmlContent)
                $('.dropdown-toggle').dropdown()
//                $(".received-by-user-text").text(selectedUser)
//                $(".received-by-user-wrapper").attr("value", selectedUser)
                $(".event-name-wrapper li").unbind("click").on("click", function () {
                    document.location = "/event-scores/single-event-display-score?id=" + $(this).attr('id');
                    $(this).parent().parent().find(".event-name-wrapper").attr("value", $(this).attr('id'))
                    $(this).parent().parent().find(".event-name-text").text($(this).text())
                    selectedEventId = $(this).attr('id');
                    selectedScoreType = $(this).attr("data-score-type");
                    selectedColor = $(this).attr("data-primary-color");
                    uploadedImg = $(this).attr("data-uploaded-logo");
                    if (selectedScoreType == 0) {
                        sortDirection = "desc"
                        $(".in-time").hide();
                        $(".in-score").show();
                    } else {
                        sortDirection = "asc"
                        $(".in-time").show();
                        $(".in-score").hide();
                    }
                    $(".main-logo-icon").hide();
                    if (uploadedImg != null && uploadedImg !== "") {
                        $(".uploaded-logo-img").attr("src", uploadedImg);
                        $("html").css({
                            "background-image": "url('" + uploadedImg + "')",
                            "background-size": "cover",
                            "background-repeat": "no-repeat",
                            "background-position": "center center",
                            "background-color": ""
                        });
                        $("body").css({
                            "background": "transparent"
                        })
                    } else {
                        $(".uploaded-logo-img").attr("src", "");
                        $("html").css({
                            "background-image": "none",
                            "background-color": selectedColor
                        });
                        $("body").css({
                            "background": "transparent"
                        })
                    }
                    $(".event-name-display").text($(this).text());
                    getAllParticipantsEventScore();
                    $("html").css("background-color",selectedColor);
                    // $(".content-wrap").css("background-color",selectedColor);
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

function getAllParticipantsEventScore() {
    $.ajax({
        url: '/event-scores/get-event-score-by-event-id',
        type: 'POST',
        dataType: "json",
        data: {
            start: startFrom,
            length: pageLength,
            order: [{
                "column": sortField,
                "dir": sortDirection
            }],
            sort: sortField,
            eventId: selectedEventId,
        },
        success: function (response) {
            document.body.className = "";
            $(".loading-records").hide();
            if (response.success) {
                $(".loading-records").hide();
                $(".supplier-order-card").show();
                // vueListEventScore.records = [];
                var records = [];
                vueListEventScore.recordsFiltered = response.recordsFiltered;
                $.each(response.data, function (i, val) {
                    records.push(val);
                });
                vueListEventScore.records = records;
                if (vueListEventScore.records.length < 1) {
                    $(".no-records-found").show();
                    $(".btn-pagination").hide();
                }else{
                    $(".btn-pagination").show();
                    $(".no-records-found").hide();
                }
            } else {
                document.body.className = "";
            }
            setInterval( getAllParticipantsEventScore(), 20000);
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