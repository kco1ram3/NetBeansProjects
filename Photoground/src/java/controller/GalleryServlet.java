package controller;

import entity.m_member;
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
import javax.servlet.http.HttpSession;
import model.t_photo_model;
import model.t_photo_model.Sort;

public class GalleryServlet extends HttpServlet {
    
    private long rowID_M_Member = 0;
    
    private String generatePageSize(HttpServletRequest request, int record, int size, int currentPage) {
        StringBuilder pageSize = new StringBuilder();
        pageSize.append("<div class=\"pageSize\">");
        for (int loop = 0; loop < (record / size) + (record % size > 0 ? 1 : 0); loop++) {
            if (loop + 1 == currentPage) {
                pageSize.append("<span class=\"active\">" + (loop + 1) + "</span>");
            } else {
                pageSize.append("<a href=\"" + request.getContextPath() + "/Gallery/" + (loop + 1) + "\">");
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
        HttpSession session = request.getSession();
        try {
            rowID_M_Member = Long.parseLong(session.getAttribute(m_member.ColumnName.ROWID).toString());
        } catch (Exception ex) {
            rowID_M_Member = 0;
        }
        
        int pageSize = Integer.parseInt(getServletContext().getInitParameter("pageSizeGallery"));
        int currentPage;
        try {
            currentPage = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException ex) {
            currentPage = 1;
        }
        
        t_photo_model photo = new t_photo_model();
        List<t_photo> list = null;
        try {
            list = photo.loadByMemberRange(rowID_M_Member, pageSize * (currentPage - 1), pageSize, Sort.DESC);
            request.setAttribute("photos", list);
            request.setAttribute("pageSize", generatePageSize(request, photo.loadByMember(rowID_M_Member, null).size(), pageSize, currentPage));
        } catch (SQLException ex) {
            Logger.getLogger(GalleryServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            photo = null;
        }
        RequestDispatcher view = request.getRequestDispatcher("gallery.jsp");
        view.forward(request, response);
    }
}
