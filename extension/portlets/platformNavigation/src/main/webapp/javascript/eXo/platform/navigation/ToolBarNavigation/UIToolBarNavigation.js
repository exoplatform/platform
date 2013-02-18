(function ($, base, uiPopup) {
    var uiPopupMenu = {

        // Elements that must be hidden
        elementsToHide:[],
        // Elements that must be kept visible
        currentVisibleContainers:[],

        /**
         * initialize UIPopupMenu
         *
         * @param {Object,
                *          String} popupMenu popup object
         * @param {Object}
                *          container
         */
        init:function (popupMenu, container) {
            this.superClass = uiPopup;
            this.superClass.init(popupMenu, container.id);
        },
        /**
         * Set position to a popup
         *
         * @param {Object}
                *          popupMenu
         * @param {Number}
                *          x x axis
         * @param {Number}
                *          y y axis
         * @param {boolean}
                *          isRTL right to left flag
         */
        setPosition:function (popupMenu, x, y, isRTL) {
            this.superClass.setPosition(popupMenu, x, y, isRTL);
        },
        /**
         * Set size to a popup
         *
         * @param {Object}
                *          popupMenu
         * @param {Number}
                *          w width
         * @param {Number}
                *          h height
         */
        setSize:function (popup, w, h) {
            this.superClass.setSize(popupMenu, w, h);
        },

        pushVisibleContainer:function (containerId) {
            uiPopupMenu.currentVisibleContainers.push(containerId);
        },

        popVisibleContainer:function () {
            uiPopupMenu.currentVisibleContainers.pop();
        },

        pushHiddenContainer:function (containerId) {
            uiPopupMenu.elementsToHide.push(containerId);
        },
        /**
         * Function called when an element (or more) must be hidden Sets a timeout to
         * time (or 100ms by default) after which the elements in elementsToHide will
         * be hidden
         */
        setCloseTimeout:function (time) {
            if (!time)
                time = 100;
            setTimeout(uiPopupMenu.doOnMenuItemOut, time);
        },
        /**
         * Adds an onCLick event to link elements If they are http links, changes the
         * url in the browser If they are javascript links, executes the javascript
         */
        createLink:function (menuItem, link) {
            if (link && link.href) {
                menuItem.onclick = function (e) {
                    if (link.href.substr(0, 7) == "http://")
                        window.location.href = link.href;
                    else
                        eval(link.href);
                    if (!e)
                        e = window.event;
                    if (e.stopPropagation)
                        e.stopPropagation();
                    e.cancelBubble = true;
                    return false;
                }
            }
        },

        /**
         * The callback function called when timeout is finished Hides the submenus
         * that are no longer pointed at
         */
        doOnMenuItemOut:function () {
            while (uiPopupMenu.elementsToHide.length > 0) {
                var container = document
                        .getElementById(uiPopupMenu.elementsToHide.shift());
                if (container) {
                    /*
                     * It can happen that a submenu appears in both the "to-hide" list and
                     * the "keep-visible" list This happens because when the mouse moves
                     * from the border of an item to the content of this item, a mouseOut
                     * Event is fired and the item submenu is added to the "to-hide" list
                     * while it remains in the "keep-visible" list. Here, we check that the
                     * item submenu doesn't appear in the "keep-visible" list before we hide
                     * it
                     */
                    if ($.inArray(container.id, uiPopupMenu.currentVisibleContainers) === -1) {
                        uiPopupMenu.hide(container);
                    }
                }
            }
        },

        showMenuItemContainer:function (menuItemContainer, x, y) {
            /*
             * menuItemContainer.style.display = "block" ; var x = menuItem.offsetWidth +
             * menuItem.offsetLeft; var y = menuItem.offsetTop; var rootX =
             * eXo.core.Browser.findPosX(menuItem); var rootY =
             * eXo.core.Browser.findPosY(menuItem); if (x +
             * menuItemContainer.offsetWidth + rootX >
             * eXo.core.Browser.getBrowserWidth()) { x -= (menuItemContainer.offsetWidth +
             * menuItem.offsetWidth); } if (y + menuItemContainer.offsetHeight + rootY >
             * eXo.core.Browser.getBrowserHeight()) { y -=
             * (menuItemContainer.offsetHeight - menuItem.offsetHeight); }
             */
            this.superClass.setPosition(menuItemContainer, x, y);
        },
        /**
         * Change object to hidden state
         *
         * @param {Object}
                *          object to hide
         */
        hide:function (object) {
            if (typeof (object) == "string")
                object = document.getElementById(object);
            object.style.display = "none";
            object.style.visibility = "hidden";
        },
        /**
         * Change object to visibility state
         *
         * @param {Object}
                *          object to hide
         */
        show:function (object) {
            this.superClass.show(object);
            object.style.visibility = "";
        }
    };

    var ScrollManager = function (id) {
        if (typeof (id) == "string") id = document.getElementById(id);
        this.mainContainer = id; // The HTML DOM element that contains the tabs, the arrows, etc
        this.elements = new Array(); // the array containing the elements
        this.firstVisibleIndex = 0; // the index in the array of the first visible element
        this.lastVisibleIndex = -1; // the index in the array of the last visible element
        this.axis = 0; // horizontal scroll : 0 , vertical scroll : 1
        this.currDirection = null; // the direction of the current scroll; left or up scroll : 0, right or down scroll : 1
        this.callback = null; // callback function when a scroll is done
        this.leftArrow = null; // the left arrow dom node
        this.rightArrow = null; // the right arrow dom node
        this.arrowsContainer = null // The HTML DOM element that contains the arrows
        var scroll = this;
        this.refresh = setTimeout(function () {
            scroll.checkResize()
        }, 700);
    };

    /**
     * Initializes the scroll manager, with some default parameters
     */
    ScrollManager.prototype.init = function () {
        this.maxSpace = 0;
        this.firstVisibleIndex = 0;
        this.lastVisibleIndex = -1;

        if (!this.arrowsContainer) {
            // Adds the tab elements to the manager
            var arrowsContainer = $(this.mainContainer).find(".ScrollButtons");
            if (arrowsContainer.length) {
                this.arrowsContainer = arrowsContainer[0];
                // Configures the arrow buttons
                var arrowButtons = arrowsContainer.find("a");
                if (arrowButtons.length == 2) {
                    this.initArrowButton(arrowButtons[0], "left", "ScrollLeftButton", "HighlightScrollLeftButton", "DisableScrollLeftButton");
                    this.initArrowButton(arrowButtons[1], "right", "ScrollRightButton", "HighlightScrollRightButton", "DisableScrollRightButton");
                }
            }
        }

        // Hides the arrows by default
        if (this.arrowsContainer) {
            this.arrowsContainer.style.display = "none";
            this.arrowsContainer.space = null;
        }
    };

    /**
     * Loads the tabs in the scroll manager, depending on their css class
     * If clean is true, calls cleanElements to remove the space property of each element
     */
    ScrollManager.prototype.loadElements = function (elementClass, clean) {
        if (clean) this.cleanElements();
        this.elements = $(this.mainContainer).find("." + elementClass);
    };

    /**
     * Initializes the arrows with :
     *  . mouse listeners
     *  . css class and other parameters
     */
    ScrollManager.prototype.initArrowButton = function (arrow, dir, normalClass, overClass, disabledClass) {
        if (arrow) {
            arrow = $(arrow);
            arrow[0].direction = dir; // "left" or "right" (up or down)
            arrow[0].overClass = overClass; // the css class for mouse over event
            arrow[0].disabledClass = disabledClass; // the css class for a disabled arrow
            arrow[0].styleClass = normalClass; // the css class for an enabled arrow, in the normal state
            arrow[0].scrollMgr = this; // an easy access to the scroll manager
            arrow.on("mouseover", this.mouseOverArrow);
            arrow.on("mouseout", this.mouseOutArrow);
            arrow.on("click", this.scroll);
            if (dir == "left") this.leftArrow = arrow[0];
            else if (dir == "right") this.rightArrow = arrow[0];
        }
    };

    /**
     * Disables or enables the arrow
     */
    ScrollManager.prototype.enableArrow = function (arrow, enabled) {
        if (arrow && !enabled) { // disables the arrow
            arrow.className = arrow.disabledClass;
        } else if (arrow && enabled) { // enables the arrow
            arrow.className = arrow.styleClass;
        }
    };
    /**
     * Sets the mouse over css style of the arrow (this)
     * only if it is enabled
     */
    ScrollManager.prototype.mouseOverArrow = function (e) {
        var arrow = this;
        if (arrow.className == arrow.styleClass) {
            arrow.className = arrow.overClass;
        }
    };
    /**
     * Sets the mouse out css style of the arrow (this)
     * only if it is enabled
     */
    ScrollManager.prototype.mouseOutArrow = function (e) {
        var arrow = this;
        if (arrow.className == arrow.overClass) {
            arrow.className = arrow.styleClass;
        }
    };

    /**
     * Calculates the available space for the elements, and inits the elements array like this :
     *  . maxSpace = space of mainContainer - space of arrowsContainer - a margin
     *  . browses the elements and add their space to elementsSpace, for each element compares elementsSpace with maxSpace
     *  . if elementsSpace le maxSpace : the current element is set visible, and its index becomes the lastVisibleIndex
     *  . if elementsSpace gt maxSpace : the current element is set hidden (isVisible = false)
     * At the end, each visible element has an isVisible property set to true, the other elements are set to false,
     * the firstVisibleIndex is 0, the lastVisibleIndex is the last element with isVisible to true
     */
    ScrollManager.prototype.checkAvailableSpace = function () { // in pixels
        if (!this.maxSpace) {
            this.maxSpace = $(this.mainContainer).width() - this.getElementSpace(this.arrowsContainer);
        }
        var elementsSpace = 0, margin = 0;
        var length = this.elements.length;
        if (!this.currDirection) {
            for (var i = this.firstVisibleIndex; i < length; i++) {
                elementsSpace += this.getElementSpace(this.elements[i]);
                if (elementsSpace < this.maxSpace) {
                    this.elements[i].isVisible = true;
                    this.lastVisibleIndex = i;
                } else {
                    this.elements[i].isVisible = false;
                }
            }
        } else {
            for (var i = this.lastVisibleIndex; i >= 0; i--) {
                elementsSpace += this.getElementSpace(this.elements[i]);
                if (elementsSpace < this.maxSpace) {
                    this.elements[i].isVisible = true;
                    this.firstVisibleIndex = i;
                } else {
                    this.elements[i].isVisible = false;
                }
            }
        }
    };

    /**
     * Calculates the space of the element passed in parameter
     * The calcul uses : (horizontal tabs | vertical tabs)
     *  . offsetWidth | offsetHeight
     *  . marginLeft and marginRight | marginTop and marginBottom
     *  . the space of the decorator associated with this element, if any
     * If the element is not rendered (display none), renders it, makes the calcul, and hides it again
     * The value of the space is stored in a property space of the element. In the function is called on
     * the same element again, this value is returned directly to avoid another calcul
     * To remove this value, use the cleanElements function, or set space to null manually
     */
    ScrollManager.prototype.getElementSpace = function (element) {
        if (element && element.space) {
            return element.space;
        }
        var elementSpace = 0;
        if (element) {
            if (this.axis == 0) { // horizontal tabs
                elementSpace += $(element).outerWidth(true);
                // decorator is another element that is linked to the current element (e.g. a separator bar)
                if (element.decorator) elementSpace += this.getElementSpace(element.decorator);
            } else if (this.axis == 1) { // vertical tabs
                elementSpace += $(element).outerHeigth(true);
                if (element.decorator) elementSpace += this.getElementSpace(element.decorator);
            }
            // Store the calculated value for faster return on next calls. To recalculate, set element.space to null.
            element.space = elementSpace;
        }
        return elementSpace;
    };

    /**
     * Clean the elements of the array : set the space property to null
     */
    ScrollManager.prototype.cleanElements = function () {
        for (var i = 0; i < this.elements.length; i++) {
            this.elements[i].space = null;
            if (this.elements[i].decorator) this.elements[i].decorator.space = null;
        }
    };

    /**
     * Function called when an arrow is clicked. Shows an additionnal element and calls the
     * appropriate scroll function (left or right). Works like this :
     *  . shows the otherHiddenElements again
     *  . moves the firstVisibleIndex or lastVisibleIndex to the new index
     *  . clear the otherHiddenElements array
     *  . calls the appropriate scroll function (left or right)
     */
    ScrollManager.prototype.scroll = function (e) {
        var src = this;
        if (src.className !== src.disableClass) {
            if (src.direction == "left") src.scrollMgr.scrollLeft();
            else if (src.direction == "right") src.scrollMgr.scrollRight();
        }
        return false;
    };

    ScrollManager.prototype.scrollLeft = function () { // Same for scrollUp
        if (this.firstVisibleIndex > 0) {
            this.currDirection = 0;
            this.firstVisibleIndex--;
            this.renderElements();
        }
    };

    ScrollManager.prototype.scrollUp = function () {
        if (this.scrollMgr) this.scrollMgr.scrollLeft();
    };
    /**
     * Scrolls right (or down) :
     *  . sets the current first visible element hidden
     *  . increments firstVisibleIndex
     *  . increments lastVisibleIndex
     *  . set the new last visible element to visible
     * Simulates a move to the right of the tabs
     */
    ScrollManager.prototype.scrollRight = function () { // Same for scrollDown
        if (this.lastVisibleIndex < this.elements.length - 1) {
            this.currDirection = 1;
            this.lastVisibleIndex++;
            this.renderElements();
        }
    };

    ScrollManager.prototype.scrollDown = function () {
        if (this.scrollMgr) this.scrollMgr.scrollRight();
    };

    ScrollManager.prototype.renderElements = function () {
        this.checkAvailableSpace();

        for (var i = 0; i < this.elements.length; i++) {
            if (this.elements[i].isVisible) { // if the element should be rendered...
                this.elements[i].style.display = "block";
            } else { // if the element must not be rendered...
                this.elements[i].style.display = "none";
                this.arrowsContainer.style.display = "block";
            }
        }
        if (this.arrowsContainer.style.display == "block") {
            this.renderArrows();
        }

        if (typeof(this.callback) == "function") this.callback();
    };

    /**
     * Renders the arrows. If we reach the end of the tabs, this end arrow is disabled
     */
    ScrollManager.prototype.renderArrows = function () {
        // Enables/Disables the arrow buttons depending on the elements to show
        if (this.firstVisibleIndex == 0) this.enableArrow(this.leftArrow, false);
        else this.enableArrow(this.leftArrow, true);

        if (this.lastVisibleIndex == this.elements.length - 1) this.enableArrow(this.rightArrow, false);
        else this.enableArrow(this.rightArrow, true);
    };

    /**
     * Calculates the space of the elements between indexStart and indexEnd
     * If these parameters are null, calculates the space for all the elements of the array
     * Uses the getElementSpace function
     */
    ScrollManager.prototype.getElementsSpace = function (indexStart, indexEnd) {
        if (indexStart == null && indexEnd == null) {
            indexStart = 0;
            indexEnd = this.elements.length - 1;
        }
        var elementsSpace = 0;
        if (indexStart >= 0 && indexEnd <= this.elements.length - 1) {
            for (var i = indexStart; i <= indexEnd; i++) {
                elementsSpace += this.getElementSpace(this.elements[i]);
            }
        }
        return elementsSpace;
    };

    ScrollManager.prototype.checkResize = function () {
        if (this.mainContainer) {
            var tmp = $("#" + this.mainContainer.id);
            if (!tmp.length) {
                clearTimeout(this.refresh);
                return;
            }
            this.mainContainer = tmp[0];
            this.mainContainer.space = null;
            this.arrowsContainer.space = null;
            var curr = $(this.mainContainer).width() - this.getElementSpace(this.arrowsContainer);
            if (this.maxSpace && this.maxSpace !== curr) {
                var mgrParent = tmp.closest(".UIWindow");
                // if the tabs exist on the page
                // in desktop mode, checks that the UIWindow containing the tabs is
                // visible (display block)
                if (mgrParent.length == 0 || mgrParent.css("display") == "block") {
                    this.init();
                    this.renderElements();
                }
            }
        }
        var scroll = this;
        this.refresh = setTimeout(function () {
            scroll.checkResize()
        }, 700);
    };

    var portalNavigation = {
        hideMenuTimeoutIds:{},
        scrollMgr:null,

        /**
         * Sets some parameters :
         *  . the superClass to eXo.webui.UIPopupMenu
         *  . the css style classes
         * and calls the buildMenu function
         */
        init:function (popupMenu, container) {
            this.superClass = uiPopupMenu;
            this.superClass.init(popupMenu, container);
            //UIPopup.js will add onclick event that increase z-index
            popupMenu.onmousedown = null;

            this.containerStyleClass = "MenuItemContainer";
            this.tabStyleClass = "MenuItem";

            this.buildMenu(popupMenu);

        },

        /**
         * Calls the init function when the page loads
         */
        onLoad:function (baseId) {
            var uiNavPortlet = $("#" + baseId);
            if (uiNavPortlet.hasClass("UIHorizontalTabs")) portalNavigation.init(uiNavPortlet[0], uiNavPortlet[0]);

            if (baseId === "UIHorizontalNavigation") {
                $(".UIHorizontalNavigation").slice(1).each(function () {
                    $(this).hide();
                });
            }

        },

        /**
         * Builds the menu and the submenus
         * Configures each menu item :
         *  . sets onmouseover and onmouseout to call setTabStyle
         *  . sets the width of the item
         * Checks if a submenu exists, if yes, set some parameters :
         *  . sets onclick on the item to call toggleSubMenu
         *  . sets the width and min-width of the sub menu container
         * For each sub menu item :
         *  . set onmouseover to onMenuItemOver and onmouseout to onMenuItemOut
         *  . adds onclick event if the item contains a link, so a click on this item will call the link
         */

        buildMenu:function (popupMenu) {
            var portalNav = portalNavigation;
            var topContainer = $(popupMenu);

            // Top menu items
            topContainer.children(".UITab").each(function () {
                var tab = $(this);
                var tabLink = tab.find("a:first");

                var highlightClass = "UITab HighlightNavigationTab";
                var actualClass = "UITab NormalToolbarTab";


                tabLink.click(function () {


                    if (tab.attr("class") !== "UITab HighlightNavigationTab") {
                        $(".UITab").each(function () {

                            portalNav.mouseLeaveTab($(this), actualClass);
                        });

                        portalNav.mouseEnterTab(tab, highlightClass);
                    }
                    else {
                        portalNav.mouseLeaveTab(tab, actualClass);
                    }
                });


                tab.click(function (e) {
                    e.stopPropagation();
                });


                $(document).click(function () {


                    topContainer.children(".UITab").each(function () {
                        portalNav.mouseLeaveTab($(this), actualClass);
                    });

                });
                tab.find("." + portalNav.containerStyleClass).first().css("width", "auto");
            });


            var itemConts = topContainer.find("." + this.containerStyleClass);
            itemConts.each(function () {
                if (!this.id) {
                    this.id = eXo.generateId("PortalNavigationContainer");
                }
                this.resized = true;

                var jObj = $(this);
                var items = jObj.find("." + portalNav.tabStyleClass);
                if (items.length == 0) {
                    jObj.remove();
                }
                else {
                    jObj.on({"mouseenter":portalNav.onMenuItemOver, "mouseleave":portalNav.onMenuItemOut
                    }, "." + portalNav.tabStyleClass);
                }
            });
        },

        cancelNextClick:function (ComponentId, baseId, message) {
            var component = $("#" + ComponentId);
            var parent = $("#" + baseId);

            var portalNav = portalNavigation;

            // Top menu items
            parent.children(".UITab").each(function () {
                var tab = $(this);
                var highlightClass = "UITab HighlightNavigationTab";
                portalNav.mouseEnterTab(tab, highlightClass);
                tab.find("." + portalNav.containerStyleClass).first().css("minWidth", tab.width());
            });

            var itemConts = parent.find("." + this.containerStyleClass);
            itemConts.each(function () {
                if (!this.id) {
                    this.id = eXo.generateId("PortalNavigationContainer");
                }
                this.resized = true;

            });

            component.hide();
            parent.find("span").html(message).css('display', 'inline').fadeOut(2000, function () {
                component.show()
            });
        },
       ClickActionButton:function ( baseId) {

            var parent = $("#" + baseId);

            var portalNav = portalNavigation;

            // Top menu items
            parent.children(".UITab").each(function () {
                var tab = $(this);
                var highlightClass = "UITab HighlightNavigationTab";
                portalNav.mouseEnterTab(tab, highlightClass);
                tab.find("." + portalNav.containerStyleClass).first().css("minWidth", tab.width());
            });

            var itemConts = parent.find("." + this.containerStyleClass);
            itemConts.each(function () {
                if (!this.id) {
                    this.id = eXo.generateId("PortalNavigationContainer");
                }
                this.resized = true;

            });
        },
        /**
         * Method triggered as mouse cursor enter a navigation node showed on navigation tab.
         *
         * @param tab
         * @param newClass
         */
        mouseEnterTab:function (tab, newClass) {
            var portalNav = portalNavigation;

            var getNodeURL = tab.attr("exo:getNodeURL");
            var menuItemContainer = tab.find("." + portalNav.containerStyleClass).first();
            if (getNodeURL && !menuItemContainer.length) {
                var jsChilds = ajaxAsyncGetRequest(getNodeURL, false)
                try {
                    var data = $.parseJSON(jsChilds);
                }
                catch (e) {
                }
                if (!data || !data.length) {
                    return;
                }
                tab.append(portalNav.generateContainer(data));
            }
            tab.attr("class", newClass);

            menuItemContainer = tab.find("." + portalNav.containerStyleClass).first();
            if (menuItemContainer.length) {
                portalNav.cancelHideMenuContainer(menuItemContainer.attr("id"));
                portalNav.showMenu(tab, menuItemContainer);
            }
            return false;
        },

        /**
         * Method triggered as mouse cursor leaves a navigation node showed on navigation tab
         *
         * @param tab
         * @param oldClass
         */
        mouseLeaveTab:function (tab, oldClass) {
            var portalNav = portalNavigation;

            tab.attr("class", oldClass);
            var conts = tab.find("." + portalNav.containerStyleClass);
            if (conts.length) {
                portalNav.hideMenuTimeoutIds[conts[0].id] = window.setTimeout(function () {
                    portalNav.hideMenu(conts[0].id);
                }, 0);
            }
            return false;
        },

        /**
         * Shows a submenu
         * Sets the width of the submenu (the first time it is shown) to fix a bug in IE
         * Sets the currentOpenedMenu to the menu being opened
         */
        showMenu:function (tab, menuItemContainer) {
            var portalNav = portalNavigation;
            var browser = base.Browser;
            portalNav.superClass.pushVisibleContainer(menuItemContainer.attr("id"));

            menuItemContainer.css({"display":"block", "position":"absolute"});
            var offParent = menuItemContainer.offsetParent();
            var y = tab.height() + browser.findPosYInContainer(tab[0], offParent[0]);
            var x = browser.findPosXInContainer(tab[0], offParent[0]) + 2;
            if (base.I18n.isRT()) {
                x = browser.findPosXInContainer(tab[0], offParent[0], true);
            }
            portalNav.superClass.setPosition(menuItemContainer[0], x, y, base.I18n.isRT());
            portalNav.superClass.show(menuItemContainer[0]);

            menuItemContainer.css("width", menuItemContainer.width() + "px");

            var posXinBrowser = menuItemContainer.offset().left;
            if (base.I18n.isLT()) {
                if (posXinBrowser + menuItemContainer.width() >= $(window).width()) {
                    x += (tab.width() - menuItemContainer.width());
                    menuItemContainer.css("left", x + "px");
                }
            } else {
                if (posXinBrowser + tab.width() < menuItemContainer.width()) {
                    x += (tab.width() - menuItemContainer.width());
                    menuItemContainer.css("right", x + "px");
                }
            }
        },

        cancelHideMenuContainer:function (containerId) {
            var timeout = portalNavigation.hideMenuTimeoutIds[containerId];
            portalNavigation.hideMenuTimeoutIds[containerId] = null;
            if (timeout) {
                window.clearTimeout(timeout);
            }
        },

        /**
         * Changes the style of the parent button when a submenu has to be hidden
         */
        hideMenu:function (containerId) {
            var portalNav = portalNavigation;
            portalNav.hideMenuTimeoutIds[containerId] = null;

            var menuItemContainer = $("#" + containerId);
            if (menuItemContainer.length) {
                var id = menuItemContainer.attr("id");
                portalNav.superClass.pushHiddenContainer(id);
                portalNav.superClass.popVisibleContainer(id);
                portalNav.superClass.setCloseTimeout();
                portalNav.superClass.hide(menuItemContainer[0]);
            }
        },

        /**
         * When the mouse goes over a menu item (in the main nav menu)
         * Check if this menu item has a sub menu, if yes, opens it
         * Changes the style of the button
         */
        onMenuItemOver:function () {
            var menuItem = $(this);
            var portalNav = portalNavigation;

            var getNodeURL = menuItem.attr("exo:getNodeURL");
            var subContainer = menuItem.find("." + portalNav.containerStyleClass).first();
            if (getNodeURL && !subContainer.length) {
                var jsChilds = ajaxAsyncGetRequest(getNodeURL, false);
                try {
                    var data = $.parseJSON(jsChilds);
                } catch (e) {
                }
                if (!data || !data.length) {
                    menuItem.removeClass("ArrowIcon");
                    menuItem.removeAttr("exo:getNodeURL");
                    return;
                }
                menuItem.append(portalNav.generateContainer(data));
            }

            subContainer = menuItem.find("." + portalNav.containerStyleClass).first();
            if (subContainer.length) {
                portalNav.superClass.pushVisibleContainer(subContainer.attr("id"));
                portalNav.showMenuItemContainer(menuItem, subContainer);
                if (!subContainer.data("firstTime")) {
                    subContainer.css("width", subContainer.width() + 2 + "px");
                    subContainer.data("firstTime", true);
                }
            }
        },

        /**
         * Shows a sub menu, uses the methods from superClass (eXo.webui.UIPopupMenu)
         */
        showMenuItemContainer:function (menuItem, menuItemContainer) {
            var x = menuItem.width();
            var y = menuItem.position().top;
            this.superClass.show(menuItemContainer[0]);
            var posRight = $(window).width() - base.Browser.findPosX(menuItem[0], true);
            var rootX = (base.I18n.isLT() ? base.Browser.findPosX(menuItem[0]) : posRight);
            if (x + menuItemContainer.width() + rootX > $(window).width()) {
                x -= (menuItemContainer.width() + menuItem.width());
            }
            this.superClass.setPosition(menuItemContainer[0], x, y, base.I18n.isRT());
        },

        /**
         * When the mouse goes out a menu item from the main nav menu
         * Checks if this item has a sub menu, if yes calls methods from superClass to hide it
         */
        onMenuItemOut:function () {
            var menuItem = $(this);
            var portalNav = portalNavigation;

            var subContainer = menuItem.find("." + portalNav.containerStyleClass).first();
            if (subContainer.length) {
                var id = subContainer.attr("id");
                portalNav.superClass.pushHiddenContainer(id);
                var index = $.inArray(id, portalNav.superClass.currentVisibleContainers);
                if (index !== -1) {
                    portalNav.superClass.currentVisibleContainers.splice(index, 1);
                }
                portalNav.superClass.setCloseTimeout(200);
            }
        },

        /***** Scroll Management *****/
        /**
         * Function called to load the scroll manager that will manage the tabs in the main nav menu
         *  . Creates the scroll manager
         *  . Adds the tabs to the scroll manager
         */
        loadScroll:function (portalNavId) {
            var uiNav = portalNavigation;
            var portalNav = $("#" + portalNavId);
            if (!portalNav.length) return;

            // Creates new ScrollManager and initializes it
            uiNav.scrollMgr = new ScrollManager(portalNav[0]);
            uiNav.scrollMgr.loadElements("UITab");

            // Finish initialization
            uiNav.scrollMgr.init();
            uiNav.scrollMgr.renderElements();
        },

        generateContainer:function (data) {
            var htmlFrags = "<ul class='" + this.containerStyleClass + "' style='display: none;' id='";
            htmlFrags += eXo.generateId("PortalNavigationContainer") + "' resized='false'>";

            for (var i = 0; i < data.length; i++) {
                var node = data[i];
                var actionLink = node.actionLink ? node.actionLink : "#" + node.label;

                htmlFrags += ("<li class='MenuItem " + (node.hasChild ? "ArrowIcon " : "") + (node.isSelected ? "SelectedItem'" : "NormalItem'"));
                htmlFrags += (node.hasChild ? (" exo:getNodeURL='" + node.getNodeURL + "' ") : "" );
                htmlFrags += ("' title='" + node.label + "'>");
                htmlFrags += ("<a class='ItemIcon " + (node.icon ? node.icon : "DefaultPageIcon") + "'" +
                        "href='" + actionLink + "'>" + (node.label.length > 40 ? node.label.substring(0, 37) + "..." : node.label) + "</a>");
                if (node.childs.length) {
                    htmlFrags += portalNavigation.generateContainer(node.childs);
                }
                htmlFrags += "</li>";
            }
            htmlFrags += "</ul>";
            return htmlFrags;
        }
    };

    return {
        ScrollManager:ScrollManager,
        UIPopupMenu:uiPopupMenu,
        UIPortalNavigation:portalNavigation
    };
})($, base, uiPopup);
