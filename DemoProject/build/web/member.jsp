<%-- 
    Document   : member
    Created on : Aug 18, 2013, 5:14:34 PM
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
        <c:url var="url" value="/MemberUpdateServlet.do" />
        <a href="${url}">เพิ่มข้อมูลสมาชิก</a>
        <table width="100%" style="border: black solid thin;">
            <tr>
                <th>ลำดับ</th>
                <th>ชื่อ</th>
                <th>นามสกุล</th>
                <th>อีเมล</th>
                <th>ชื่อผู้ใช้</th>
                <th>รหัสผ่าน</th>
                <th>วันที่สมัครสมาชิก</th>
                <th>วันที่แก้ไขข้อมูลล่าสุด</th>
                <th>แก้ไข</th>
                <th>ลบ</th>
            </tr>
            <c:forEach var="member" items="${members}">
                <tr>
                    <td><c:out value="${member.rowID}"/></td>
                    <td><c:out value="${member.firstName}"/></td>
                    <td><c:out value="${member.lastName}"/></td>
                    <td><c:out value="${member.email}"/></td>
                    <td><c:out value="${member.username}"/></td>
                    <td><c:out value="${member.password}"/></td>
                    <td><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${member.registerDate}" /></td>
                    <td><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${member.updateDate}" /></td>
                    <td><a href="${url}?rowID=${member.rowID}">แก้ไข</a></td>
                    <td><a href="${url}?rowID=${member.rowID}&flag=delete" onclick="javascript: return confirm('ยืนยันการลบข้อมูล'); return false;">ลบ</a></td>
                </tr>
            </c:forEach>
        </table>
        ${pageSize}
    </body>
</html>
