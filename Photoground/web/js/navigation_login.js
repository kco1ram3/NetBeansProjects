function checkLogin() {
    var isValid = true;
    var username_login = document.getElementById("username_login");
    var password_login = document.getElementById("password_login");
    if (username_login.value === "") {
        username_login.className = "invalid_textbox";
        isValid = false;
    } else {
        username_login.className = username_login.className.replace("invalid_textbox", "");
    }
    if (password_login.value === "") {
        password_login.className = "invalid_textbox";
        isValid = false;
    } else {
        password_login.className = password_login.className.replace("invalid_textbox", "");
    }
    return isValid;
    //document.forms['login'].submit();
}