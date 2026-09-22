var startFrom = 0;
var pageLength = 10;
var sortField = "score";
var sortDirection = "asc";
var isLeadCarouselInitialize = false;

var vueListEvents = new Vue({
    el: "#leadboard-list",
    data: {
        records: [],
        recordsFiltered: 0,
    },
    beforeUpdate: function () {

    },
    updated: function () {
        this.$nextTick(function () {
            initEvents();
            if(!isLeadCarouselInitialize) {
                initCarouselComponents();
            }
        });
    }
    , mounted: function () {
        $("#list-records .no-records-found").hide();
        $("#list-records .loading-records").show();
    }
});

$(document).ready(function () {
    $(".main-logo-icon").hide();
    getAllLeaderBoardEvents();
    initEvents();
});


function getAllLeaderBoardEvents() {
    $(".loading-records").show();
    $(".no-records-found").hide();
    document.body.className = "loading";

    $.ajax({
        url: '/events/get-leader-board-events',
        type: 'POST',
        dataType: "json",
        data:{
            "start":startFrom,
            "length":pageLength
        },

        success: function (response) {
            document.body.className = "";
            $(".loading-records").hide();
//                    if (resetList) {
//                        vueListRecords.records = [];
//                    }
            if (response.success) {
                // $("#leadboard-list").ceCarouselDestroy();
                if(vueListEvents.records.length<=0) {
                    vueListEvents.records = response.data;
                }else{
                    $.each(response.data, function (i, val) {
                        $.each(vueListEvents.records,function (index,eventVal){
                            if(val.id == eventVal.id){
                                vueListEvents.records[index].eventScoresDTOList = val.eventScoresDTOList;
                            }
                        })
                    });
                }
            } else {
                document.body.className = "";
            }
            setInterval( getAllLeaderBoardEvents(), 20000);
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

function initCarouselComponents(){
    if(window.innerWidth<= 375) {
        $("#leadboard-list").ceCarousel(244, 15, 1, 1);
    }else if(window.innerWidth<= 425) {
        $("#leadboard-list").ceCarousel(300, 15, 1, 1);
    }else if(window.innerWidth<= 768) {
        $("#leadboard-list").ceCarousel(600, 15, 1, 1);
    }else if(window.innerWidth<= 1024) {
        $("#leadboard-list").ceCarousel(900, 15, 1, 1);
    }else if(window.innerWidth<= 1440) {
        $("#leadboard-list").ceCarousel(650, 15, 2, 1);
    }else if(window.innerWidth<= 1680) {
        $("#leadboard-list").ceCarousel(500, 25, 2, 1);
    }else if(window.innerWidth<= 1778) {
        $("#leadboard-list").ceCarousel(800, 25, 2, 1);
    }else {
        $("#leadboard-list").ceCarousel(900, 25, 2, 1);
    }
    carouselInitialized = true;
    isLeadCarouselInitialize = true;
}
function initEvents(){
    $(".btn-pagination").unbind("click").click(function (){
        pageLength = $(this).attr("value");
        getAllLeaderBoardEvents();

    })
}

function leadPagination( pageSize){
    pageLength = pageSize;
    getAllLeaderBoardEvents();
}