
/*
$(document).ready(function (e) {
    $.each($(".kt-carousel"), function (key,value){
        initializeCarousel(value);
    })
});
*/
;(function($, window, document, undefined) {

    function Carousel(element,_itemWidth, _rightMargin,_visibleItems,_opacity) {
        this.rightMargin = 20;
        this.currentTransformation = 0;
        this.slider;
        this.isDown = false;
        this.element = $(element)
        this.carouselElement = null;
        this.itemsCanBeVisible;
        this.scrollLeft;
        this.btnOpacity = 1;
        this.itemWidth = _itemWidth
        this.rightMargin = _rightMargin
        if (_visibleItems == null || _visibleItems == undefined) {
            this.itemsVisible = 0
        } else {
            this.itemsVisible = _visibleItems
        }
        if (_opacity) {
            this.btnOpacity = _opacity
        }
        // this.supplierTopCard = _supplierTopCard
        this.availableWidth = this.element.parent().innerWidth();
        if (this.itemsVisible == 0) {
            // if (_supplierTopCard) {
            //     this.itemsVisible = Math.floor((this.availableWidth - 120) / (this.itemWidth + this.rightMargin));
            //     this.width = this.element.children().length * (this.itemWidth + this.rightMargin);
            // } else {
            //     this.itemsVisible = Math.floor(this.availableWidth / (this.itemWidth + this.rightMargin));
            //     this.width = this.element.children().length * (this.itemWidth + this.rightMargin);
            // }
        } else {
            this.itemsCanBeVisible = Math.floor(this.availableWidth / (this.itemWidth));
            if (this.itemsCanBeVisible > this.itemsVisible) {
                this.rightMargin = this.rightMargin + (((this.itemsCanBeVisible - this.itemsVisible) * this.itemWidth)/this.itemsVisible)
            }
            this.width = this.element.children().length * (this.itemWidth + this.rightMargin);
        }
        this.currentVisibleFirstPosition = this.itemsVisible;
        this.items = $(this.element.children());
        this.initializeCarousel();
    }

    $.fn.ceCarousel = function(_itemWidth, _rightMargin,_visibleItems,_opacity) {

        if ($(this).length > 1) {
            this.each(function () {
                new Carousel(this,_itemWidth, _rightMargin,_visibleItems,_opacity);
            })
        } else {
            new Carousel(this,_itemWidth, _rightMargin,_visibleItems,_opacity);
            //initializeCarousel(this)
        }
    }

    // $.fn.ktMultipleCarousel = function(_itemWidth, _rightMargin,_visibleItems,_opacity,_supplierTopCard,_calculatePos,_selectedPos) {
    //
    //     this.each(function (index) {
    //         new Carousel(this,_itemWidth, _rightMargin,_visibleItems,_opacity,_supplierTopCard,_calculatePos,_selectedPos[index]);
    //     })
    //
    // }

    $.fn.ceCarouselDestroy = function () {
        currentVisibleFirstPosition = 0;
        currentTransformation =0;
        availableWidth = 0;
        $(this).empty();
    }

    $.fn.reInitialize = function () {
        currentVisibleFirstPosition = 0;
        currentTransformation =0;
        availableWidth = 0;
    }

    Carousel.prototype.initializeCarousel = function () {

        this.element.css("width", "100%");
        this.element.css("position", "relative");
        this.element.empty();
        this.prevElement =  $("<div class='carousel-prev'><div style='width: 50px;height: 50px;display: inline-flex;align-items: center'>" +
            '<button style="background: url(\'/images/icons/arrow-right.svg\');transform: rotate(180deg);width: 50px;height: 50px;border: none;background-size: contain;opacity: ' + this.btnOpacity + '" class="carousel-btn">' +
            '</button></div></div>').on('click',$.proxy(function (e) {
            // if (shoppingListPopover != null) {
            //     shoppingListPopover.popover("dispose");
            //     shoppingListPopover = null;
            // }
            e.stopPropagation();
            if (this.currentVisibleFirstPosition <= (this.itemsVisible + 1)) {
                $(this.prevElement.find('button')[0]).prop("disabled",true);
                $(this.prevElement.find('button')[0]).addClass("disabled");
            }
            if (this.currentVisibleFirstPosition <= (this.items.length)) {
                $(this.nextElement.find('button')[0]).prop("disabled", false);
                $(this.nextElement.find('button')[0]).removeClass("disabled");
            }
            this.transformLeft(1)
        },this));
        this.element.append(this.prevElement);

        if (this.calculatePos != null && this.selectedPos == -1) {
            /*let midPos = Math.round(this.items.length / 2) - 2
            this.currentTransformation -= (midPos * Math.round((this.width / this.items.length)));
            this.currentVisibleFirstPosition += midPos*/
            let lastPurchasePos = 0;
            let width = this.width
            let length = this.items.length
            let currentTransformation = 0;
            let currentVisibleFirstPosition = 0;
            let lastPurchaseExists = false;
            let purchaseOrderPos = 0;
            this.items.each(function (key, item) {
                if($(item).find('.last-ordered').length > 0) {
                    lastPurchasePos = key - 1
                    purchaseOrderPos = key
                    lastPurchaseExists = true
                    if (lastPurchasePos > 0) {
                        if (key >= (length-1)) {
                            lastPurchasePos = lastPurchasePos - 1
                        }
                        currentTransformation -= (lastPurchasePos * Math.round((width / length)));
                        currentVisibleFirstPosition += lastPurchasePos
                    }
                }
            });
            this.currentTransformation = currentTransformation;
            this.currentVisibleFirstPosition = this.itemsVisible + currentVisibleFirstPosition;
            if (!lastPurchaseExists) {
                let midPos = Math.round(this.items.length / 2) - 2
                this.currentTransformation -= (midPos * Math.round((this.width / this.items.length)));
                this.currentVisibleFirstPosition = this.itemsVisible + midPos
            } else {
                if (this.currentVisibleFirstPosition >= this.items.length && purchaseOrderPos >= (this.items.length - 1)) {
                    this.currentTransformation -= Math.round((width / length))
                    this.currentVisibleFirstPosition += 1
                } else if (lastPurchasePos < 0) {
                    this.currentTransformation += Math.round((width / length))
                    this.currentVisibleFirstPosition -= 1
                }
            }
        } else if (this.selectedPos != -1 && this.selectedPos != null) {
            this.selectedPos = this.selectedPos - 1
            this.currentTransformation -= (this.selectedPos * Math.round((this.width / this.items.length)));
            this.currentVisibleFirstPosition += this.selectedPos
        }
        let appendElement;
        if (this.items.length < 2) {
            appendElement = $("<div style='width:" + this.width + "px;transform: translate3d(0px, 0px, 0px);transition: all 0.25s ease 0s;' class='clearfix item-container' />").wrap("<div class='carousel-item-inner-content' style='position: relative;overflow: hidden;margin-left: 5px' />");
        } else {
            if (this.supplierTopCard) {
                appendElement = $("<div style='width:" + this.width + "px;transform: translate3d(0px, 0px, 0px);transition: all 0.25s ease 0s;' class='clearfix item-container' />").wrap("<div class='carousel-item-inner-content' style='position: relative;overflow: hidden;margin-left: 5px' />");
            } else {
                appendElement = $("<div style='width:" + this.width + "px;transform: translate3d(" + this.currentTransformation + "px, 0px, 0px);transition: all 0.25s ease 0s;' class='clearfix item-container' />").wrap("<div class='carousel-item-inner-content' style='position: relative;overflow: hidden;margin-left: 5px' />");
            }
         }
        this.element.append($(appendElement).parent())
        let width = this.width
        let length = this.items.length
        this.items.each(function (key, item) {
            item = $('<div style="width: ' + (width / length) + 'px;float: left" class="carousel-items">').append(item);
            $(appendElement).append(item)
        })
        this.carouselElement = $(appendElement)
        this.nextElement = $("<div class='carousel-next'><div style='width: 50px;height: 50px; display: inline-flex;align-items: center'>" +
            '<button style="background: url(\'/images/icons/arrow-right.svg\');width: 50px;height: 50px;border: none;background-size: contain;opacity: ' + this.btnOpacity + '" class="carousel-btn"></button></div></div>').on('click',$.proxy(function (e) {
            // if (shoppingListPopover != null) {
            //     shoppingListPopover.popover("dispose");
            //     shoppingListPopover = null;
            // }
            e.stopPropagation();
            if (this.currentVisibleFirstPosition >= (this.items.length - 1)) {
                $(this.nextElement.find('button')[0]).prop("disabled",true);
                $(this.nextElement.find('button')[0]).addClass("disabled");
            }
            if (this.currentVisibleFirstPosition >= this.itemsVisible) {
                $(this.prevElement.find('button')[0]).prop("disabled", false);
                $(this.prevElement.find('button')[0]).removeClass("disabled");
            }
            this.transformRight(1)
        },this));
        this.element.append(this.nextElement)
        slider = $('.item-container');
        // if ((this.itemsCanBeVisible >= this.items.length && this.currentVisibleFirstPosition === 3) || (this.items.length == 3 && this.currentVisibleFirstPosition == 3)|| (this.items.length < 3 && this.currentVisibleFirstPosition < 3)) {
        //     $(this.nextElement.find('button')[0]).prop("disabled",true);
        //     $(this.prevElement.find('button')[0]).prop("disabled",true);
        //     $(this.nextElement).hide();
        //     $(this.prevElement).hide();
        //     $(this.nextElement.find('button')[0]).addClass("disabled");
        //     $(this.prevElement.find('button')[0]).addClass("disabled");
        // } else if (this.items.length == 1) {
        //     $(this.nextElement.find('button')[0]).prop("disabled",true);
        //     $(this.prevElement.find('button')[0]).prop("disabled",true);
        //     $(this.nextElement).hide();
        //     $(this.prevElement).hide();
        //     $(this.nextElement.find('button')[0]).addClass("disabled");
        //     $(this.prevElement.find('button')[0]).addClass("disabled");
        // }
        if (this.btnOpacity < 1) {
            if (this.element.parent().parent().css('max-width')) {
                let avWidth = parseInt(this.element.parent().parent().css('max-width').replaceAll('px', '')) - 125;
                let itemVisible = Math.floor(avWidth / (this.itemWidth + this.rightMargin));
                this.currentVisibleFirstPosition = itemVisible;
                this.itemsVisible = itemVisible;
                if (itemVisible >= this.items.length) {
                    $(this.nextElement.find('button')[0]).prop("disabled", true);
                    $(this.prevElement.find('button')[0]).prop("disabled", true);
                } else {
                    $(this.prevElement.find('button')[0]).prop("disabled", true);
                    $(this.prevElement.find('button')[0]).css('opacity',1);
                    $(this.prevElement.find('button')[0]).addClass("disabled");
                    $(this.nextElement.find('button')[0]).css('opacity',1)
                }
            } else {
                $(this.nextElement.find('button')[0]).prop("disabled", true);
                $(this.prevElement.find('button')[0]).prop("disabled", true);
            }
        }
        if (this.currentVisibleFirstPosition <= 3) {
            $(this.prevElement.find('button')[0]).prop("disabled",true);
            $(this.prevElement.find('button')[0]).addClass("disabled");
        }
        if (this.currentVisibleFirstPosition >= this.items.length) {
            $(this.nextElement.find('button')[0]).prop("disabled",true);
            $(this.nextElement.find('button')[0]).addClass("disabled");
        }
        if (this.supplierTopCard) {
            if (this.itemsVisible < this.items.length) {
                $(this.prevElement.find('button')[0]).prop("disabled",true);
                $(this.prevElement.find('button')[0]).addClass("disabled");
            }
            if (this.itemsVisible >= this.items.length) {
                $(this.nextElement.find('button')[0]).prop("disabled",true);
                $(this.prevElement.find('button')[0]).prop("disabled",true);
                $(this.nextElement.find('button')[0]).addClass("disabled");
                $(this.prevElement.find('button')[0]).addClass("disabled");
            }
        }
        //this.initCarouselEvents();
    }

    Carousel.prototype.transformRight = function (count) {
        this.currentTransformation -= (count * Math.round((this.width / this.items.length)));
        this.currentVisibleFirstPosition += count;
        this.carouselElement.css({"transform": "translate3d(" + this.currentTransformation + "px, 0px, 0px)"});
    }

    Carousel.prototype.transformLeft = function (count) {
        this.currentTransformation += (count * Math.round((this.width / this.items.length)));
        this.currentVisibleFirstPosition -= count;
        this.carouselElement.css({"transform": "translate3d(" + this.currentTransformation + "px, 0px, 0px)"});
    }

    Carousel.prototype.initCarouselEvents = function () {
        $(".carousel-next").unbind("click").on("click", function (e) {
            if (this.currentVisibleFirstPosition >= (this.items.length - 1)) {
                $($(this).find('button')[0]).prop("disabled",true);
            }
            $($($(this).parent().parent().find(".carousel-prev")[0]).find('button')[0]).prop("disabled",false);
            this.transformRight(1, $(this).parent().parent().find(".item-container")[0]);
        });
        $(".carousel-prev").unbind("click").on("click", function (e) {
            if (this.currentVisibleFirstPosition <= (this.itemsVisible + 1)) {
                $($(this).find('button')).prop("disabled",true);
            }
            $($($(this).parent().parent().find(".carousel-next")[0]).find('button')[0]).prop("disabled",false);
            this.transformLeft(1, $(this).parent().parent().find(".item-container")[0]);
        });
    }

    function animate(transform) {
        $(".item-container").css({"transform": "translate3d(" + transform + "px, 0px, 0px)"});
    }
})(window.Zepto || window.jQuery, window, document);