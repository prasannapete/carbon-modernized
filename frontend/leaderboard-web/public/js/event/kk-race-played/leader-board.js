var selectedConsoleId = $("#consoleId").val();
var isRaceCarouselInitialized = false;
var raceRefreshTimer = null;

var vueKkRaceLeaderBoard = new Vue({
    el: "#kk-race-leadboard-list",
    data: {
        records: []
    },
    updated: function () {
        this.$nextTick(function () {
            initRaceCarousel();
        });
    }
});

$(document).ready(function () {
    $(".main-logo-icon").hide();
    getRaceInfoLeaderBoard();
    raceRefreshTimer = setInterval(getRaceInfoLeaderBoard, 20000);
});

function getRaceInfoLeaderBoard() {
    $(".loading-records").show();
    $(".no-records-found").hide();

    var requestData = {};
    if (selectedConsoleId != null && selectedConsoleId !== "") {
        requestData.consoleId = selectedConsoleId;
    }

    $.ajax({
        url: "/kk-race-played/race-info",
        type: "POST",
        dataType: "json",
        data: requestData,
        success: function (response) {
            $(".loading-records").hide();
            if (response.success) {
                updateRaceInfoRecords(normalizeRaceInfoResponse(response.data));
            } else {
                vueKkRaceLeaderBoard.records = [];
                $(".no-records-found").show();
            }
        },
        error: function (data) {
            $(".loading-records").hide();
            if (typeof Swal !== "undefined") {
                Swal.fire({
                    title: "Something went wrong",
                    confirmButtonText: '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px"></img>',
                    text: data.d,
                    type: "error"
                });
            }
        }
    });
}

function normalizeRaceInfoResponse(data) {
    if (Array.isArray(data)) {
        return [{
            consoleId: selectedConsoleId,
            title: "Console " + selectedConsoleId,
            raceInfoList: data
        }];
    }

    var records = [];
    $.each(data || {}, function (consoleId, raceInfoList) {
        records.push({
            consoleId: consoleId,
            title: "Console " + consoleId,
            raceInfoList: raceInfoList || []
        });
    });
    return records;
}

function updateRaceInfoRecords(records) {
    if (selectedConsoleId || vueKkRaceLeaderBoard.records.length <= 0) {
        vueKkRaceLeaderBoard.records = records;
        return;
    }

    $.each(records, function (index, record) {
        var existingRecord = vueKkRaceLeaderBoard.records.find(function (item) {
            return String(item.consoleId) === String(record.consoleId);
        });
        if (existingRecord) {
            existingRecord.raceInfoList = record.raceInfoList;
        }
    });
}

function initRaceCarousel() {
    if (isRaceCarouselInitialized || selectedConsoleId || vueKkRaceLeaderBoard.records.length <= 1) {
        return;
    }

    if (window.innerWidth <= 375) {
        $("#kk-race-leadboard-list").ceCarousel(244, 15, 1, 1);
    } else if (window.innerWidth <= 425) {
        $("#kk-race-leadboard-list").ceCarousel(300, 15, 1, 1);
    } else if (window.innerWidth <= 768) {
        $("#kk-race-leadboard-list").ceCarousel(600, 15, 1, 1);
    } else if (window.innerWidth <= 1024) {
        $("#kk-race-leadboard-list").ceCarousel(900, 15, 1, 1);
    } else {
        $("#kk-race-leadboard-list").ceCarousel(900, 25, 1, 1);
    }
    isRaceCarouselInitialized = true;
}
