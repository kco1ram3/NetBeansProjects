<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlMapJS" value="/js/map.js" />
<c:url var="urlRootPath" value="/" />
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Cache-control" content="no-cache">
        <meta http-equiv="Expires" content="0">
        <title>Photoground</title>
        <link type="text/css" href="css/style.css" rel="stylesheet" />
        <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false">
        </script>
        <script src="js/ajax_framework.js">
        </script>
    </head>
    <body>
        <%@include file="sharedControls/navigation_login.jsp" %>
        <%@include file="sharedControls/navigation_menu.jsp" %>
        <div class="content_header"></div>
        <article>
            <div class="content_main" style="position: relative">
                <div id="search_map">
                    <input type="hidden" id="rootPath" value="${urlRootPath}" />
                    <input type="search" id="address" name="address" />
                    <button class="button" onclick="codeAddress();">
                        <img class="system_icons imgSearch" />ค้นหา
                    </button>
                    <img id="imgLoading" alt="" src="images/loading/arrows16.gif" />
                </div>
                <div id="map-canvas" class="gmap"></div>
            </div>
        </article>
        <footer>
            <div class="content_footer"></div>
        </footer>
        <script src="${urlMapJS}">
        </script>
    </body>
</html>
