<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.m_member"%>
<c:url var="urlNavigationLoginJS" value="/js/navigation_login.js" />
<c:url var="urlLogin" value="/Login" />
<c:url var="urlLogout" value="/Logout" />
<c:url var="urlMember" value="/Member" />
<c:set var="rowIDLogin" value="<%= session.getAttribute(m_member.ColumnName.ROWID)%>"/>
<header>
    <div class="navigation_login">
        <table align="right" style="margin-right: 15px; font-size: small;">
            <c:choose>
                <c:when test='${rowIDLogin == null}'>
                    <tr>
                        <td>
                            <form name="login" method="POST" action="${urlLogin}" onsubmit="return checkLogin();">
                                <label for="username_login">ชื่อผู้ใช้&nbsp;:&nbsp;</label>
                                <input type="text" id="username_login" name="username_login" />
                                &nbsp;
                                <label for="password_login">รหัสผ่าน&nbsp;:&nbsp;</label>
                                <input type="password" id="password_login" name="password_login" />
                                <button type="submit" class="button">
                                    <img class="system_icons imgLogin" />เข้าสู่ระบบ
                                </button>
                            </form>
                        </td>
                        <td>
                            <a href="${urlMember}">
                                <button type="button" class="button">
                                    <img class="system_icons imgRegister" />ลงทะเบียน
                                </button>
                            </a>
                        </td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td>
                            <span class="comment_icon comment_user"><%= session.getAttribute(m_member.ColumnName.USERNAME)%></span>
                            &nbsp;
                            <a href="${urlMember}">
                                <button type="button" class="button">
                                    <img class="system_icons imgEdit" />แก้ไขข้อมูลส่วนตัว
                                </button>
                            </a>
                        </td>
                        <td>
                            <a href="${urlLogout}" onclick="javascript: return confirm('ยืนยันการออกจากระบบ');
                                return false;">
                                <button type="button" class="button">
                                    <img class="system_icons imgLogout" />ออกจากระบบ
                                </button>
                            </a>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </div>
</header>
<script src="${urlNavigationLoginJS}">
</script>