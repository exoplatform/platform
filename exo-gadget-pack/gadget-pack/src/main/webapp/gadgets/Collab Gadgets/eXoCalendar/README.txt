                    
                          Calendar Gadget                          


  What is it?
  -----------
  Calendar Gadget is a gadget complied with Google Gadget and OpenSocial API. Based on eXo Calendar application and inspired from Google Calendar Gadget, this gadget is built to help user work and manage eXo Calendar as easily as posible.
  
  Authors
  -----------
    Lê Thanh Quang - eXo CT member & Đỗ  Hoàng Khiêm - eXo CT member

  Features
  -----------
  The gadget's features include:
      * Displaying user's calendars on a visual board and highlight event dates
      * Rendering events by date in rich flexible scrollbar with several user interations.
      * Supporting to add event to the calendar quickly.
      * User preference allows user to choose visible calendars.

  In the future
  -----------
  Short-term:
    * Integrate the gadget on iGoogle.
    * Revamp user interface.
    * Support public calendars.
    * Manage time-zone.
    * install i18n.
  Long-term:
    * Support other views(week, year) on Gadget canvas mode.
    * Support remote calendars.
      
  Installation
  -----------
  The binary includes following parts:
      * "calgad" folder is folder of the gadget in war structure.
      * "calgad.war" file is war deployment of the gadget.
      * "calgad.groovy" file is code of Restful service.

  The deployment includes 2 parts: deploying web service and deploying Calendar gadget.

     * To deploy web Service, upload "calgad.groovy" to eXo IDE and deploy it as a Restful service.

     * The gadget is deploy by one of following ways:
          * Upload "calgad" folder to any host to be accessed from Gadget server. In gadget board(such as eXo Dashboard), use "remote add gadget" feature with the link is "[the host path]/calgad/calendargadget/org.exoplatform.calendar.client.CalendarGadget.gadget.xml".
          * Deploy "calgad.war" to the server of GateIn portal. The gadget will be registered to the portal automatically.
  
  *Note: Because of web service deployment, the rest url is not a constant but depends on individual circumstances. The gadget uses "http://localhost:8080/rest" as default value as well as read new value from "restUrl" UserPref. Therefore, in particular case, the administrator can notice users to update this value in their gadget.



