package controller;

import entity.m_member;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            HttpSession session = request.getSession();
            session.removeAttribute(m_member.ColumnName.ROWID);
            session.removeAttribute(m_member.ColumnName.NAME);
            session.removeAttribute(m_member.ColumnName.SURNAME);
            session.removeAttribute(m_member.ColumnName.EMAIL);
            session.removeAttribute(m_member.ColumnName.MOBILEPHONENO);
            session.removeAttribute(m_member.ColumnName.USERNAME);
            session.removeAttribute(m_member.ColumnName.PASSWORD);
            session.invalidate();
            
            out.println("<script type=\"text/javascript\">");
            out.println("window.parent.alert('ออกจากระบบเรียบร้อย');");
            out.println("window.parent.location.href='" + request.getContextPath() + "/';");
            out.println("</script>");
        } catch (Exception ex) {
            out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
            Logger.getLogger(LogoutServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
}
