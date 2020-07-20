<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlRegister" value="/Register" />
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
        <form method="POST" target="registerAction" action="${urlRegister}">
            <table align="center" style="font-size: small;">
                <caption>Register</caption>
                <tr>
                    <td><label for="name">Name : </label></td>
                    <td><input type="text" id="name" name="name" autocomplete="off" /></td>
                </tr>
                <tr>
                    <td><label for="surname">Surname : </label></td>
                    <td><input type="text" id="surname" name="surname" autocomplete="off" /></td>
                </tr>
                <tr>
                    <td><label for="email">Email : </label></td>
                    <td><input type="email" id="email" name="email" autocomplete="off" /></td>
                </tr>
                <tr>
                    <td><label for="mobile">Mobile : </label></td>
                    <td><input type="text" id="mobile" name="mobile" autocomplete="off" /></td>
                </tr>
                <tr>
                    <td><label for="username">Username : </label></td>
                    <td><input type="text" id="username" name="username" autocomplete="off" /></td>
                </tr>
                <tr>
                    <td><label for="password">Password : </label></td>
                    <td>
                        <input type="password" class="qwerty" id="password" name="password" onfocus="this.blur();" />
                        <img id="imgKeyboard" src="<c:url value="/js/Keyboard-master/demo/keyboard.png" />" style="cursor: pointer;" />
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <button type="submit">Save</button>
                        <button type="button" onclick="javascript: history.back(-1);">Cancel</button>
                    </td>
                </tr>
            </table>
        </form>
        <iframe name="registerAction" style="display: none;"></iframe>
    </body>
    <script>
        $(document).ready(function() {
            $("#password")
                    .keyboard({
                        openOn: null,
                        stayOpen: true,
                        layout: "qwerty"
                    })
                    .addTyping();
            $("#imgKeyboard").click(function() {
                $("#password").getkeyboard().reveal();
            });
        });
    </script>
</html>
