
$(function () {

     function init(initKey){

    $('span.addButtonSpan').on("click", function() {

        var calId=$(this).parent().children("input").val();

        $('div.SettingsContainer').jzLoad(
            "AgendaPortlet.addCalendar()",
            {"calendarId":calId});

    });
         $('span.deleteButtonSpan').on("click", function() {

             var calId=$(this).parent().children("input").val();

             $('div.SettingsContainer').jzLoad(
                 "AgendaPortlet.deleteCalendar()",
                 {"calendarId":calId});

         });
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
        var button= $(this).children("p.addButton");

        button.css("display","block");

    });
    $('.CalendarItem').mouseout(function() {
        var button= $(this).children("p.addButton");
        button.css("display","none");
    });
    $('.CalendarPortlet').mouseover(function() {
        $('.SettingsContainer').children("div").css("display","block");

    });
    $('.CalendarPortlet').mouseout(function() {
        $('.SettingsContainer').children("div").css("display","none");
    });
         $('span.PrevDateLink').on("click", function() {

             $('div.CalendarPortlet').jzLoad(
                 "AgendaPortlet.decDate()",
                 {"nbClick":"1"});

         });
         $('span.NextDateLink').on("click", function() {



             $('div.CalendarPortlet').jzLoad(
                 "AgendaPortlet.incDate()",
                 {"nbClick":"1"});

         });
     };

    $(document).ready(function(){
        var initKey=$('input.PLFcalendarSearchKey').val();
        init(initKey) ;
    });
});
