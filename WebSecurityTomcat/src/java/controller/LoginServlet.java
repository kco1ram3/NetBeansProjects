package controller;

import entity.m_member;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
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
        //password_login = password_login.hashCode() + "";
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<script type=\"text/javascript\">");
            
            m_member_model clsMember = new m_member_model();
            List<m_member> list = clsMember.login(username_login, password_login);
            if (list.size() > 0) {
                HttpSession session = request.getSession(true);
                session.setAttribute(m_member.ColumnName.ROWID, list.get(0).getRowID());
                session.setAttribute(m_member.ColumnName.NAME, list.get(0).getName());
                session.setAttribute(m_member.ColumnName.SURNAME, list.get(0).getSurname());
                session.setAttribute(m_member.ColumnName.EMAIL, list.get(0).getEmail());
                session.setAttribute(m_member.ColumnName.MOBILEPHONENO , list.get(0).getMobilePhoneNo());
                session.setAttribute(m_member.ColumnName.USERNAME, list.get(0).getUsername());
                session.setAttribute(m_member.ColumnName.PASSWORD, list.get(0).getPassword());
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
