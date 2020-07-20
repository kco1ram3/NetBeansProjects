<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlStyle" value="/css/style.css" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Cache-control" content="no-cache">
        <meta http-equiv="Expires" content="0">
        <title>Photoground</title>
        <link type="text/css" href="${urlStyle}" rel="stylesheet" rel="stylesheet" />
    </head>
    <body>
        <%@include file="sharedControls/navigation_login.jsp" %>
        <%@include file="sharedControls/navigation_menu.jsp" %>
        <div class="content_header"></div>
        <article>
            <div class="content_main">
                <div class="text_line">
                </div>
                <c:url var="urlPhoto" value="/Photo" />
                <c:set var="items" value="0"/>
                <c:forEach var="photo" items="${photos}">
                    <c:if test='${items == 0}'>
                        <div>
                    </c:if>
                    <div id="image" class="thumbnail">
                        <a href="${urlPhoto}/${photo.rowID}" target="_parent">
                            <img alt="" src="${photo.fileThumbnailPath}" border="0" />
                        </a>
                        <br />
                        <span class="comment_icon comment_no">
                            <fmt:formatNumber type="number" maxFractionDigits="0" value="${photo.numOfComment}" />
                        </span>
                    </div>
                    <c:set var="items" value="${items + 1}"/>
                    <c:if test='${items == 4}'>
                        </div>
                        <c:set var="items" value="0"/>
                    </c:if>
                </c:forEach>
                <c:if test='${items != 0}'>
                    </div>
                </c:if>
                <div class="text_line">
                </div>
                <center>${pageSize}</center>
            </div>
        </article>
        <footer>
            <div class="content_footer"></div>
        </footer>
    </body>
</html>
