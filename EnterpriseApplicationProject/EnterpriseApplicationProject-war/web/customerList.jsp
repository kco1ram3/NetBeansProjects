<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlcustomerInfo" value="/CustomerInfo" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Customer List</title>
        <style>
            table td {
                border: black solid thin;
            }
            
            table th {
                background-color: black;
                color: white;
            }
        </style>
    </head>
    <body>
        <p>
            <a href="${urlcustomerInfo}">Create Customer</a>
        </p>
        <table width="100%" style="border: black solid thin;" cellpadding="0" cellspacing="0">
            <caption>Customer List</caption>
            <tr>
                <th>id</th>
                <th>name</th>
                <th>surname</th>
                <th>email</th>
                <th>phone</th>
                <th>create date</th>
                <th>update date</th>
                <th>edit</th>
                <th>delete</th>
            </tr>
            <c:forEach var="cus" items="${customerList}">
                <tr>
                    <td style="text-align: center;"><c:out value="${cus.id}"/></td>
                    <td><c:out value="${cus.name}"/></td>
                    <td><c:out value="${cus.surname}"/></td>
                    <td><c:out value="${cus.email}"/></td>
                    <td><c:out value="${cus.phone}"/></td>
                    <td style="text-align: center;"><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${cus.createDate}" /></td>
                    <td style="text-align: center;"><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${cus.updateDate}" /></td>
                    <td style="text-align: center;"><a href="${urlcustomerInfo}?id=${cus.id}">edit</a></td>
                    <td style="text-align: center;"><a href="${urlcustomerInfo}?id=${cus.id}&flag=delete" onclick="javascript: return confirm('confirm delete ?'); return false;">delete</a></td>
                </tr>
            </c:forEach>
        </table>
    </body>
</html>
