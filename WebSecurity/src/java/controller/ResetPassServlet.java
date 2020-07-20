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
import model.m_member_model;
import utility.SecurityHelper;
import utility.SmsHelper;

public class ResetPassServlet extends HttpServlet {

    private int minPasswordLength = 0;
    private long rowID_M_Member = 0;
    private String otpReference = "";
    private String otpPassword = "";
    private String aciton = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.sendRedirect("resetpwd.jsp");
        otpReference = "";
        otpPassword = "";
        aciton = "";
        request.setAttribute("showForm", false);
        RequestDispatcher view = request.getRequestDispatcher("resetpwd.jsp");
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        aciton = request.getParameter("aciton");
        if (aciton == null) {
            aciton = "";
        }
        if (aciton.trim().equals("resetPassword")) {
            String errorMessage = "";
            m_member_model clsMember = new m_member_model();

            otpPassword = request.getParameter("otp_password");
            int otpTimeout = Integer.parseInt(getServletContext().getInitParameter("otpTimeout"));
            try {
                if (!clsMember.checkExistOTP(rowID_M_Member, otpReference, otpPassword, otpTimeout)) {
                    errorMessage += "window.parent.alert('รหัส OTP ไม่ถูกต้อง');";
                    errorMessage += "window.parent.$('#otp_password').val('');";
                    errorMessage += "window.parent.$('#new_password').val('');";
                    errorMessage += "window.parent.$('#retype_new_password').val('');";
                    errorMessage += "window.parent.$('#new_password').trigger('change');";
                }
            } catch (Exception ex) {
                if (ex.getMessage().equals("timeout")) {
                    errorMessage += "window.parent.alert('หมดเวลาการใช้รหัส OTP นี้แล้ว');";
                    errorMessage += "window.parent.location.href='" + request.getContextPath() + "/';";
                } else {
                    errorMessage += "window.parent.alert('" + ex.getMessage() + "');";
                }
                Logger.getLogger(ResetPassServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            String new_password = request.getParameter("new_password");
            if (new_password == null) {
                new_password = "";
            }
            new_password = new_password.trim();

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script type=\"text/javascript\">");

            if (errorMessage.isEmpty()) {
                if (SecurityHelper.checkPasswordStrength(new_password) && new_password.length() >= minPasswordLength) {
                    try {
                        m_member member;
                        member = clsMember.loadByPrimaryKey(rowID_M_Member);
                        member.setPassword(SecurityHelper.encrypt(member.getSalt(), new_password));
                        member.setOtpReference(null);
                        member.setOtpPassword(null);
                        member.setOtpRequestTime(null);
                        member.setUpdateDate(new Date());

                        clsMember.update(member);

                        out.println("window.parent.alert('บันทึกข้อมูลเรียบร้อย');");
                        out.println("window.parent.location.href='" + request.getContextPath() + "/';");
                    } catch (SQLException ex) {
                        out.println("window.parent.alert('" + ex.getMessage() + "');");
                        Logger.getLogger(ResetPassServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    out.println("window.parent.alert('รหัสผ่านต้องมีขนาดอย่างน้อย " + minPasswordLength + " ตัวอักษร และประกอบไปด้วย ตัวอักษรเล็ก ตัวอักษรใหญ่ อักขระพิเศษ และตัวเลข');");
                }
            } else {
                out.println(errorMessage);
            }
            out.println("</script>");
            out.close();
        } else {
            m_member_model clsMember = new m_member_model();
            String username = request.getParameter("username");
            response.setContentType("text/html;charset=UTF-8");
            try {
                m_member member = clsMember.getUserByName(username);
                rowID_M_Member = member.getRowID();
                if (rowID_M_Member == 0) {
                    PrintWriter out = response.getWriter();
                    out.println("<script type=\"text/javascript\">");
                    out.println("window.alert('Username ไม่ถูกต้อง');");
                    out.println("window.location.href='" + request.getContextPath() + "/ResetPass';");
                    out.println("</script>");
                    out.close();
                } else {
                    otpReference = SecurityHelper.generateSessionKey(8);
                    otpPassword = SecurityHelper.generateSessionKey(6);

                    member.setOtpReference(otpReference);
                    member.setOtpPassword(otpPassword);
                    member.setOtpRequestTime(new Date());
                    clsMember.update(member);

                    SmsHelper sms = new SmsHelper();
                    sms.send(member.getMobilePhoneNo(), "OTP=" + otpPassword + "(Ref=" + otpReference + ")");
                    
                    minPasswordLength = Integer.parseInt(getServletContext().getInitParameter("minPasswordLength"));
                    request.setAttribute("minPasswordLength", minPasswordLength);
                    request.setAttribute("showForm", true);
                    request.setAttribute("otpReference", member.getOtpReference());
                    RequestDispatcher view = request.getRequestDispatcher("resetpwd.jsp");
                    view.forward(request, response);
                }
            } catch (Exception ex) {
                PrintWriter out = response.getWriter();
                out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
                out.close();
                Logger.getLogger(ResetPassServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
