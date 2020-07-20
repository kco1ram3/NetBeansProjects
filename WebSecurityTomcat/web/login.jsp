<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlLogin" value="/Login" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Cache-control" content="no-cache">
        <meta http-equiv="Expires" content="0">
        <title>Web Security</title>
        <%@include file="sharedControls/master_script.jsp" %>
    </head>
    <body>
        <form method="POST" target="loginAction" action="${urlLogin}">
            <table align="center" style="font-size: small;">
                <caption>Welcome to Web Security</caption>
                <tr>
                    <td><label for="username_login">Username : </label></td>
                    <td>
                        <input type="text" id="username_login" name="username_login" autocomplete="off" />
                    </td>
                </tr>
                <tr>
                    <td><label for="password_login">Password : </label></td>
                    <td>
                        <input type="password" class="qwerty" id="password_login" name="password_login" onfocus="this.blur();" />
                        <img id="imgKeyboard" src="<c:url value="/js/Keyboard-master/demo/keyboard.png" />" style="cursor: pointer;" />
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <button type="submit" id="btnLogin" name="btnLogin">Login</button>
                        <a href="<c:url value="/Register" />"><button type="button" id="btnRegister" name="btnRegister">Register</button></a>
                    </td>
                </tr>
            </table>
        </form>
        <iframe name="loginAction" style="display: none;"></iframe>
    </body>
    <script>
        $(document).ready(function() {
            $("#password_login")
                    .keyboard({
                        openOn: null,
                        stayOpen: true,
                        layout: "qwerty"
                    })
                    .addTyping();
            $("#imgKeyboard").click(function() {
                $("#password_login").getkeyboard().reveal();
            });
        });
    </script>
</html>
