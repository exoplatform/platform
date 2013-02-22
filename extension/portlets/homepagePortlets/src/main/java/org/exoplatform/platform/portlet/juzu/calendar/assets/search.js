$(function () {

    function init(initKey) {
        setTimeout(function()
                {
                    $('div.CalendarPortletContainer').jzLoad(
                            "CalendarPortletController.calendarHome()");
                }
                ,100);

        //not taken effects must be duplicated to gtmpl
        $('.SettingValidationButton').on('click',function(){
            $('div.CalendarPortletContainer').jzLoad(
                    "CalendarPortletController.calendarHome()");
        });
        $('.settingsLink').on("click", function(){
            $('.calendarPortlet').jzLoad(
                    "CalendarPortletController.setting()");
        });
        $('span.addButtonSpan').on("click", function () {

            var calId = $(this).parent().children("input").val();

            $('div.SettingsContainerData').jzLoad(
                    "CalendarPortletController.addCalendar()",
                    {"calendarId":calId});

        });
        $('span.deleteButtonSpan').on("click", function () {

            var calId = $(this).parent().children("input").val();

            $('div.SettingsContainerData').jzLoad(
                    "CalendarPortletController.deleteCalendar()",
                    {"calendarId":calId});

        });
        $('input.PLFcalendarSearchKey').on("click", function () {
            $("#nonDisplayedCalendarContainer").css("display", "none");
            var seakey = $(this).val();

            if (seakey == initKey) {
                seakey = "";
                $(this).val("");
            }

            $('div.NonDisplayedCalendar').jzLoad(
                    "CalendarPortletController.getSearchResult()",
                    {"key":seakey});

        });
        $('input.PLFcalendarSearchKey').on('show', function () {
            $("#nonDisplayedCalendarContainer").css("display", "none");
            var seakey = $(this).val();
            $('div.NonDisplayedCalendar').jzLoad(
                    "CalendarPortletController.getSearchResult()",
                    {"key":seakey});
        });
        $('input.PLFcalendarSearchKey').on("keydown", function () {
            $("#nonDisplayedCalendarContainer").css("display", "none");
            var seakey = $(this).val();
            $('div.NonDisplayedCalendar').jzLoad(
                    "CalendarPortletController.getSearchResult()",
                    {"key":seakey});
        });

        $('input.PLFcalendarSearchKey').on("keypress", function () {
            $("#nonDisplayedCalendarContainer").css("display", "none");
            var seakey = $(this).val();
            $('div.NonDisplayedCalendar').jzLoad(
                    "CalendarPortletController.getSearchResult()",
                    {"key":seakey});
        });

        $('input.PLFcalendarSearchKey').on("keyup", function () {
            $("#nonDisplayedCalendarContainer").css("display", "none");
            var seakey = $(this).val();
            $('div.NonDisplayedCalendar').jzLoad(
                    "CalendarPortletController.getSearchResult()",
                    {"key":seakey});
        });
        $('input.PLFcalendarSearchKey').on("focus", function () {
            if ($(this).val() == initKey) {
                $(this).val("");
            }
        });
        $('input.PLFcalendarSearchKey').on("blur", function () {
            if ($(this).val() == "") {
                $(this).val(initKey);
            }
        });
        $('.CalendarItem').mouseover(function () {
            var button = $(this).children("p.addButton");
            button.css("display", "block");
        });
        $('.CalendarItem').mouseout(function () {
            var button = $(this).children("p.addButton");
            button.css("display", "none");
        });
        $('.calendarPortlet').mouseover(function () {
            $('.settingsContainerPage').children("a").css("display", "block");

        });
        $('.calendarPortlet').mouseout(function () {
            $('.settingsContainerPage').children("a").css("display", "none");
        });
        $('.prevDate').on("click", function () {
            $('div.calendarPortletData').jzLoad(
                    "CalendarPortletController.decDate()",
                    {"nbClick":"1"});
        });
        $('.nextDate').on("click", function () {
            $('div.calendarPortletData').jzLoad(
                    "CalendarPortletController.incDate()",
                    {"nbClick":"1"});
        });
    };

    $(document).ready(function () {
        var initKey = $('input.PLFcalendarSearchKey').val();
        init(initKey);
    });
});
