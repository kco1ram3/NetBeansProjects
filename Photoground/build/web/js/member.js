function checkRegister() { 
    var isValid = true;
    var name = document.getElementById("name");
    var surname = document.getElementById("surname");
    var email = document.getElementById("email");
    var username_register = document.getElementById("username_register");
    var password_register = document.getElementById("password_register");
    var password_confirm = document.getElementById("password_confirm");
    var regEmail = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/igm;
    if (name.value === "") {
        name.className = "invalid_textbox";
        isValid = false;
    } else {
        name.className = name.className.replace("invalid_textbox", "");
    }
    if (surname.value === "") {
        surname.className = "invalid_textbox";
        isValid = false;
    } else {
        surname.className = surname.className.replace("invalid_textbox", "");
    }
    if (email.value === "" || !regEmail.test(email.value)) {
        email.className = "invalid_textbox";
        isValid = false;
    } else {
        email.className = email.className.replace("invalid_textbox", "");
    }
    if (username_register.value === "") {
        username_register.className = "invalid_textbox";
        isValid = false;
    } else {
        username_register.className = username_register.className.replace("invalid_textbox", "");
    }
    if (password_register.value === "") {
        password_register.className = "invalid_textbox";
        isValid = false;
    } else {
        password_register.className = password_register.className.replace("invalid_textbox", "");
    }
    if (password_confirm.value === "" || password_confirm.value !== password_register.value) {
        password_confirm.className = "invalid_textbox";
        isValid = false;
    } else {
        password_confirm.className = password_confirm.className.replace("invalid_textbox", "");
    }
    return isValid;
    //document.forms['login'].submit();
}