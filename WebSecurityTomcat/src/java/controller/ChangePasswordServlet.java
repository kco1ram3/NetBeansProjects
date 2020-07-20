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

public class ChangePasswordServlet extends HttpServlet {

    private long rowID_M_Member = 0;
    private String new_password = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        rowID_M_Member = 0;

        HttpSession session = request.getSession();
        try {
            rowID_M_Member = Long.parseLong(session.getAttribute(m_member.ColumnName.ROWID).toString());
        } catch (Exception ex) {
            PrintWriter out = response.getWriter();
            out.println("<script type=\"text/javascript\">");
            out.println("window.location.href='" + request.getContextPath() + "/';");
            out.println("</script>");
            out.close();
        }

        RequestDispatcher view = request.getRequestDispatcher("change-password.jsp");
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        new_password = request.getParameter("new_password");
        if (new_password == null) {
            new_password = "";
        }
        new_password = new_password.trim();

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            m_member member = new m_member();
            member.setRowID(rowID_M_Member);
            member.setPassword(new_password);
            member.setUpdateDate(new Date());

            m_member_model clsMember = new m_member_model();
            clsMember.update(member);

            out.println("<script type=\"text/javascript\">");
            out.println("window.parent.alert('บันทึกข้อมูลเรียบร้อย');");
            out.println("window.parent.location.href='" + request.getContextPath() + "/Home';");
            out.println("</script>");
        } catch (SQLException ex) {
            out.println("window.parent.alert('" + ex.getMessage() + "');");
            Logger.getLogger(ChangePasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
}
