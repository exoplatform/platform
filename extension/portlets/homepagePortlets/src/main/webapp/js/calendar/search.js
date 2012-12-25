
$(function () {

    $('input.PLFcalendarSearchKey').on( "click", function() {
        $("#nonDisplayedCalendarContainer").css("display","none");
        var seakey=$(this).val();

        if (seakey == "search your calendar") {
            seakey="";
            $(this).val("");
        }

        $('div.NonDisplayedCalendar').jzLoad(
            "AgendaPortlet.getSearchResult()",
            {"key":seakey});

    });
    $('input.PLFcalendarSearchKey').on('show', function () {
        $("#nonDisplayedCalendarContainer").css("display","none");
        var seakey=$(this).val();
        $('div.NonDisplayedCalendar').jzLoad(
            "AgendaPortlet.getSearchResult()",
            {"key":seakey});
    });
    $('input.PLFcalendarSearchKey').change(function() {
        $("#nonDisplayedCalendarContainer").css("display","none");
        var seakey=$(this).val();
        $('div.NonDisplayedCalendar').jzLoad(
            "AgendaPortlet.getSearchResult()",
            {"key":seakey});
    });
    $('input.PLFcalendarSearchKey').keypress(function() {
        $("#nonDisplayedCalendarContainer").css("display","none");
        var seakey=$(this).val();
        $('div.NonDisplayedCalendar').jzLoad(
            "AgendaPortlet.getSearchResult()",
            {"key":seakey});
    });
    $('input.PLFcalendarSearchKey').on("keydown",function() {
        $("#nonDisplayedCalendarContainer").css("display","none");
        var seakey=$(this).val();
        $('div.NonDisplayedCalendar').jzLoad(
            "AgendaPortlet.getSearchResult()",
            {"key":seakey});
    });

    $('input.PLFcalendarSearchKey').on("keypress",function() {
        $("#nonDisplayedCalendarContainer").css("display","none");
        var seakey=$(this).val();
        $('div.NonDisplayedCalendar').jzLoad(
            "AgendaPortlet.getSearchResult()",
            {"key":seakey});
    });

    $('input.PLFcalendarSearchKey').on("keyup" ,function() {
        $("#nonDisplayedCalendarContainer").css("display","none");
        var seakey=$(this).val();
        $('div.NonDisplayedCalendar').jzLoad(
            "AgendaPortlet.getSearchResult()",
            {"key":seakey});
    });
    $('input.PLFcalendarSearchKey').on("focus" ,function() {
        if ($(this).val() == "search your calendar") {
            $(this).val("");
        }
    });

    $('input.PLFcalendarSearchKey').on("blur",function() {
        if ($(this).val() == "") {
            $(this).val("search your calendar") ;
        }
    });
    $('.CalendarItem').mouseover(function() {
        var button= $(this).children("form").children("p.addButton");

        button.css("display","block");

    });
    $('.CalendarItem').mouseout(function() {
        var button= $(this).children("form").children("p.addButton");
        button.css("display","none");
    });
    $('.CalendarPortlet').mouseover(function() {
        $('.SettingsContainer').children("div").css("display","block");

    });
    $('.CalendarPortlet').mouseout(function() {
        $('.SettingsContainer').children("div").css("display","none");
    });

});
