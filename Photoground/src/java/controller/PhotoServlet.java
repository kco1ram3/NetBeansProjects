package controller;

import entity.m_member;
import entity.t_comment;
import entity.t_photo;
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
import model.t_comment_model;
import model.t_photo_model;

public class PhotoServlet extends HttpServlet {

    private long rowID = 0;
    private long rowID_M_Member = 0;
    private String commentMessage = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int pageSize = Integer.parseInt(getServletContext().getInitParameter("pageSizeComment"));
        try {
            rowID = Long.parseLong(request.getParameter("rowID"));
        } catch (NumberFormatException ex) {
            rowID = 0;
        }
        HttpSession session = request.getSession();
        try {
            rowID_M_Member = Long.parseLong(session.getAttribute(m_member.ColumnName.ROWID).toString());
        } catch (Exception ex) {
            rowID_M_Member = 0;
        }

        t_photo_model photo = new t_photo_model();
        List<t_photo> list = null;
        try {
            list = photo.loadByPrimaryKey(rowID);
            if (list.size() > 0) {
                request.setAttribute("latitude", list.get(0).getLatitudeMap());
                request.setAttribute("longitude", list.get(0).getLongitudeMap());
                request.setAttribute("filePath", list.get(0).getFilePath());
                request.setAttribute("videoPath", list.get(0).getVideoPath());
                request.setAttribute("numOfComment", list.get(0).getNumOfComment());
            }
        } catch (SQLException ex) {
            Logger.getLogger(PhotoServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            photo = null;
        }
        request.setAttribute("rowID", rowID);
        request.setAttribute("rowID_M_Member", rowID_M_Member);
        request.setAttribute("pageSize", pageSize);
        RequestDispatcher view = request.getRequestDispatcher("photo.jsp");
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        rowID = Long.parseLong(request.getParameter("rowID"));
        rowID_M_Member = Long.parseLong(request.getParameter("rowID_M_Member"));
        commentMessage = request.getParameter("commentMessage");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            t_comment comment = new t_comment();
            comment.setRowID_M_Member(rowID_M_Member);
            comment.setRowID_T_Photo(rowID);
            comment.setComment(commentMessage);
            comment.setCommentDate(new Date());
            t_comment_model clsComment = new t_comment_model();
            clsComment.insert(comment);
            comment = null;
            clsComment = null;

            out.println("<script type=\"text/javascript\">");
            out.println("window.parent.commentMessage.value = '';");
            out.println("window.parent.loadComment();");
            out.println("</script>");
        } catch (SQLException ex) {
            out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
            Logger.getLogger(PhotoServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
}
