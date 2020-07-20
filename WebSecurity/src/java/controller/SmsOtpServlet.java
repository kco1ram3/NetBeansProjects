package controller;

import entity.m_member;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.m_member_model;
import utility.SecurityHelper;

public class SmsOtpServlet extends HttpServlet {

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
        return result;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        m_member_model clsMember = new m_member_model();
        String username = request.getParameter("username_login");
        response.setContentType("text/html;charset=UTF-8");
        try {
            m_member member = clsMember.getUserByName(username);
            if (member.getRowID() == 0) {
                PrintWriter out = response.getWriter();
                out.println("<script type=\"text/javascript\">");
                out.println("window.parent.alert('Username ไม่ถูกต้อง');");
                out.println("</script>");
                out.close();
            } else {
                String newPassword = generateSessionKey(6);
                otpReference = generateSessionKey(8);
                otpPassword = generateSessionKey(6);

                member.setOtpReference(otpReference);
                member.setOtpPassword(otpPassword);
                member.setOtpRequestTime(new Date());
                member.setPassword(SecurityHelper.encrypt(member.getSalt(), newPassword)); 

                clsMember.update(member);

                //sendSMS(member.getMobilePhoneNo(), "OTP=" + otpPassword + " (Ref=" + otpReference + ")");
                sendSMS(member.getMobilePhoneNo(), "password=" + newPassword + "(OTP=" + otpPassword + ",Ref=" + otpReference + ")");
            }
            //request.setAttribute("otpReference", member.getOtpReference());
        } catch (Exception ex) {
            PrintWriter out = response.getWriter();
            out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
            out.close();
            Logger.getLogger(SmsOtpServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
         RequestDispatcher view = request.getRequestDispatcher("login.jsp");
         view.forward(request, response);
         response.sendRedirect(request.getContextPath() + "/");
         */
        PrintWriter out = response.getWriter();
        out.println("<script type=\"text/javascript\">");
        out.println("window.parent.location.href='" + request.getContextPath() + "/';");
        out.println("</script>");
        out.close();
    }
}
