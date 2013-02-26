$(function () {
        setTimeout(function()
            {
                $('div.CalendarPortletContainer').jzLoad(
                    "CalendarPortletController.calendarHome()");
            },100);
});
