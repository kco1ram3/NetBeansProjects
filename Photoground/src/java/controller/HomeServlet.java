package controller;

import entity.t_photo;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.t_photo_model;
import model.t_photo_model.Sort;

public class HomeServlet extends HttpServlet {
    
    private String generatePageSize(HttpServletRequest request, int record, int size, int currentPage) {
        StringBuilder pageSize = new StringBuilder();
        pageSize.append("<div class=\"pageSize\">");
        for (int loop = 0; loop < (record / size) + (record % size > 0 ? 1 : 0); loop++) {
            if (loop + 1 == currentPage) {
                pageSize.append("<span class=\"active\">" + (loop + 1) + "</span>");
            } else {
                pageSize.append("<a href=\"" + request.getContextPath() + "/Home/" + (loop + 1) + "\">");
                pageSize.append("<span class=\"seleted\">" + (loop + 1) + "</span>");
                pageSize.append("</a>");
            }
            pageSize.append("&nbsp;");
        }
        pageSize.append("</div>");
        return pageSize.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int pageSize = Integer.parseInt(getServletContext().getInitParameter("pageSizeHome"));
        int currentPage;
        try {
            currentPage = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException ex) {
            currentPage = 1;
        }
        
        t_photo_model photo = new t_photo_model();
        List<t_photo> list = null;
        try {
            list = photo.listAllRange(pageSize * (currentPage - 1), pageSize, Sort.DESC);
            request.setAttribute("photos", list);
            request.setAttribute("pageSize", generatePageSize(request, photo.listAll(null).size(), pageSize, currentPage));
        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            photo = null;
        }
        RequestDispatcher view = request.getRequestDispatcher("home.jsp");
        view.forward(request, response);
    }
}
