package controller;

import entity.m_member;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.m_member_model;

public class LoginServlet extends HttpServlet {

    private long rowID_M_Member = 0;
    private String username_login = "";
    private String password_login = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        rowID_M_Member = 0;

        HttpSession session = request.getSession();
        try {
            rowID_M_Member = Long.parseLong(session.getAttribute(m_member.ColumnName.ROWID).toString());
        } catch (Exception ex) {
            rowID_M_Member = 0;
        }

        if (rowID_M_Member > 0) {
            PrintWriter out = response.getWriter();
            out.println("<script type=\"text/javascript\">");
            out.println("window.location.href='" + request.getContextPath() + "/Home';");
            out.println("</script>");
            out.close();
        }

        RequestDispatcher view = request.getRequestDispatcher("login.jsp");
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        username_login = request.getParameter("username_login");
        password_login = request.getParameter("password_login");
        if (username_login == null) {
            username_login = "";
        }
        if (password_login == null) {
            password_login = "";
        }
        username_login = username_login.trim();
        password_login = password_login.trim();

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<script type=\"text/javascript\">");
            m_member_model clsMember = new m_member_model();
            m_member member = clsMember.login(username_login, password_login);
            if (member.getRowID() != 0) {
                HttpSession session = request.getSession(true);
                session.setAttribute(m_member.ColumnName.ROWID, member.getRowID());
                session.setAttribute(m_member.ColumnName.NAME, member.getName());
                session.setAttribute(m_member.ColumnName.SURNAME, member.getSurname());
                session.setAttribute(m_member.ColumnName.EMAIL, member.getEmail());
                session.setAttribute(m_member.ColumnName.MOBILEPHONENO, member.getMobilePhoneNo());
                session.setAttribute(m_member.ColumnName.USERNAME, member.getUsername());
                out.println("window.parent.alert('เข้าสู่ระบบเรียบร้อย');");
                out.println("window.parent.location.href='" + request.getContextPath() + "/Home';");
            } else {
                out.println("window.parent.alert('ชื่อผู้ใช้ หรือ รหัสผ่าน ไม่ถูกต้อง');");
                out.println("window.parent.$('#username_login').val('');");
                out.println("window.parent.$('#password_login').val('');");
            }
            out.println("</script>");
        } catch (SQLException ex) {
            out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
}
