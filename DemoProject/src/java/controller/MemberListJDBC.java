/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class MemberListJDBC extends HttpServlet {

    @EJB
    private Member1Facade member1Facade;
    
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    
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
            out.println("<title>Servlet MemberListJDBC</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MemberListJDBC at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }
    
    protected String generatePageSize(HttpServletRequest request, int record, int size, int currentPage) {
        StringBuilder pageSize = new StringBuilder();
        for (int loop = 0; loop < (record / size) + (record % size > 0 ? 1 : 0); loop++) {
            if (loop + 1 == currentPage) {
                pageSize.append(loop + 1);
            } else {
                pageSize.append("<a href=\"" + request.getContextPath() + "/MemberList?page=" + (loop + 1) + "\">");
                pageSize.append(loop + 1);
                pageSize.append("</a>");
            }
            pageSize.append("&nbsp;");
        }
        return pageSize.toString();
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
        int pageSize = Integer.parseInt(getServletContext().getInitParameter("pageSize"));
        int currentPage;
        try {
            currentPage = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException ex) {
            currentPage = 1;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MemberListJDBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MemberListJDBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MemberListJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demoproject", "root", "admin");
        } catch (SQLException ex) {
            Logger.getLogger(MemberListJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(MemberListJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            rs = stmt.executeQuery("SELECT * FROM Member");
        } catch (SQLException ex) {
            Logger.getLogger(MemberListJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
         /*
            request.setAttribute("members", 
                    member1Facade.findRange(new int[]{pageSize * (currentPage - 1), pageSize * currentPage}));
            */
        request.setAttribute("pageSize", generatePageSize(request, 0, pageSize, currentPage));
        RequestDispatcher view = request.getRequestDispatcher("memberListJDBC.jsp");
        view.forward(request, response);
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
        processRequest(request, response);
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
