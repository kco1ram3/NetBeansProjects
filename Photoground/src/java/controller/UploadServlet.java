package controller;

import entity.m_member;
import entity.t_photo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.servlet.http.Part;
import model.t_photo_model;

public class UploadServlet extends HttpServlet {

    private String flag = "";
    private long rowID = 0;
    private long rowID_M_Member = 0;
    private String photoName = "";
    private String photoDescription = "";
    private Date photoDate = null;
    private String filePath = "";
    private String fileThumbnailPath = "";
    private double latitudeOriginal = 0;
    private double longitudeOriginal = 0;
    private double latitudeMap = 0;
    private double longitudeMap = 0;
    private String videoPath = "";
    private String coordinates = "";
    private String chkDeleteVideo = "";

    private void uploadFile(InputStream is, String outputpath) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(outputpath);
            int ch = is.read();
            while (ch != -1) {
                os.write(ch);
                ch = is.read();
            }
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            os = null;
        }
    }

    private Date convertStringToDate(String strDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(strDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date;
    }

    private String convertDateToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = "";
        try {
            strDate = df.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    private double parseDouble(String value) {
        double db = 0;
        try {
            db = Double.parseDouble(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return db;
    }

    private void deleteFile(String filePath) {
        File file = null;
        try {
            file = new File(getServletContext().getRealPath(filePath.replace("/PhotoZ", "")));
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            file = null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        flag = "";
        rowID = 0;
        rowID_M_Member = 0;
        photoName = "";
        photoDescription = "";
        photoDate = null;
        filePath = "";
        fileThumbnailPath = "";
        latitudeOriginal = 0;
        longitudeOriginal = 0;
        latitudeMap = 0;
        longitudeMap = 0;
        videoPath = null;
        coordinates = "";
        chkDeleteVideo = "";

        flag = request.getParameter("flag");
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

        if (flag != null && flag.equals("delete")) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                t_photo_model clsPhoto = new t_photo_model();
                List<t_photo> list = null;
                list = clsPhoto.loadByPrimaryKey(rowID);
                if (list.size() > 0) {
                    deleteFile(list.get(0).getFilePath());
                    deleteFile(list.get(0).getFileThumbnailPath());
                    deleteFile(list.get(0).getVideoPath());
                }
                clsPhoto.delete(rowID);
                clsPhoto = null;

                out.println("<script type=\"text/javascript\">");
                out.println("alert('ลบข้อมูลเรียบร้อย');");
                out.println("window.location.href='" + request.getContextPath() + "/Gallery';");
                out.println("</script>");
                out.close();
            } catch (Exception ex) {
                out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
                Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                out.close();
            }
        } else {
            t_photo_model photo = new t_photo_model();
            List<t_photo> list = null;
            try {
                list = photo.loadByPrimaryKey(rowID);
                if (list.size() > 0) {
                    request.setAttribute("photoName", list.get(0).getPhotoName());
                    request.setAttribute("photoDescription", list.get(0).getPhotoDescription());
                    request.setAttribute("photoDate", convertDateToString(list.get(0).getPhotoDate()));
                    request.setAttribute("latitudeMap", list.get(0).getLatitudeMap());
                    request.setAttribute("longitudeMap", list.get(0).getLongitudeMap());
                    request.setAttribute("latitudeOriginal", list.get(0).getLatitudeOriginal());
                    request.setAttribute("longitudeOriginal", list.get(0).getLongitudeOriginal());
                    request.setAttribute("filePath", list.get(0).getFilePath());
                    request.setAttribute("fileThumbnailPath", list.get(0).getFileThumbnailPath());
                    request.setAttribute("videoPath", list.get(0).getVideoPath());
                }
            } catch (SQLException ex) {
                Logger.getLogger(PhotoServlet.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                photo = null;
            }

            request.setAttribute("rowID", rowID);
            request.setAttribute("rowID_M_Member", rowID_M_Member);
            RequestDispatcher view = request.getRequestDispatcher("upload.jsp");
            view.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        rowID = Long.parseLong(request.getParameter("rowID"));
        rowID_M_Member = Long.parseLong(request.getParameter("rowID_M_Member"));
        photoName = request.getParameter("photoName");
        photoDescription = request.getParameter("photoDescription");
        photoDate = convertStringToDate(request.getParameter("photoDate").toString());
        filePath = request.getParameter("filePath");
        fileThumbnailPath = request.getParameter("fileThumbnailPath");
        latitudeOriginal = parseDouble(request.getParameter("latitudeOriginal"));
        longitudeOriginal = parseDouble(request.getParameter("longitudeOriginal"));
        latitudeMap = parseDouble(request.getParameter("latitudeValue"));
        longitudeMap = parseDouble(request.getParameter("longitudeValue"));
        videoPath = request.getParameter("videoPath");
        coordinates = request.getParameter("coordinates");
        chkDeleteVideo = request.getParameter("chkDeleteVideo");

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = dateFormat.format(new Date());

        String pathUpload = getServletContext().getRealPath("/") + "UploadFile";
        File folderUpload = new File(pathUpload);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            if (!folderUpload.exists()) {
                folderUpload.mkdirs();
            }
            Part photoUpload = request.getPart("photo");
            if (!photoUpload.getContentType().equals("application/octet-stream")) {
                if (!filePath.equals("")) {
                    deleteFile(filePath);
                }
                if (!fileThumbnailPath.equals("")) {
                    deleteFile(fileThumbnailPath);
                }
                
                String fileType = "." + photoUpload.getContentType().substring(photoUpload.getContentType().lastIndexOf("/") + 1).toLowerCase();
                String photoName = fileName + fileType;
                String outputPath = folderUpload + "/" + photoName;
                String outputThumbnailPath = folderUpload + "/thumbnail_" + photoName;
                filePath = getServletContext().getContextPath() + "/UploadFile/" + photoName;
                fileThumbnailPath = getServletContext().getContextPath() + "/UploadFile/thumbnail_" + photoName;
                int resizeScale = Integer.parseInt(getServletContext().getInitParameter("resizeScale"));
                javaxt.io.Image image = new javaxt.io.Image(photoUpload.getInputStream());
                image.saveAs(outputPath);
                if (image.getWidth() > image.getHeight()) {
                    image.setWidth(resizeScale);
                } else {
                    image.setHeight(resizeScale);
                }
                image.saveAs(outputThumbnailPath);
                double[] gps = image.getGPSCoordinate();

                latitudeOriginal = (gps != null ? gps[1] : 0);
                longitudeOriginal = (gps != null ? gps[0] : 0);
            }

            if (chkDeleteVideo != null) {
                if (chkDeleteVideo.equals("on")) {
                    deleteFile(videoPath);
                    videoPath = "";
                }
            }

            Part videoUpload = request.getPart("video");
            if (!videoUpload.getContentType().equals("application/octet-stream")) {
                if (!videoPath.equals("")) {
                    deleteFile(videoPath);
                }
                
                String fileType = "." + videoUpload.getContentType().substring(videoUpload.getContentType().lastIndexOf("/") + 1).toLowerCase();
                String videoName = fileName + fileType;
                String outputPath = folderUpload + "/" + videoName;
                videoPath = getServletContext().getContextPath() + "/UploadFile/" + videoName;
                uploadFile(videoUpload.getInputStream(), outputPath);
            }

            if (coordinates.equals("0")) {
                latitudeMap = latitudeOriginal;
                longitudeMap = longitudeOriginal;
            }

            t_photo photo = new t_photo();
            photo.setRowID(rowID);
            photo.setRowID_M_Member(rowID_M_Member);
            photo.setPhotoName(photoName);
            photo.setPhotoDescription(photoDescription);
            photo.setPhotoDate(photoDate);
            photo.setFilePath(filePath);
            photo.setFileThumbnailPath(fileThumbnailPath);
            photo.setLatitudeOriginal(latitudeOriginal);
            photo.setLongitudeOriginal(longitudeOriginal);
            photo.setLatitudeMap(latitudeMap);
            photo.setLongitudeMap(longitudeMap);
            photo.setVideoPath(videoPath);
            photo.setCreateDate(new Date());
            photo.setUpdateDate(new Date());

            t_photo_model clsPhoto = new t_photo_model();
            if (rowID == 0) {
                clsPhoto.insert(photo);
            } else {
                clsPhoto.update(photo);
            }
            photo = null;
            clsPhoto = null;

            out.println("<script type=\"text/javascript\">");
            out.println("alert('บันทึกข้อมูลเรียบร้อย');");
            out.println("window.location.href='" + request.getContextPath() + "/Gallery';");
            out.println("</script>");
            out.close();
        } catch (Exception ex) {
            out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
}
