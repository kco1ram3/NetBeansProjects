package controller;

import entity.m_member;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.m_member_model;

public class RegisterServlet extends HttpServlet {

    private long rowID_M_Member = 0;
    private String name = "";
    private String surname = "";
    private String email = "";
    private String mobile = "";
    private String username = "";
    private String password = "";

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

        RequestDispatcher view = request.getRequestDispatcher("register.jsp");
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isExist = false;
        name = request.getParameter("name");
        surname = request.getParameter("surname");
        email = request.getParameter("email");
        mobile = request.getParameter("mobile");
        username = request.getParameter("username");
        password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            m_member member = new m_member();
            member.setName(name);
            member.setSurname(surname);
            member.setEmail(email);
            member.setMobilePhoneNo(mobile);
            member.setUsername(username);
            member.setPassword(password);
            member.setRegisterDate(new Date());

            m_member_model clsMember = new m_member_model();
            if (clsMember.checkExistUsername(username)) {
                isExist = true;
            } else {
                clsMember.insert(member);
            }

            out.println("<script type=\"text/javascript\">");
            if (isExist) {
                out.println("window.parent.alert('ชื่อผู้ใช้นี้ไม่สามารถใช้ได้');");
                out.println("window.parent.$('#username').val('');");
                out.println("window.parent.$('#password').val('');");
            } else {
                out.println("window.parent.alert('บันทึกข้อมูลเรียบร้อย');");
                out.println("window.parent.location.href='" + request.getContextPath() + "/';");
            }
            out.println("</script>");
        } catch (SQLException ex) {
            out.println("window.parent.alert('" + ex.getMessage() + "');");
            Logger.getLogger(RegisterServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
}
