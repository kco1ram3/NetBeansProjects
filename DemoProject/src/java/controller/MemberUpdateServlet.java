/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Member1;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import session.Member1Facade;

/**
 *
 * @author Administrator
 */
public class MemberUpdateServlet extends HttpServlet {

    @EJB
    private Member1Facade member1Facade;
    
    private Member1 member1;
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MemberUpdateServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MemberUpdateServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        long rowID;
        try {
            rowID = Long.parseLong(request.getParameter("rowID"));
        } catch (NumberFormatException ex) {
            rowID = 0;
        }
        String flag = request.getParameter("flag") != null ? request.getParameter("flag") : "";
        if (flag.equals("delete")) {
            member1 = new Member1(rowID);
            member1Facade.remove(member1);
            
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                out.println("<script type=\"text/javascript\">");  
                out.println("alert('ลบข้อมูลเรียบร้อย');");  
                out.println("window.location.href='" + request.getContextPath() + "/MemberServlet';");
                out.println("</script>"); 
            } finally {            
                out.close();
            }
        } else {
            request.setAttribute("rowID", rowID);
            request.setAttribute("member", member1Facade.find(rowID));
            RequestDispatcher view = request.getRequestDispatcher("memberUpdate.jsp");
            view.forward(request, response);   
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        long rowID = Long.parseLong(request.getParameter("rowID"));
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        /*
        try {
            registerDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH).parse(request.getParameter("registerDate"));
        } catch (Exception ex) {
            Logger.getLogger(MemberUpdateServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
        member1 = new Member1(rowID);
        member1.setFirstName(firstName);
        member1.setLastName(lastName);
        member1.setEmail(email);
        member1.setUsername(username);
        member1.setPassword(password);
        
        if (rowID > 0) {
            member1.setRegisterDate(member1Facade.find(rowID).getRegisterDate());
            member1.setUpdateDate(new Date());
            member1Facade.edit(member1);
        } else {
            member1.setRegisterDate(new Date());
            member1Facade.create(member1);
        }
        //response.sendRedirect(request.getContextPath() + "/MemberServlet");
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<script type=\"text/javascript\">");  
            out.println("alert('บันทึกข้อมูลเรียบร้อย');");  
            out.println("window.location.href='" + request.getContextPath() + "/MemberServlet';");
            out.println("</script>"); 
        } finally {            
            out.close();
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
