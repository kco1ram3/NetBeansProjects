<%-- 
    Document   : memberUpdate
    Created on : Aug 18, 2013, 6:06:41 PM
    Author     : Administrator
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form method="POST" action="MemberUpdateServlet.do">
            <%--
            <c:set var="now" value="<%=new java.util.Date()%>" />
            <c:choose>
                <c:when test="${member.registerDate != null}">
                   <fmt:formatDate var="registerDate" pattern="dd-MM-yyyy hh:mm:ss" value="${member.registerDate}" />
                </c:when>
                <c:otherwise>
                   <fmt:formatDate var="registerDate" pattern="dd-MM-yyyy hh:mm:ss" value="${now}" />
                </c:otherwise>
            </c:choose>
            --%>
            <input type="hidden" name="rowID" value="${rowID}" />
            <input type="hidden" name="registerDate" value="${registerDate}" />
            <h3>ชื่อ&nbsp;:</h3>
            <input type="text" name="firstName" value="${member.firstName}" />
            <h3>นามสกุล&nbsp;:</h3>
            <input type="text" name="lastName" value="${member.lastName}" />
            <h3>อีเมล&nbsp;:</h3>
            <input type="text" name="email" value="${member.email}" />
            <h3>ชื่อผู้ใช้&nbsp;:</h3>
            <input type="text" name="username" value="${member.username}" />
            <h3>รหัสผ่าน&nbsp;:</h3>
            <input type="text" name="password" value="${member.password}" />
            <p>
                <input type="submit" value="บันทึกข้อมูล" />
                <input type="button" value="ย้อนกลับ" onclick="javascript: history.back(-1);" />
            </p>
        </form>
    </body>
</html>
