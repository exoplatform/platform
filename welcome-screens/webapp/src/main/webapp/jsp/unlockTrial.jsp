<%@ page import="org.exoplatform.platform.welcomescreens.TrialService" %>
<%
    int rday = TrialService.getNbDaysBeforeExpiration();
    boolean outdated = TrialService.isOutdated();
    String css="backNotOutdated";
    String label1="You have";
    String label2="days left in your evaluation";
    String productCode=TrialService.getProductCode();
    if (outdated)  {
        css="backOutdated";
        label1= "Your evaluation has expired"  ;
        label2= "days ago";
        rday = TrialService.getNbDaysAfterExpiration();
    }

%>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>Welcome to eXo Platform</title>
</head>
<body>

    <div class="HeaderClass">
          <span class="eXoLogo" style="float:left">

          </span>

          <span style="width:100px;height: 40px;background-color: #ff6347;border-color: red">
               <%=label1 %> <%=rday%> <%=label2 %>
          </span>

    </div>

    <div>
        <span >
            You must own a valid a subscription in order to unlock this eXo-Platform
        </span>
    </div>

    <table>
        <tr>
            <td>
                <span class="oneIcon" >one</span>
            </td>
            <td>
                <span>Pickup your favourite <a> subscription </a> plan</span>

            </td>
        </tr>
        <tr>
           <td>
               <span class="tWoIcon" >two</span>
           </td>
            <td>

                    <span>Grab your product code and request an unlock key</span>
                     <br>
                    <span>Product Code</span> <input type="text" placeholder="">

            </td>
        </tr>
        <tr>
            <td>
                <span class="threeIcon" >three</span>
            </td>
            <td>
                <span>
                   Enter the unlock key below to unlock the product
                </span>
                <br>

                    <form action="/welcome-screens/UnlockServlet" method="post" name="unlockForm">
                        <table>
                            <tr>
                                <td>
                                    <label class="TextForm" id="hashMD5">Unlock Key</label>
                                </td>
                                <td>
                                    <input class="Text" type="text" name="hashMD5" id="hashMD5">
                                </td>
                                <td>
                                    <input type="submit" class="FormSubmit BlueFormRect" value="Unlock Product">
                                </td>
                            </tr>
                            <% if(request.getAttribute("errorMessage") != null && !request.getAttribute("errorMessage").toString().isEmpty()) {%>
                            <tr>
                                <td colspan="3" class="Red">
                                    <%=request.getAttribute("errorMessage").toString() %>
                                </td>
                            </tr>
                            <% }%>
                        </table>
                    </form>

            </td>
        </tr>
    </table>
<div>
    <h5 class="CenterContent">Question about your eXo Platform evaluation?<br>Contact us at <a href="mailto:info@exoplatform.com">info@exoplatform.com</a> or on our website <a href="http://www.exoplatform.com">www.exoplatform.com</a></h5>
</div>
</body>
</html>