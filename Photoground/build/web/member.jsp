<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="urlMemberJS" value="/js/member.js" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Cache-control" content="no-cache">
        <meta http-equiv="Expires" content="0">
        <title>Photoground</title>
        <link type="text/css" href="css/style.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="sharedControls/navigation_login.jsp" %>
        <%@include file="sharedControls/navigation_menu.jsp" %>
        <div class="content_header"></div>
        <article>
            <div class="content_main">
                <div class="text_line">
                </div>
                <form method="POST" target="memberAction" action="Member" onsubmit="return checkRegister();">
                    <input type="hidden" name="rowID" value="${rowID}" />
                    <table align="center" style="font-size: small;">
                        <caption>ข้อมูลการสมัครสมาชิก</caption>
                        <tr>
                            <td>ชื่อ&nbsp;:&nbsp;</td>
                            <td><input type="text" id="name" name="name" value="${name}" /></td>
                        </tr>
                        <tr>
                            <td>นามสกุล&nbsp;:&nbsp;</td>
                            <td><input type="text" id="surname" name="surname" value="${surname}" /></td>
                        </tr>
                        <tr>
                            <td>อีเมล&nbsp;:&nbsp;</td>
                            <td><input type="email" id="email" name="email" value="${email}" /></td>
                        </tr>
                        <tr>
                            <td>ชื่อผู้ใช้&nbsp;:&nbsp;</td>
                            <td>
                                <input type="text" id="username_register" name="username_register" value="${username}" 
                                    <c:if test='${rowID != null && rowID != 0}'> 
                                        style="background-color: transparent; border: 0" onfocus="this.blur();" 
                                    </c:if>
                                />
                            </td>
                        </tr>
                        <tr>
                            <td>รหัสผ่าน&nbsp;:&nbsp;</td>
                            <td><input type="password" id="password_register" name="password_register" value="${password}" /></td>
                        </tr>
                        <tr>
                            <td>ยืนยันรหัสผ่าน&nbsp;:&nbsp;</td>
                            <td><input type="password" id="password_confirm" name="password_confirm" value="${password}" /></td>
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
                <iframe name="memberAction" style="display: none;"></iframe>
                <div class="text_line">
                </div>
            </div>
        </article>
        <footer>
            <div class="content_footer"></div>
        </footer>
        <script src="${urlMemberJS}">
        </script>
    </body>
</html>
