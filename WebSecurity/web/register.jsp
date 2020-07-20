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
        <%@include file="sharedControls/master_style.jsp" %>
    </head>
    <body>
        <form method="POST" target="registerAction" action="${urlRegister}" onsubmit="return checkRegister();">
            <table align="center" style="font-size: small; border: black solid thin;">
                <caption style="background-color: black; color: white">Register</caption>
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
                        <img id="imgKeyboard1" src="<c:url value="/js/Keyboard-master/demo/keyboard.png" />" style="cursor: pointer;" />
                        <div id="complexity" class="default">Enter a password</div>
                    </td>
                </tr>
                <tr>
                    <td><label for="retype_password">Retype Password : </label></td>
                    <td>
                        <input type="password" class="qwerty" id="retype_password" name="retype_password" onfocus="this.blur();" />
                        <img id="imgKeyboard2" src="<c:url value="/js/Keyboard-master/demo/keyboard.png" />" style="cursor: pointer;" />
                    </td>
                </tr>
                <tr>
                    <td><label for="defaultReal">Please enter the letters displayed : </label></td>
                    <td>
                        <input type="text" id="defaultReal" name="defaultReal" autocomplete="off" />
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <input type="submit" value="Save" />
                        <button type="button" onclick="javascript: history.back(-1);">Cancel</button>
                    </td>
                </tr>
            </table>
        </form>
        <iframe name="registerAction" style="display: none;"></iframe>
    </body>
    <script>
        $(document).ready(function() {
            $(".qwerty")
                    .keyboard({
                        openOn: null,
                        stayOpen: true,
                        layout: "qwerty"
                    })
                    .addTyping();
            $("#imgKeyboard1").click(function() {
                $("#password").getkeyboard().reveal();
            });
            $("#imgKeyboard2").click(function() {
                $("#retype_password").getkeyboard().reveal();
            });
            setLetters();
            $("#password").bind("change", checkVal);
        });

        function setLetters() {
            try {
                $('#defaultReal').realperson('destroy');
            } catch (e) {

            }
            $('#defaultReal').realperson({chars: $.realperson.alphanumeric});
            $('#defaultReal').val('');
        }

        function checkRegister() {
            var isValid = true;
            var name = $("#name");
            var surname = $("#surname");
            var email = $("#email");
            var mobile = $("#mobile");
            var username = $("#username");
            var password = $("#password");
            var retype_password = $("#retype_password");
            var defaultReal = $("#defaultReal");
            var regMobile = /^\d+$/;
            var regEmail = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/igm;
            if (name.val().length < 1) {
                name.addClass("invalid_textbox");
                isValid = false;
            } else {
                name.removeClass("invalid_textbox");
            }
            if (surname.val().length < 1) {
                surname.addClass("invalid_textbox");
                isValid = false;
            } else {
                surname.removeClass("invalid_textbox");
            }
            if (email.val().length < 1 || !regEmail.test(email.val())) {
                email.addClass("invalid_textbox");
                isValid = false;
            } else {
                email.removeClass("invalid_textbox");
            }
            if (mobile.val().length < 1 || !regMobile.test(mobile.val())) {
                mobile.addClass("invalid_textbox");
                isValid = false;
            } else {
                mobile.removeClass("invalid_textbox");
            }
            if (username.val().length < 1) {
                username.addClass("invalid_textbox");
                isValid = false;
            } else {
                username.removeClass("invalid_textbox");
            }
            if (password.val().length < 1 || score < 100) {
                password.addClass("invalid_textbox");
                isValid = false;
            } else {
                password.removeClass("invalid_textbox");
            }
            if (retype_password.val().length < 1 || retype_password.val() !== password.val()) {
                retype_password.addClass("invalid_textbox");
                isValid = false;
            } else {
                retype_password.removeClass("invalid_textbox");
            }
            if (defaultReal.val().length < 1) {
                defaultReal.addClass("invalid_textbox");
                isValid = false;
            } else {
                defaultReal.removeClass("invalid_textbox");
            }
            return isValid;
        }

        var charPassword;
        var complexity = $("#complexity");
        var minPasswordLength = ${minPasswordLength};
        var score = 0;

        var num = {};

        function checkVal()
        {
            charPassword = $("#password").val();
            num.Lower = 0;
            num.Upper = 0;
            num.Digits = 0;
            num.Symbols = 0;
            analyzeString();
            outputResult();
        }

        function analyzeString()
        {
            for (i = 0; i < charPassword.length; i++)
            {
                if ((/[a-z]/g).test(charPassword[i])) {
                    num.Lower = 25;
                }
                if ((/[A-Z]/g).test(charPassword[i])) {
                    num.Upper = 25;
                }
                if ((/[0-9]/g).test(charPassword[i])) {
                    num.Digits = 25;
                }
                if ((/(.*[!,@,#,$,%,^,&,*,?,_,~])/).test(charPassword[i])) {
                    num.Symbols = 25;
                }
            }
            score = num.Lower + num.Upper + num.Digits + num.Symbols;
        }

        function outputResult()
        {
            complexity.removeClass("default").removeClass("weak").removeClass("strong").removeClass("stronger").removeClass("strongest");
            if (charPassword.length < 1)
            {
                complexity.html("Enter a password").addClass("default");
            }
            else if (charPassword.length < minPasswordLength)
            {
                complexity.html("At least " + minPasswordLength + " characters please!").addClass("weak");
            }
            else if (score < 50)
            {
                complexity.html("Weak!").addClass("weak");
            }
            else if (score >= 50 && score < 75)
            {
                complexity.html("Average!").addClass("strong");
            }
            else if (score >= 75 && score < 100)
            {
                complexity.html("Strong!").addClass("stronger");
            }
            else if (score >= 100)
            {
                complexity.html("Secure!").addClass("strongest");
            }
        }
    </script>
</html>
