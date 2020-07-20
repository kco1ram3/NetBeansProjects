package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.t_comment_model;
import model.t_comment_model.Sort;

public class CommentServlet extends HttpServlet {

    private long rowID_T_Photo = 0;
    private int size = 0;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            rowID_T_Photo = Long.parseLong(request.getParameter("rowID_T_Photo"));
        } catch (NumberFormatException ex) {
            rowID_T_Photo = 0;
        }
        try {
            size = Integer.parseInt(request.getParameter("size"));
        } catch (NumberFormatException ex) {
            size = 0;
        }
        
        t_comment_model comment = new t_comment_model();
        try { 
            request.setAttribute("comments", comment.loadByPhotoSize(rowID_T_Photo, size, Sort.DESC));
        } catch (SQLException ex) {
            Logger.getLogger(CommentServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            comment = null;
        }
        RequestDispatcher view = request.getRequestDispatcher("comment.jsp");
        view.forward(request, response);
    }
}
