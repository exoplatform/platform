$(function () {

    function init(initKey) {

    /*    setTimeout(function()
            {
                $('div.NonDisplayedCalendar').jzLoad(
                    "CalendarPortletController.getSearchResult()",
                    {"key":""});
            }
            ,100);  */

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
        $('.CalendarPortlet').mouseover(function () {
            $('.SettingsContainerPage').children("div").css("display", "block");

        });
        $('.CalendarPortlet').mouseout(function () {
            $('.SettingsContainerPage').children("div").css("display", "none");
        });
        $('span.PrevDateSpan').on("click", function () {

            $('div.CalendarPortletData').jzLoad(
                "CalendarPortletController.decDate()",
                {"nbClick":"1"});

        });
        $('span.NextDateSpan').on("click", function () {
            $('div.CalendarPortletData').jzLoad(
                "CalendarPortletController.incDate()",
                {"nbClick":"1"});

        });
    };

    $(document).ready(function () {
        var initKey = $('input.PLFcalendarSearchKey').val();
        init(initKey);
    });
    function eventDetail(id)
    {
        window.location = "/portal/intranet/calendar" ;
        var href="'UICalendarView','View','&objectId="+id+"',true);"
        eXo.webui.UIForm.submitForm(href,true) ;
    } ;
});
