<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.m_member"%>
<c:set var="rowIDLogin" value="<%= session.getAttribute(m_member.ColumnName.ROWID)%>"/>
<c:if test='${rowIDLogin == null}'>
    <c:redirect url="/"/>
</c:if>