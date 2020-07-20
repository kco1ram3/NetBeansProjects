<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlChangePassword" value="/ChangePassword" />
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
        <form method="POST" target="changePasswordAction" action="${urlChangePassword}">
            <table align="center" style="font-size: small;">
                <caption>Change Password</caption>
                <tr>
                    <td><label for="new_password">New Password : </label></td>
                    <td>
                        <input type="password" class="qwerty" id="new_password" name="new_password" onfocus="this.blur();" />
                        <img id="imgKeyboard" src="<c:url value="/js/Keyboard-master/demo/keyboard.png" />" style="cursor: pointer;" />
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <button type="submit" id="btnSave" name="btnSave">Save</button>
                        <button type="button" onclick="javascript: history.back(-1);">Cancel</button>
                    </td>
                </tr>
            </table>
        </form>
        <iframe name="changePasswordAction" style="display: none;"></iframe>
    </body>
    <script>
        $(document).ready(function() {
            $("#new_password")
                    .keyboard({
                        openOn: null,
                        stayOpen: true,
                        layout: "qwerty"
                    })
                    .addTyping();
            $("#imgKeyboard").click(function() {
                $("#new_password").getkeyboard().reveal();
            });
        });
    </script>
</html>
