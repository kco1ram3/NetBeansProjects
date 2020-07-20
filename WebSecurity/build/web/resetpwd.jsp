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
        <c:choose>
            <c:when test='${showForm}'>
                <form method="POST" target="resetPasswordAction" action="<c:url value="/ResetPass" />" onsubmit="return validateForm();">
                    <table align="center" style="font-size: small; border: black solid thin;">
                        <caption style="background-color: black; color: white">Reset Password</caption>
                        <tr>
                            <td>OTP Reference : </td>
                            <td>
                                ${otpReference}
                            </td>
                        </tr>
                        <tr>
                            <td><label for="otp_password">OTP Password : </label></td>
                            <td>
                                <input type="password" class="qwerty" id="otp_password" name="otp_password" onfocus="this.blur();" />
                                <img id="imgKeyboard1" src="<c:url value="/js/Keyboard-master/demo/keyboard.png" />" style="cursor: pointer;" />
                            </td>
                        </tr>
                        <tr>
                            <td><label for="new_password">New Password : </label></td>
                            <td>
                                <input type="password" class="qwerty" id="new_password" name="new_password" onfocus="this.blur();" />
                                <img id="imgKeyboard2" src="<c:url value="/js/Keyboard-master/demo/keyboard.png" />" style="cursor: pointer;" />
                                <div id="complexity" class="default">Enter a new password</div>
                            </td>
                        </tr>
                        <tr>
                            <td><label for="retype_new_password">Retype New Password : </label></td>
                            <td>
                                <input type="password" class="qwerty" id="retype_new_password" name="retype_new_password" onfocus="this.blur();" />
                                <img id="imgKeyboard3" src="<c:url value="/js/Keyboard-master/demo/keyboard.png" />" style="cursor: pointer;" />
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <input type="submit" id="btnResetPassword" name="btnResetPassword" value="Reset Password" />
                            </td>
                        </tr>
                    </table>
                    <input type="hidden" id="action" name="aciton" value="resetPassword" />
                </form>
                <iframe name="resetPasswordAction" style="display: none;"></iframe>
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
                            $("#otp_password").getkeyboard().reveal();
                        });
                        $("#imgKeyboard2").click(function() {
                            $("#new_password").getkeyboard().reveal();
                        });
                        $("#imgKeyboard3").click(function() {
                            $("#retype_new_password").getkeyboard().reveal();
                        });
                        $("#new_password").bind("change", checkVal);
                    });

                    function validateForm() {
                        var isValid = true;
                        var otp_password = $("#otp_password");
                        var new_password = $("#new_password");
                        var retype_new_password = $("#retype_new_password");
                        if (otp_password.val().length < 1) {
                            otp_password.addClass("invalid_textbox");
                            isValid = false;
                        } else {
                            otp_password.removeClass("invalid_textbox");
                        }
                        if (new_password.val().length < 1 || score < 100) {
                            new_password.addClass("invalid_textbox");
                            isValid = false;
                        } else {
                            new_password.removeClass("invalid_textbox");
                        }
                        if (retype_new_password.val().length < 1 || retype_new_password.val() !== new_password.val()) {
                            retype_new_password.addClass("invalid_textbox");
                            isValid = false;
                        } else {
                            retype_new_password.removeClass("invalid_textbox");
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
                        charPassword = $("#new_password").val();
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
                            complexity.html("Enter a new password").addClass("default");
                        }
                        else if (charPassword.length < minPasswordLength) {
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
            </c:when>
            <c:otherwise>
                <form method="POST" action="<c:url value="/ResetPass" />" onsubmit="return validateForm();">
                    <table align="center" style="font-size: small; border: black solid thin;">
                        <caption style="background-color: black; color: white">Please specify Username</caption>
                        <tr>
                            <td><label for="username">Username : </label></td>
                            <td>
                                <input type="text" id="username" name="username" autocomplete="off" />
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <input type="submit" id="btnSendOTP" name="btnSendOTP" value="Send OTP" />
                                <a href="<c:url value="/" />"><button type="button" id="btnCancel" name="btnCancel">Cancel</button></a>
                            </td>
                        </tr>
                    </table>
                </form>
                <script>
                    function validateForm() {
                        var isValid = true;
                        var username = $("#username");
                        if (username.val().length < 1) {
                            username.addClass("invalid_textbox");
                            isValid = false;
                        } else {
                            username.removeClass("invalid_textbox");
                        }
                        return isValid;
                    }
                </script>
            </c:otherwise>
        </c:choose>
    </body>
</html>
