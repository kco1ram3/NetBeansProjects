<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlStyle" value="/css/style.css" />
<c:url var="urlImageEdit" value="/images/list_edit.png" />
<c:url var="urlImageDelete" value="/images/trash-can-delete.png" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Cache-control" content="no-cache">
        <meta http-equiv="Expires" content="0">
        <title>Photoground</title>
        <link type="text/css" href="${urlStyle}" rel="stylesheet" />
    </head>
    <body>
        <%@include file="sharedControls/navigation_login.jsp" %>
        <%@include file="sharedControls/navigation_menu.jsp" %>
        <div class="content_header"></div>
        <article>
            <div class="content_main">
                <c:url var="url" value="/Upload" />
                <c:url var="urlPhoto" value="/Photo" />
                <table align="center" width="90%" style="font-size: small;">
                    <tr>
                        <th width="25%">รูปภาพ</th>
                        <th width="25%">วันที่อัพโหลด</th>
                        <th width="40%">ชื่อรูป</th>
                        <th width="5%" align="center">แก้ไข</th>
                        <th width="5%" align="center">ลบ</th>
                    </tr>
                    <c:forEach var="photo" items="${photos}">
                        <tr>
                            <td><a href="${urlPhoto}/${photo.rowID}" target="_blank"><img alt="" src="${photo.fileThumbnailPath}" border="0" /></a></td>
                            <td><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${photo.createDate}" /></td>
                            <td>${photo.photoName}</td>
                            <td align="center"><a href="${url}/${photo.rowID}"><img alt="Edit" src="${urlImageEdit}" border="0" /></a></td>
                            <td align="center"><a href="${url}/d/${photo.rowID}" onclick="javascript: return confirm('ยืนยันการลบข้อมูล');
                                return false;"><img alt="Delete" src="${urlImageDelete}" border="0" /></a></td>
                        </tr>
                    </c:forEach>
                </table>
                <center>${pageSize}</center>
            </div>
        </article>
        <footer>
            <div class="content_footer"></div>
        </footer>
    </body>
</html>
