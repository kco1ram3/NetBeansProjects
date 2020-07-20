package controller;

import entity.m_member;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.m_member_model;

public class SmsOtpServlet extends HttpServlet {

    private long rowID_M_Member = 0;
    private String otpReference = "";
    private String otpPassword = "";

    private void sendSMS(String mobilePhoneNo, String message) {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("socket.properties"));
            Socket s = new Socket(prop.getProperty("ip"), Integer.parseInt(prop.getProperty("port")));
            PrintWriter socketOut = new PrintWriter(s.getOutputStream());
            socketOut.print(mobilePhoneNo + ":" + message);
            socketOut.flush();
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(SmsOtpServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String generateSessionKey(int length) {
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int n = alphabet.length();
        String result = new String();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            result = result + alphabet.charAt(r.nextInt(n));
        }
        return result; //UUID.randomUUID().toString()
    }

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

        m_member member = new m_member();
        try {
            member.setRowID(rowID_M_Member);
            member.setOtpReference(generateSessionKey(8));
            member.setOtpPassword(generateSessionKey(6));
            member.setOtpRequestTime(new Date());

            m_member_model clsMember = new m_member_model();
            clsMember.update(member);
        } catch (SQLException ex) {
            PrintWriter out = response.getWriter();
            out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
            out.close();
            Logger.getLogger(SmsOtpServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        sendSMS(session.getAttribute(m_member.ColumnName.MOBILEPHONENO).toString(), "OTP=" + member.getOtpPassword() + " (Ref=" + member.getOtpReference() + ")");

        request.setAttribute("otpReference", member.getOtpReference());

        RequestDispatcher view = request.getRequestDispatcher("sms-otp.jsp");
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        otpReference = request.getParameter("otpReference");
        otpPassword = request.getParameter("otpPassword");
        if (otpReference == null) {
            otpReference = "";
        }
        if (otpPassword == null) {
            otpPassword = "";
        }
        otpReference = otpReference.trim();
        otpPassword = otpPassword.trim();
        
        int otpTimeout = Integer.parseInt(getServletContext().getInitParameter("otpTimeout"));
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<script type=\"text/javascript\">");
            
            m_member_model clsMember = new m_member_model();
            if (clsMember.checkExistOTP(rowID_M_Member, otpReference, otpPassword, otpTimeout)) {
                out.println("window.parent.alert('รหัสผ่านถูกต้อง');");
                out.println("window.parent.location.href='" + request.getContextPath() + "/Home';");
            } else {
                out.println("window.parent.alert('รหัสผ่านไม่ถูกต้อง กรุณากรอกใหม่');");
                out.println("window.parent.$('#otpPassword').val('');");
            }
        } catch (Exception ex) {
            if (ex.getMessage().equals("timeout")) {
                out.println("window.parent.alert('หมดเวลาการใช้รหัสผ่านนี้ กรุณาขอรหัสใหม่');");
                out.println("window.parent.location.href='" + request.getContextPath() + "/Home';");
            } else {
                out.println("window.parent.alert('" + ex.getMessage() + "');");
            }
            Logger.getLogger(SmsOtpServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.println("</script>");
            out.close();
        }
    }
}
