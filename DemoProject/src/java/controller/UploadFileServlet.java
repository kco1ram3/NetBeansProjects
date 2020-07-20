/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author Administrator
 */
@MultipartConfig
public class UploadFileServlet extends HttpServlet {

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
            out.println("<title>Servlet UploadFileServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UploadFileServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }
    
    private void createPhoto(InputStream is, String outputpath) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(outputpath);
            // write bytes taken from uploaded file to target file
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
        RequestDispatcher view = request.getRequestDispatcher("uploadFile.jsp");
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
        //processRequest(request, response);
        String pathUpload = getServletContext().getRealPath("/") + "Upload";
        File folderUpload = new File(pathUpload);  
        if (!folderUpload.exists()){  
              folderUpload.mkdirs();
        }
        Part file = request.getPart("file");
        if (!file.getSubmittedFileName().equals("")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS"); 
            String fileName = dateFormat.format(new Date());
            String fileType = file.getSubmittedFileName().substring(file.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
            fileName += fileType;
            // get path on the server
            String outputPath = folderUpload + "/" + fileName;
            String outputThumbnailPath = folderUpload + "/thumbnail_" + fileName;
            String filePath = getServletContext().getContextPath() + "/Upload/" + fileName;
            String fileThumbnailPath = getServletContext().getContextPath() + "/Upload/thumbnail_" + fileName;
            // store photo
            InputStream is = file.getInputStream();
            //createPhoto(is, outputPath);

            /*
            BufferedImage bi = ImageIO.read(file.getInputStream()); 
            Image img = bi.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            int w = img.getWidth(null);
            int h = img.getHeight(null);  
            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);       
            Graphics2D g = scaled.createGraphics();
            g.drawImage(img, 0, 0,null); 
            if(g != null) g.dispose();
            ImageIO.write(scaled, "", new FileOutputStream(outputpath));
            */
            
            int resizeScale = Integer.parseInt(getServletContext().getInitParameter("resizeScale"));
            javaxt.io.Image image = new javaxt.io.Image(is);
            image.saveAs(outputPath);
            if (image.getWidth() > image.getHeight()) {
                image.setWidth(resizeScale);
            } else {
                image.setHeight(resizeScale);
            }
            image.saveAs(outputThumbnailPath);
            double[] gps = image.getGPSCoordinate();

            request.setAttribute("Latitude", gps != null ? gps[1] : 0); 
            request.setAttribute("Longitude", gps != null ? gps[0] : 0); 
            request.setAttribute("ImageUpload", fileThumbnailPath);
        }
        RequestDispatcher view = request.getRequestDispatcher("uploadFile.jsp");
        view.forward(request, response);
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
