package controller;

import entity.m_member;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
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

public class MemberServlet extends HttpServlet {

    private long rowID = 0;
    private String name = "";
    private String surname = "";
    private String email = "";
    private String username_register = "";
    private String password_register = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        name = "";
        surname = "";
        email = "";
        username_register = "";
        password_register = "";
        
        HttpSession session = request.getSession();
        try {
            rowID = Long.parseLong(session.getAttribute(m_member.ColumnName.ROWID).toString());
        } catch (Exception ex) {
            rowID = 0;
        }
        m_member_model member = new m_member_model();
        List<m_member> list = null;
        try {
            list = member.loadByPrimaryKey(rowID);
            if (list.size() > 0) {
                rowID = list.get(0).getRowID();
                request.setAttribute("name", list.get(0).getName());
                request.setAttribute("surname", list.get(0).getSurname());
                request.setAttribute("email", list.get(0).getEmail());
                request.setAttribute("username", list.get(0).getUsername());
                request.setAttribute("password", list.get(0).getPassword());
            }
        } catch (SQLException ex) {
            Logger.getLogger(MemberServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            member = null;
        }
        
        request.setAttribute("rowID", rowID);
        RequestDispatcher view = request.getRequestDispatcher("member.jsp");
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isExist = false;
        rowID = Long.parseLong(request.getParameter("rowID"));
        name = request.getParameter("name");
        surname = request.getParameter("surname");
        email = request.getParameter("email");
        username_register = request.getParameter("username_register");
        password_register = request.getParameter("password_register");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            m_member member = new m_member();
            member.setRowID(rowID);
            member.setName(name);
            member.setSurname(surname);
            member.setEmail(email);
            member.setUsername(username_register);
            member.setPassword(password_register);
            member.setRegisterDate(new Date());
            member.setUpdateDate(new Date());

            m_member_model clsMember = new m_member_model();
            if (rowID == 0) {
                if (clsMember.checkExistUsername(username_register)) {
                    isExist = true;
                } else {
                    clsMember.insert(member);
                }
            } else {
                clsMember.update(member);
            }
            member = null;
            clsMember = null;

            out.println("<script type=\"text/javascript\">");
            if (isExist) {
                out.println("window.parent.alert('ชื่อผู้ใช้นี้ไม่สามารถใช้ได้');");
            } else {
                out.println("window.parent.alert('บันทึกข้อมูลเรียบร้อย');");
                out.println("window.parent.location.href='" + request.getContextPath() + "/Home';");
            }
            out.println("</script>");
        } catch (SQLException ex) {
            out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
            Logger.getLogger(MemberServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
}
