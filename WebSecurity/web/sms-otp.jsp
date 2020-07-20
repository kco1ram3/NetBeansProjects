<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlSmsOtp" value="/SMS-OTP" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Cache-control" content="no-cache">
        <meta http-equiv="Expires" content="0">
        <title>Web Security</title>
        <%@include file="sharedControls/master_script.jsp" %>
        <%@include file="sharedControls/master_style.jsp" %>
    </head>
    <body>
        <form method="POST" target="SmsOtpAction" action="${urlSmsOtp}">
            <table align="center" style="font-size: small;">
                <caption style="white-space: nowrap;">กรุณาระบุรหัสรักษาความปลอดภัย SMS-OTP เพื่อยืนยันการทำรายการ</caption>
                <tr>
                    <td>รหัสอ้างอิง : </td>
                    <td>
                        <label>${otpReference}</label>
                        <input type="hidden" name="otpReference" name="otpReference" value="${otpReference}" />
                    </td>
                </tr>
                <tr>
                    <td><label for="otpPassword">รหัสรักษาความปลอดภัย SMS-OTP : </label></td>
                    <td>
                        <input type="password" id="otpPassword" name="otpPassword" />
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <button type="submit" id="btnNext" name="btnNext">Next</button>
                    </td>
                </tr>
            </table>
        </form>
        <iframe name="SmsOtpAction" style="display: none;"></iframe>
    </body>
</html>
