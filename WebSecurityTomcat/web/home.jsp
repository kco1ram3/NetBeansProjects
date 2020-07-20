<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlChangePassword" value="/ChangePassword" />
<c:url var="urlSmsOtp" value="/SMS-OTP" />
<c:url var="urlLogout" value="/Logout" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Cache-control" content="no-cache">
        <meta http-equiv="Expires" content="0">
        <title>Web Security</title>
        <%@include file="sharedControls/check_login.jsp" %>
        <%@include file="sharedControls/master_script.jsp" %>
    </head>
    <body>
        <a href="${urlChangePassword}"><button>Change Password</button></a>
        <a href="${urlSmsOtp}"><button>Request OTP</button></a>
        <a href="${urlLogout}" target="frameAction" 
           onclick="javascript: return confirm('ยืนยันการออกจากระบบ'); return false;">
            <button type="button" >Logout</button>
        </a>
        <iframe name="frameAction" style="display: none;"></iframe>
    </body>
</html>
