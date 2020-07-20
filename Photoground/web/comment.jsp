<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:forEach var="comment" items="${comments}">
    <div id="comment">
        <div style="margin: 0 15px;">
            &nbsp;
            <div class="comment_header">
                <span class="comment_icon comment_date"><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${comment.commentDate}" /></span>
                <span class="comment_icon comment_user">${comment.username}</span>
            </div>
            <div class="comment_message">${comment.comment}</div>&nbsp;
        </div>
    </div>
    <br />
</c:forEach>
