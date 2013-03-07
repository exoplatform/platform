<%@ page import="org.exoplatform.platform.welcomescreens.service.UnlockService" %>
<%
    int rday = UnlockService.getNbDaysBeforeExpiration();
    boolean outdated = UnlockService.isOutdated();
    String css="backNotOutdated";
    String label1="You have";
    String label2="days left in your evaluation";
    String productCode= UnlockService.getProductCode();
    if (outdated)  {
        css="backOutdated";
        label1= "Your evaluation has expired"  ;
        label2= "days ago";
        rday = UnlockService.getNbDaysAfterExpiration();
    }

%>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>Welcome to eXo Platform</title>
    <link rel="shortcut icon" type="image/x-icon"  href="/welcome-screens/favicon.ico" />
    <link rel="stylesheet" type="text/css" href="/welcome-screens/css/Stylesheet.css"/>
    <script type='text/javascript'>
        function formValidation() {
            if(document.unlockForm.hashMD5.value!="")
                return true;
            else {
                ERROR.innerHTML="Unlock key is mondatory";
                var elem = document.getElementById("KEYERROR");
                if (elem!=null) elem.style.display = "none";
                return false;
            }
        }
    </script>
</head>
<body>
<div class="UIBanner">
    <div class="BannerContent ClearFix">
        <h1 class="BannerTitle FR"> <%=label1%> <span class="YellowColor"> <%=rday%> </span><%=label2%></h1>
        <img src="/welcome-screens/css/background/Logo.png" alt="Evaluation"/>
    </div>
</div>
<div class="UIContent">
    <h2 class="CenterTitle">You must own a valid subscription in order to unlock this eXo-Platform instance</h2>

    <div class="Container ClearFix">
        <span class="TextContainer">1- Pickup your favorite <a class="" href="<%=UnlockService.getSubscriptionUrl()%>" target="_blank">subscription</a> plan</span>
        <span class="TriangleItem OrangeIcon"></span>
    </div>
    <form action="/welcome-screens/UnlockServlet" method="post" name="unlockForm" onsubmit="return formValidation();">
    <div class="Container ClearFix">

        <span class="TextContainer">2- Grab your product code and request an unlock key</span>
        <br>
        <span>Product Code</span> <input type="text" class="Text"  name="pc" value="<%=UnlockService.getProductCode() %>">  <a class="Botton BlueRect" target="_blank" href="<%=UnlockService.getRegistrationFormUrl()%>?'pc=<%=UnlockService.getProductCode()%>'">Request a Key</a>
        <span class="TriangleItem BlueIcon"></span>
    </div>

    <div class="Container ClearFix">
        <span>

            <span class="TextContainer">3- Enter your unlock key below to unlock the product <br> </span>
            <div class="FormContainer">

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
                        <tr>
                            <td colspan="3" class="Red">
                                <span  id="ERROR"> </span>
                            </td>
                        </tr>
                        <% if(request.getAttribute("errorMessage") != null && !request.getAttribute("errorMessage").toString().isEmpty()) {%>
                        <tr>
                            <td colspan="3" class="Red">
                                <span id="KEYERROR" style="display: block"><%=request.getAttribute("errorMessage").toString() %> </span>
                            </td>
                        </tr>

                        <% }%>
                    </table>
            </div>
            </span>
        <span class="TriangleItem GreenIcon"></span>
    </div>
                </form>



</div>
</body>
</html>