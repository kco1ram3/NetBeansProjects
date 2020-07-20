<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.m_member"%>
<c:url var="urlStyle" value="/css/style.css" />
<c:url var="urlAjaxFramework" value="/js/ajax_framework.js" />
<c:url var="urlPhotoJS" value="/js/photo.js" />
<c:url var="urlComment" value="/Comment" />
<c:url var="urlImageLoadingComment" value="/images/loading/bar120.gif" />
<c:set var="rowIDLogin" value="<%= session.getAttribute(m_member.ColumnName.ROWID)%>"/>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Cache-control" content="no-cache">
        <meta http-equiv="Expires" content="0">
        <title>Photoground</title>
        <link type="text/css" href="${urlStyle}" rel="stylesheet" />
        <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false">
        </script>
        <script src="${urlAjaxFramework}">
        </script>
    </head>
    <body>
        <%@include file="sharedControls/navigation_login.jsp" %>
        <%@include file="sharedControls/navigation_menu.jsp" %>
        <div class="content_header"></div>
        <article>
            <div class="content_main">
                <c:url var="urlComment" value="/Comment" />
                <div class="photo">
                    <div style="margin-bottom: 5px;">
                        <button type="button" class="button" onclick="changeDisplay(1);">
                            <img class="system_icons imgPhoto" />รูปภาพ
                        </button>
                        <c:if test='${videoPath != null && videoPath != ""}'>
                            <button type="button" class="button" onclick="changeDisplay(2);">
                                <img class="system_icons imgVideo" />วีดีโอ
                            </button>
                        </c:if>
                        <c:if test='${latitude != 0 && longitude != 0}'>
                            <button type="button" class="button" onclick="changeDisplay(3);">
                                <img class="system_icons imgMap" />แผนที่
                            </button>
                        </c:if>
                    </div>
                    <div id="display_photo">
                        <img alt="" src="${filePath}" width="100%" />
                    </div>
                    <div id="display_video">
                        <table style="width: 100%; white-space: nowrap; font-size: small; background-color: #000000; color: #FFFFFF;">
                            <tr>
                                <td colspan="4">
                                    <video id="videoPlayer" src="${videoPath}" width="100%"></video>
                                </td>
                            </tr>
                            <tr>
                                <td style="width: 1%;">
                                    <button id="btnRestart" type="button" class="media_player_button">
                                        <img class="system_icons imgRestart" />
                                    </button>
                                    <button id="btnPlay" type="button" class="media_player_button">
                                        <img class="system_icons imgPlay" />
                                    </button>
                                    <button id="btnStop" type="button" class="media_player_button">
                                        <img class="system_icons imgStop" />
                                    </button>
                                    <button id="btnBackward" type="button" class="media_player_button">
                                        <img class="system_icons imgBackward" />
                                    </button>
                                    <button id="btnForward" type="button" class="media_player_button">
                                        <img class="system_icons imgForward" />
                                    </button>
                                </td>
                                <td style="width: 97%; text-align: left;">
                                    <input id="seekSliderTime" type="range" min="0" max="100" value="0" step="1" style="width: 95%;" />
                                </td>
                                <td style="width: 1%;">
                                    <span id="currentTime">00:00</span>&nbsp;/&nbsp;<span id="durationTime">00:00</span>
                                </td>
                                <td style="width: 1%;">
                                    <button id="btnFullscreen" type="button" class="media_player_button">
                                        <img class="system_icons imgFullscreen" />
                                    </button>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div id="display_map">
                        <input type="hidden" id="latitudeValue" name="latitudeValue" value="${latitude}" />
                        <input type="hidden" id="longitudeValue" name="longitudeValue" value="${longitude}" />
                        <div id="map-canvas" class="gmap_upload"></div>
                    </div>
                </div>
                <div class="comment">
                    <p>
                    <form method="POST" target="commentAction" onsubmit="return checkComment();">
                        <input type="hidden" id="rowID" name="rowID" value="${rowID}" />
                        <input type="hidden" name="rowID_M_Member" value="${rowID_M_Member}" />
                        <c:if test='${rowIDLogin != null && rowID > 0}'>
                            <textarea id="commentMessage" name="commentMessage" rows="5"></textarea>
                            <br />
                            <!--
                            <input class="comment_icon comment_button" type="submit" value="แสดงความคิดเห็น" />
                            -->
                            <button type="submit" class="button">
                                <img class="system_icons imgComment" />แสดงความคิดเห็น
                            </button>
                        </c:if>
                    </form>
                    <iframe name="commentAction" style="display: none;"></iframe>
                    </p>
                    <br />
                    <div id="comment_detail">
                    </div>
                    <input type="hidden" id="urlComment" value="${urlComment}" />
                    <input type="hidden" id="numOfComment" name="numOfComment" value="${numOfComment}" />
                    <input type="hidden" id="pageSize" name="pageSize" value="${pageSize}" />
                    <input type="hidden" id="currentSize" name="currentSize" />
                    <center>
                        <div><img id="imgLoadingComment" alt="" src="${urlImageLoadingComment}" style="background-color: transparent;" /></div>
                        <button id="btnLoadComment" type="button" class="button" onclick="setCurrentSize(true);
                    loadComment(true);">
                            <img class="system_icons imgAddition" />อ่านเพิ่มเติม
                        </button>
                    </center>
                </div>
            </div>
        </article>
        <footer>
            <div class="content_footer"></div>
        </footer>
        <script src="${urlPhotoJS}">
        </script>
    </body>
</html>
