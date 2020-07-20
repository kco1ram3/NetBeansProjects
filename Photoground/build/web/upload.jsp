<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlUpload" value="/Upload" />
<c:url var="urlStyle" value="/css/style.css" />
<c:url var="urlScript" value="/js/script.js" />
<c:url var="urlAjaxFramework" value="/js/ajax_framework.js" />
<c:url var="urlUploadJS" value="/js/upload.js" />
<c:url var="urlImageLoading" value="/images/loading/arrows16.gif" />
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
        <script src="${urlScript}">
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
                <form method="POST" action="${urlUpload}" enctype="multipart/form-data" onsubmit="return checkUpload();">
                    <input type="hidden" name="rowID" value="${rowID}" />
                    <input type="hidden" name="rowID_M_Member" value="${rowID_M_Member}" />
                    <input type="hidden" id="latitudeValue" name="latitudeValue" value="${latitudeMap}" />
                    <input type="hidden" id="longitudeValue" name="longitudeValue" value="${longitudeMap}" />
                    <input type="hidden" id="latitudeOriginal" name="latitudeOriginal" value="${latitudeOriginal}" />
                    <input type="hidden" id="longitudeOriginal" name="longitudeOriginal" value="${longitudeOriginal}" />
                    <input type="hidden" id="filePath" name="filePath" value="${filePath}" />
                    <input type="hidden" id="fileThumbnailPath" name="fileThumbnailPath" value="${fileThumbnailPath}" />
                    <input type="hidden" id="videoPath" name="videoPath" value="${videoPath}" />
                    <table align="center" style="font-size: small;">
                        <caption>รายละเอียดการ&nbsp;Upload</caption>
                        <tr>
                            <td>ชื่อรูปภาพ&nbsp;:&nbsp;</td>
                            <td><input type="text" id="photoName" name="photoName" value="${photoName}" /></td>
                        </tr>
                        <tr>
                            <td>รายละเอียดรูปภาพ&nbsp;:&nbsp;</td>
                            <td><textarea id="photoDescription" name="photoDescription" cols="80" rows="10">${photoDescription}</textarea></td>
                        </tr>
                        <tr>
                            <td>วันที่รูปภาพ&nbsp;:&nbsp;</td>
                            <td><input type="date" id="photoDate" name="photoDate" value="${photoDate}" /></td>
                        </tr>
                        <tr>
                            <td>พิกัดรูปภาพ&nbsp;:&nbsp;</td>
                            <td>
                                <input type="radio" id="coordinates1" name="coordinates" value="0" onclick="coordinatesType(this.value);" /><label for="coordinates1">ตำแหน่งในรูปภาพ</label>
                                <input type="radio" id="coordinates2" name="coordinates" value="1" onclick="coordinatesType(this.value);" /><label for="coordinates2">ตำแหน่งปัจจุบัน</label>
                                <input type="radio" id="coordinates3" name="coordinates" value="2" onclick="coordinatesType(this.value);" checked="true" /><label for="coordinates3">กำหนดเอง</label>
                                <div id="coordinates" style="margin: 10px 0;">
                                    latitude&nbsp;:&nbsp;
                                    <input type="number" id="latitude" name="latitude" onchange="changePosition();" value="${latitudeMap}" />
                                    longitude&nbsp;:&nbsp;
                                    <input type="number" id="longitude" name="longitude" onchange="changePosition();" value="${longitudeMap}" />
                                </div>
                                <div id="gmap" style="position: relative">
                                    <div id="search_map">
                                        <input type="search" id="address" name="address" />
                                        <button type="button" class="button" onclick="codeAddress();">
                                            <img class="system_icons imgSearch" />ค้นหา
                                        </button>
                                        <img id="imgLoading" alt="" src="${urlImageLoading}" />
                                    </div>
                                    <div id="map-canvas" class="gmap_upload"></div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>ไฟล์รูปภาพ&nbsp;:&nbsp;</td>
                            <td>
                                <c:if test='${fileThumbnailPath != null && fileThumbnailPath != ""}'>
                                    <a href="#" onclick="openWindow('${filePath}');">
                                        <img alt="" src="${fileThumbnailPath}" alt="" border="0" />
                                    </a>
                                    <br />
                                </c:if>
                                <input type="file" id="photo" name="photo" />
                            </td>
                        </tr>
                        <tr>
                            <td>ไฟล์วีดีโอ&nbsp;:&nbsp;</td>
                            <td>
                                <c:if test='${videoPath != null && videoPath != ""}'>
                                    <video src="${videoPath}" controls></video><br />
                                    <input type="checkbox" id="chkDeleteVideo" name="chkDeleteVideo" /><label for="chkDeleteVideo">ลบไฟล์วีดีโอ</label><br />
                                </c:if>
                                <input type="file" id="video" name="video" />
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <button type="submit" class="button">
                                    <img class="system_icons imgSave" />บันทึกข้อมูล
                                </button>
                                <button type="button" class="button" onclick="javascript: history.back(-1);">
                                    <img class="system_icons imgBack" />ย้อนกลับ
                                </button>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </article>
        <footer>
            <div class="content_footer"></div>
        </footer>
        <script src="${urlUploadJS}">
        </script>
    </body>
</html>
