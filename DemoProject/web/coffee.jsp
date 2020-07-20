<%-- 
    Document   : coffee
    Created on : Aug 17, 2013, 11:36:29 PM
    Author     : Administrator
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h2>Coffee Advisor Output</h2>
        <c:forEach var="type" items="${types}">
            <c:out value="${type}"/>
            <br />
        </c:forEach>
    </body>
</html>
