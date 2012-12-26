
$(function () {

     function init(initKey){

    $('input.PLFcalendarSearchKey').on( "click", function() {
        $("#nonDisplayedCalendarContainer").css("display","none");
        var seakey=$(this).val();

        if (seakey == initKey) {
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
        if ($(this).val() == initKey) {
            $(this).val("");
        }
    });

    $('input.PLFcalendarSearchKey').on("blur",function() {
        if ($(this).val() == "") {
            $(this).val(initKey) ;
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
     };

    $(document).ready(function(){
        var initKey=$('input.PLFcalendarSearchKey').val();
        init(initKey) ;
    });
});
