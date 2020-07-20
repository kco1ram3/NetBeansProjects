package controller;

import entity.t_photo;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.t_photo_model;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AddressJsonServlet extends HttpServlet {

    private double southwestLat = 0;
    private double southwestLng = 0;
    private double northeastLat = 0;
    private double northeastLng = 0;
    private String viewport = "";

    private double ParseDouble(String value) {
        double db = 0;
        try {
            db = Double.parseDouble(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return db;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        southwestLat = 0;
        southwestLng = 0;
        northeastLat = 0;
        northeastLng = 0;
        String[] point = null;
        viewport = request.getParameter("viewport");
        if (viewport != null) {
            viewport = viewport.replace("(", "").replace(")", "");
            point = viewport.split(",");
            southwestLat = ParseDouble(point[0].trim());
            southwestLng = ParseDouble(point[1].trim());
            northeastLat = ParseDouble(point[2].trim());
            northeastLng = ParseDouble(point[3].trim());
        }

        response.setContentType("application/json"); 
        PrintWriter out = response.getWriter();

        JSONObject json = new JSONObject();
        JSONArray addresses = new JSONArray();
        JSONObject address;
        t_photo_model photo = new t_photo_model();
        List<t_photo> list = null;
        try {
            list = photo.loadByViewport(southwestLat, southwestLng, northeastLat, northeastLng);
            for (int item = 0; item < list.size(); item++) {
                address = new JSONObject();
                address.put(t_photo.ColumnName.ROWID, list.get(item).getRowID());
                address.put(t_photo.ColumnName.PHOTONAME, list.get(item).getPhotoName());
                address.put(t_photo.ColumnName.FILEPATH, list.get(item).getFilePath());
                address.put(t_photo.ColumnName.FILETHUMBNAILPATH, list.get(item).getFileThumbnailPath());
                address.put(t_photo.ColumnName.LATITUDEMAP, list.get(item).getLatitudeMap());
                address.put(t_photo.ColumnName.LONGITUDEMAP, list.get(item).getLongitudeMap());
                addresses.add(address);
            }
            json.put("Addresses", addresses);
            
            //Thread.sleep(1000);
            
            out.print(json);
        } catch (Exception ex) {
            out.println("<br /><font color='#FF0000'>" + ex.getMessage() + "</font>");
            Logger.getLogger(AddressJsonServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            json = null;
            photo = null;
            out.flush();
            out.close();
        }
    }
}
