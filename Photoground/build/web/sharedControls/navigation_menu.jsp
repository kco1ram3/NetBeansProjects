<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page import="entity.m_member"%>
<c:url var="urlHome" value="/Home" />
<c:url var="urlMap" value="/Map" />
<c:url var="urlGallery" value="/Gallery" />
<c:url var="urlUpload" value="/Upload" />
<c:set var="rowIDLogin" value="<%= session.getAttribute(m_member.ColumnName.ROWID)%>"/>
<nav>
    <div class="navigation_menu">
        <div class="text_line">
        </div>
        <div class="menu_button">
            <a href="${urlHome}"><button>Home</button></a>
        </div>
        <div class="menu_button">
            <a href="${urlMap}"><button>Map</button></a>
        </div>
        <c:if test='${rowIDLogin != null}'>
            <div class="menu_button">
                <a href="${urlGallery}"><button>My Gallery</button></a>
            </div>
            <div class="menu_button">
                <a href="${urlUpload}"><button>Upload</button></a>
            </div>
        </c:if>
        <div class="text_line">
        </div>
    </div>
</nav>