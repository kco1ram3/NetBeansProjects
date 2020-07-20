<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlcustomerInfo" value="/CustomerInfo" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Customer Info</title>
    </head>
    <body>
        <form method="POST" action="${urlcustomerInfo}">
            <input type="hidden" name="id" value="${id}" />
            <table align="center" style="font-size: small; border: black solid thin;">
                <caption style="background-color: black; color: white">Customer Info</caption>
                <tr>
                    <td><label for="name">Name : </label></td>
                    <td><input type="text" id="name" name="name" autocomplete="off" value="${customer.name}" /></td>
                </tr>
                <tr>
                    <td><label for="surname">Surname : </label></td>
                    <td><input type="text" id="surname" name="surname" autocomplete="off" value="${customer.surname}" /></td>
                </tr>
                <tr>
                    <td><label for="email">Email : </label></td>
                    <td><input type="email" id="email" name="email" autocomplete="off" value="${customer.email}" /></td>
                </tr>
                <tr>
                    <td><label for="phone">Phone : </label></td>
                    <td><input type="text" id="phone" name="phone" autocomplete="off" value="${customer.phone}" /></td>
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
    </body>
</html>
