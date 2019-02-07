// (function ($) {
//
//     return {
//         initCalendar: function(initKey) {
//
//             $('.jz').on("click", '.uiIconDel', function () {
//                 var calId = $(this).parent().children("input").val();
//                 $('div.CalendarPortletContainer').jzLoad(
//                         "CalendarPortletController.deleteCalendar()",
//                         {"calendarId":calId}, function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//
//             $('.jz').on("click", '.uiIconSimplePlusMini', function () {
//                 var calId = $(this).parent().children("input").val();
//                 $('div.CalendarPortletContainer').jzLoad(
//                         "CalendarPortletController.addCalendar()",
//                         {"calendarId":calId}, function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//
//             $('.jz').on('mouseenter', '.calendarItem', function() {
//                 var button= $(this).children("a.addButton");
//                 button.css("display","block");
//             });
//
//             $('.jz').on('mouseleave', '.calendarItem', function() {
//                 var button= $(this).children("a.addButton");
//                 button.css("display","none");
//             });
//
//             $('.jz').on("click", 'input.PLFcalendarSearchKey', function () {
//                 var initKey = $('input.PLFcalendarSearchKey').val();
//                 var seakey = $(this).val();
//                 if (seakey == initKey) {
//                     seakey = "";
//                     $(this).val("");
//                 }
//                 $('ul.NonDisplayedCalendar').jzLoad(
//                         "CalendarPortletController.getSearchResult()",
//                         {"key":seakey}, function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//
//             $('.jz').on('show', 'input.PLFcalendarSearchKey', function () {
//                 var seakey = $(this).val();
//                 $('ul.NonDisplayedCalendar').jzLoad(
//                         "CalendarPortletController.getSearchResult()",
//                         {"key":seakey}, function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//
//             $('.jz').on("keydown", 'input.PLFcalendarSearchKey', function () {
//                 var seakey = $(this).val();
//                 $('ul.NonDisplayedCalendar').jzLoad(
//                         "CalendarPortletController.getSearchResult()",
//                         {"key":seakey}, function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//
//             $('.jz').on("keypress", 'input.PLFcalendarSearchKey', function () {
//                 var seakey = $(this).val();
//                 $('ul.NonDisplayedCalendar').jzLoad(
//                         "CalendarPortletController.getSearchResult()",
//                         {"key":seakey}, function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//
//             $('.jz').on("keyup", 'input.PLFcalendarSearchKey', function () {
//                 var seakey = $(this).val();
//                 $('ul.NonDisplayedCalendar').jzLoad(
//                         "CalendarPortletController.getSearchResult()",
//                         {"key":seakey}, function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//
//             $('.jz').on("focus", 'input.PLFcalendarSearchKey', function () {
//                 var initKey = $('input.PLFcalendarSearchKey').val();
//                 if ($(this).val() == initKey) {
//                     $(this).val("");
//                 }
//             });
//
//             $('.jz').on("blur", 'input.PLFcalendarSearchKey', function () {
//                 var initKey = $('input.PLFcalendarSearchKey').val();
//                 if ($(this).val() == "") {
//                     $(this).val(initKey);
//                 }
//             });
//
//             $('.jz').on('click', '.btn', function(){
//                 $('div.CalendarPortletContainer').jzLoad(
//                         "CalendarPortletController.calendarHome()", function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//
//             $('.jz').on("click", '.prevDate', function() {
//                 $('div.CalendarPortletContainer').jzLoad(
//                         "CalendarPortletController.decDate()",
//                         {"nbClick":"1"}, function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//             $('.jz').on("click", '.nextDate', function() {
//                 $('div.CalendarPortletContainer').jzLoad(
//                         "CalendarPortletController.incDate()",
//                         {"nbClick":"1"}, function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//             $('.jz').on("click", '.settingsLink', function(){
//                 $('.CalendarPortletContainer').jzLoad(
//                         "CalendarPortletController.setting()", function() {
//
//                         $('*[rel="tooltip"]').tooltip();
//
//                     });
//             });
//
//             $('.jz').on('mouseenter', '.calendarPortlet', function () {
//                 $('.settingsContainerPage').children("a").css("display", "block");
//             });
//             $('.jz').on('mouseleave', '.calendarPortlet', function () {
//                 $('.settingsContainerPage').children("a").css("display", "none");
//             });
//
//             setTimeout(function(){
//                 $('div.CalendarPortletContainer').jzLoad("CalendarPortletController.calendarHome()", function() {
//
//                                  $('*[rel="tooltip"]').tooltip();
//
//                                    });
//             },100);
//         }
//     };
//     //call to init should be in the template
//     //init();
// })($);