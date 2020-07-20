package model;

import entity.t_comment;
import entity.t_photo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.DatabaseHelper;

public class t_photo_model {

    private DatabaseHelper dbHelper;
    
    public class Sort {
        public static final String ASC = "ASC";
        public static final String DESC = "DESC";
    }

    public long insert(t_photo photo) throws SQLException {
        long rowID = 0;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + t_photo.TABLE_NAME + " WHERE " + t_photo.ColumnName.ROWID + " = 0", conn, false);

            rs.addNew();
            rs.setValue(t_photo.ColumnName.ROWID_M_MEMBER, photo.getRowID_M_Member());
            rs.setValue(t_photo.ColumnName.PHOTONAME, photo.getPhotoName());
            rs.setValue(t_photo.ColumnName.PHOTODESCRIPTION, photo.getPhotoDescription());
            rs.setValue(t_photo.ColumnName.PHOTODATE, photo.getPhotoDate());
            rs.setValue(t_photo.ColumnName.FILEPATH, photo.getFilePath());
            rs.setValue(t_photo.ColumnName.FILETHUMBNAILPATH, photo.getFileThumbnailPath());
            rs.setValue(t_photo.ColumnName.LATITUDEORIGINAL, photo.getLatitudeOriginal());
            rs.setValue(t_photo.ColumnName.LONGITUDEORIGINAL, photo.getLongitudeOriginal());
            rs.setValue(t_photo.ColumnName.LATITUDEMAP, photo.getLatitudeMap());
            rs.setValue(t_photo.ColumnName.LONGITUDEMAP, photo.getLongitudeMap());
            rs.setValue(t_photo.ColumnName.VIDEOPATH, photo.getVideoPath());
            rs.setValue(t_photo.ColumnName.CREATEDATE, photo.getCreateDate());
            rs.update();
            rs.close();

            rowID = rs.getGeneratedKey().toLong();
        } catch (SQLException ex) {
            Logger.getLogger(t_photo_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return rowID;
    }

    public void update(t_photo photo) throws SQLException {
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + t_photo.TABLE_NAME + " WHERE " + t_photo.ColumnName.ROWID + " = " + photo.getRowID(), conn, false);

            rs.setValue(t_photo.ColumnName.PHOTONAME, photo.getPhotoName());
            rs.setValue(t_photo.ColumnName.PHOTODESCRIPTION, photo.getPhotoDescription());
            rs.setValue(t_photo.ColumnName.PHOTODATE, photo.getPhotoDate());
            rs.setValue(t_photo.ColumnName.FILEPATH, photo.getFilePath());
            rs.setValue(t_photo.ColumnName.FILETHUMBNAILPATH, photo.getFileThumbnailPath());
            rs.setValue(t_photo.ColumnName.LATITUDEORIGINAL, photo.getLatitudeOriginal());
            rs.setValue(t_photo.ColumnName.LONGITUDEORIGINAL, photo.getLongitudeOriginal());
            rs.setValue(t_photo.ColumnName.LATITUDEMAP, photo.getLatitudeMap());
            rs.setValue(t_photo.ColumnName.LONGITUDEMAP, photo.getLongitudeMap());
            rs.setValue(t_photo.ColumnName.VIDEOPATH, photo.getVideoPath());
            rs.setValue(t_photo.ColumnName.UPDATEDATE, photo.getUpdateDate());
            rs.update();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_photo_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
    }

    public void delete(long rowID) throws SQLException {
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            conn.execute("DELETE FROM " + t_photo.TABLE_NAME + " WHERE " + t_photo.ColumnName.ROWID + " = " + rowID);
            conn.execute("DELETE FROM " + t_comment.TABLE_NAME + " WHERE " + t_comment.ColumnName.ROWID_T_PHOTO + " = " + rowID);
        } catch (SQLException ex) {
            Logger.getLogger(t_photo_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
    }

    public List<t_photo> listAll(String sort) throws SQLException {
        t_photo photo;
        List<t_photo> list = new ArrayList<t_photo>();
        String sql = "SELECT * FROM " + t_photo.TABLE_NAME + " ";
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            if (sort != null && !sort.equals("")) {
                sql += "ORDER BY " + t_photo.ColumnName.ROWID + " " + sort;
            }
            
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(sql, conn);

            while (rs.hasNext()) {
                photo = new t_photo();
                photo.setRowID(rs.getValue(t_photo.ColumnName.ROWID).toLong());
                photo.setRowID_M_Member(rs.getValue(t_photo.ColumnName.ROWID_M_MEMBER).toLong());
                photo.setPhotoName(rs.getValue(t_photo.ColumnName.PHOTONAME).toString());
                photo.setPhotoDescription(rs.getValue(t_photo.ColumnName.PHOTODESCRIPTION).toString());
                if (rs.getValue(t_photo.ColumnName.PHOTODATE).isNull()) {
                    photo.setPhotoDate(null);
                } else {
                    photo.setPhotoDate(new Date(rs.getValue(t_photo.ColumnName.PHOTODATE).toTimeStamp().getTime()));
                }
                photo.setFilePath(rs.getValue(t_photo.ColumnName.FILEPATH).toString());
                photo.setFileThumbnailPath(rs.getValue(t_photo.ColumnName.FILETHUMBNAILPATH).toString());
                photo.setLatitudeOriginal(rs.getValue(t_photo.ColumnName.LATITUDEORIGINAL).toDouble());
                photo.setLongitudeOriginal(rs.getValue(t_photo.ColumnName.LONGITUDEORIGINAL).toDouble());
                photo.setLatitudeMap(rs.getValue(t_photo.ColumnName.LATITUDEMAP).toDouble());
                photo.setLongitudeMap(rs.getValue(t_photo.ColumnName.LONGITUDEMAP).toDouble());
                photo.setVideoPath(rs.getValue(t_photo.ColumnName.VIDEOPATH).toString());
                photo.setNumOfComment(rs.getValue(t_photo.ColumnName.NUMOFCOMMENT).toInteger());
                photo.setCreateDate(new Date(rs.getValue(t_photo.ColumnName.CREATEDATE).toTimeStamp().getTime()));
                if (rs.getValue(t_photo.ColumnName.UPDATEDATE).isNull()) {
                    photo.setUpdateDate(null);
                } else {
                    photo.setUpdateDate(new Date(rs.getValue(t_photo.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(photo);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_photo_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }
    
    public List<t_photo> listAllRange(int start, int length, String sort) throws SQLException {
        t_photo photo;
        List<t_photo> list = new ArrayList<t_photo>();
        String sql = "";
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            sql += "SELECT * FROM " + t_photo.TABLE_NAME + " ";
            if (sort != null && !sort.equals("")) {
                sql += "ORDER BY " + t_photo.ColumnName.ROWID + " " + sort + " ";
            }
            sql += "LIMIT " + start + ", " + length + " ";
            
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(sql, conn);

            while (rs.hasNext()) {
                photo = new t_photo();
                photo.setRowID(rs.getValue(t_photo.ColumnName.ROWID).toLong());
                photo.setRowID_M_Member(rs.getValue(t_photo.ColumnName.ROWID_M_MEMBER).toLong());
                photo.setPhotoName(rs.getValue(t_photo.ColumnName.PHOTONAME).toString());
                photo.setPhotoDescription(rs.getValue(t_photo.ColumnName.PHOTODESCRIPTION).toString());
                if (rs.getValue(t_photo.ColumnName.PHOTODATE).isNull()) {
                    photo.setPhotoDate(null);
                } else {
                    photo.setPhotoDate(new Date(rs.getValue(t_photo.ColumnName.PHOTODATE).toTimeStamp().getTime()));
                }
                photo.setFilePath(rs.getValue(t_photo.ColumnName.FILEPATH).toString());
                photo.setFileThumbnailPath(rs.getValue(t_photo.ColumnName.FILETHUMBNAILPATH).toString());
                photo.setLatitudeOriginal(rs.getValue(t_photo.ColumnName.LATITUDEORIGINAL).toDouble());
                photo.setLongitudeOriginal(rs.getValue(t_photo.ColumnName.LONGITUDEORIGINAL).toDouble());
                photo.setLatitudeMap(rs.getValue(t_photo.ColumnName.LATITUDEMAP).toDouble());
                photo.setLongitudeMap(rs.getValue(t_photo.ColumnName.LONGITUDEMAP).toDouble());
                photo.setVideoPath(rs.getValue(t_photo.ColumnName.VIDEOPATH).toString());
                photo.setNumOfComment(rs.getValue(t_photo.ColumnName.NUMOFCOMMENT).toInteger());
                photo.setCreateDate(new Date(rs.getValue(t_photo.ColumnName.CREATEDATE).toTimeStamp().getTime()));
                if (rs.getValue(t_photo.ColumnName.UPDATEDATE).isNull()) {
                    photo.setUpdateDate(null);
                } else {
                    photo.setUpdateDate(new Date(rs.getValue(t_photo.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(photo);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_photo_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }
    
    public List<t_photo> loadByPrimaryKey(Long rowID) throws SQLException {
        t_photo photo;
        List<t_photo> list = new ArrayList<t_photo>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + t_photo.TABLE_NAME + " WHERE " + t_photo.ColumnName.ROWID + " = " + rowID, conn);

            while (rs.hasNext()) {
                photo = new t_photo();
                photo.setRowID(rs.getValue(t_photo.ColumnName.ROWID).toLong());
                photo.setRowID_M_Member(rs.getValue(t_photo.ColumnName.ROWID_M_MEMBER).toLong());
                photo.setPhotoName(rs.getValue(t_photo.ColumnName.PHOTONAME).toString());
                photo.setPhotoDescription(rs.getValue(t_photo.ColumnName.PHOTODESCRIPTION).toString());
                if (rs.getValue(t_photo.ColumnName.PHOTODATE).isNull()) {
                    photo.setPhotoDate(null);
                } else {
                    photo.setPhotoDate(new Date(rs.getValue(t_photo.ColumnName.PHOTODATE).toTimeStamp().getTime()));
                }
                photo.setFilePath(rs.getValue(t_photo.ColumnName.FILEPATH).toString());
                photo.setFileThumbnailPath(rs.getValue(t_photo.ColumnName.FILETHUMBNAILPATH).toString());
                photo.setLatitudeOriginal(rs.getValue(t_photo.ColumnName.LATITUDEORIGINAL).toDouble());
                photo.setLongitudeOriginal(rs.getValue(t_photo.ColumnName.LONGITUDEORIGINAL).toDouble());
                photo.setLatitudeMap(rs.getValue(t_photo.ColumnName.LATITUDEMAP).toDouble());
                photo.setLongitudeMap(rs.getValue(t_photo.ColumnName.LONGITUDEMAP).toDouble());
                photo.setVideoPath(rs.getValue(t_photo.ColumnName.VIDEOPATH).toString());
                photo.setNumOfComment(rs.getValue(t_photo.ColumnName.NUMOFCOMMENT).toInteger());
                photo.setCreateDate(new Date(rs.getValue(t_photo.ColumnName.CREATEDATE).toTimeStamp().getTime()));
                if (rs.getValue(t_photo.ColumnName.UPDATEDATE).isNull()) {
                    photo.setUpdateDate(null);
                } else {
                    photo.setUpdateDate(new Date(rs.getValue(t_photo.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(photo);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_photo_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }
    
    public List<t_photo> loadByMember(Long rowID_M_Member, String sort) throws SQLException {
        t_photo photo;
        List<t_photo> list = new ArrayList<t_photo>();
        String sql = "SELECT * FROM " + t_photo.TABLE_NAME + " WHERE " + t_photo.ColumnName.ROWID_M_MEMBER + " = " + rowID_M_Member + " ";
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            if (sort != null && !sort.equals("")) {
                sql += "ORDER BY " + t_photo.ColumnName.ROWID + " " + sort;
            }
            
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(sql, conn);

            while (rs.hasNext()) {
                photo = new t_photo();
                photo.setRowID(rs.getValue(t_photo.ColumnName.ROWID).toLong());
                photo.setRowID_M_Member(rs.getValue(t_photo.ColumnName.ROWID_M_MEMBER).toLong());
                photo.setPhotoName(rs.getValue(t_photo.ColumnName.PHOTONAME).toString());
                photo.setPhotoDescription(rs.getValue(t_photo.ColumnName.PHOTODESCRIPTION).toString());
                if (rs.getValue(t_photo.ColumnName.PHOTODATE).isNull()) {
                    photo.setPhotoDate(null);
                } else {
                    photo.setPhotoDate(new Date(rs.getValue(t_photo.ColumnName.PHOTODATE).toTimeStamp().getTime()));
                }
                photo.setFilePath(rs.getValue(t_photo.ColumnName.FILEPATH).toString());
                photo.setFileThumbnailPath(rs.getValue(t_photo.ColumnName.FILETHUMBNAILPATH).toString());
                photo.setLatitudeOriginal(rs.getValue(t_photo.ColumnName.LATITUDEORIGINAL).toDouble());
                photo.setLongitudeOriginal(rs.getValue(t_photo.ColumnName.LONGITUDEORIGINAL).toDouble());
                photo.setLatitudeMap(rs.getValue(t_photo.ColumnName.LATITUDEMAP).toDouble());
                photo.setLongitudeMap(rs.getValue(t_photo.ColumnName.LONGITUDEMAP).toDouble());
                photo.setVideoPath(rs.getValue(t_photo.ColumnName.VIDEOPATH).toString());
                photo.setNumOfComment(rs.getValue(t_photo.ColumnName.NUMOFCOMMENT).toInteger());
                photo.setCreateDate(new Date(rs.getValue(t_photo.ColumnName.CREATEDATE).toTimeStamp().getTime()));
                if (rs.getValue(t_photo.ColumnName.UPDATEDATE).isNull()) {
                    photo.setUpdateDate(null);
                } else {
                    photo.setUpdateDate(new Date(rs.getValue(t_photo.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(photo);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_photo_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }
    
    public List<t_photo> loadByMemberRange(Long rowID_M_Member, int start, int length, String sort) throws SQLException {
        t_photo photo;
        List<t_photo> list = new ArrayList<t_photo>();
        String sql = "";
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            sql += "SELECT * FROM " + t_photo.TABLE_NAME + " WHERE " + t_photo.ColumnName.ROWID_M_MEMBER + " = " + rowID_M_Member + " ";
            if (sort != null && !sort.equals("")) {
                sql += "ORDER BY " + t_photo.ColumnName.ROWID + " " + sort + " ";
            }
            sql += "LIMIT " + start + ", " + length + " ";
            
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(sql, conn);

            while (rs.hasNext()) {
                photo = new t_photo();
                photo.setRowID(rs.getValue(t_photo.ColumnName.ROWID).toLong());
                photo.setRowID_M_Member(rs.getValue(t_photo.ColumnName.ROWID_M_MEMBER).toLong());
                photo.setPhotoName(rs.getValue(t_photo.ColumnName.PHOTONAME).toString());
                photo.setPhotoDescription(rs.getValue(t_photo.ColumnName.PHOTODESCRIPTION).toString());
                if (rs.getValue(t_photo.ColumnName.PHOTODATE).isNull()) {
                    photo.setPhotoDate(null);
                } else {
                    photo.setPhotoDate(new Date(rs.getValue(t_photo.ColumnName.PHOTODATE).toTimeStamp().getTime()));
                }
                photo.setFilePath(rs.getValue(t_photo.ColumnName.FILEPATH).toString());
                photo.setFileThumbnailPath(rs.getValue(t_photo.ColumnName.FILETHUMBNAILPATH).toString());
                photo.setLatitudeOriginal(rs.getValue(t_photo.ColumnName.LATITUDEORIGINAL).toDouble());
                photo.setLongitudeOriginal(rs.getValue(t_photo.ColumnName.LONGITUDEORIGINAL).toDouble());
                photo.setLatitudeMap(rs.getValue(t_photo.ColumnName.LATITUDEMAP).toDouble());
                photo.setLongitudeMap(rs.getValue(t_photo.ColumnName.LONGITUDEMAP).toDouble());
                photo.setVideoPath(rs.getValue(t_photo.ColumnName.VIDEOPATH).toString());
                photo.setNumOfComment(rs.getValue(t_photo.ColumnName.NUMOFCOMMENT).toInteger());
                photo.setCreateDate(new Date(rs.getValue(t_photo.ColumnName.CREATEDATE).toTimeStamp().getTime()));
                if (rs.getValue(t_photo.ColumnName.UPDATEDATE).isNull()) {
                    photo.setUpdateDate(null);
                } else {
                    photo.setUpdateDate(new Date(rs.getValue(t_photo.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(photo);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_photo_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }
    
    public List<t_photo> loadByViewport(double southwestLat, double southwestLng, double northeastLat, double northeastLng) throws SQLException {
        t_photo photo;
        List<t_photo> list = new ArrayList<t_photo>();
        String sql = "";
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            sql += "SELECT * FROM " + t_photo.TABLE_NAME + " ";
            sql += "WHERE (" + t_photo.ColumnName.LATITUDEMAP + " <> 0 AND " + t_photo.ColumnName.LONGITUDEMAP + " <> 0) ";
            sql += "AND (" + t_photo.ColumnName.LATITUDEMAP + " BETWEEN " + southwestLat + " AND " + northeastLat + ") ";
            sql += "AND (" + t_photo.ColumnName.LONGITUDEMAP + " BETWEEN " + southwestLng + " AND " + northeastLng + ") ";
            
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(sql, conn);

            while (rs.hasNext()) {
                photo = new t_photo();
                photo.setRowID(rs.getValue(t_photo.ColumnName.ROWID).toLong());
                photo.setRowID_M_Member(rs.getValue(t_photo.ColumnName.ROWID_M_MEMBER).toLong());
                photo.setPhotoName(rs.getValue(t_photo.ColumnName.PHOTONAME).toString());
                photo.setPhotoDescription(rs.getValue(t_photo.ColumnName.PHOTODESCRIPTION).toString());
                if (rs.getValue(t_photo.ColumnName.PHOTODATE).isNull()) {
                    photo.setPhotoDate(null);
                } else {
                    photo.setPhotoDate(new Date(rs.getValue(t_photo.ColumnName.PHOTODATE).toTimeStamp().getTime()));
                }
                photo.setFilePath(rs.getValue(t_photo.ColumnName.FILEPATH).toString());
                photo.setFileThumbnailPath(rs.getValue(t_photo.ColumnName.FILETHUMBNAILPATH).toString());
                photo.setLatitudeOriginal(rs.getValue(t_photo.ColumnName.LATITUDEORIGINAL).toDouble());
                photo.setLongitudeOriginal(rs.getValue(t_photo.ColumnName.LONGITUDEORIGINAL).toDouble());
                photo.setLatitudeMap(rs.getValue(t_photo.ColumnName.LATITUDEMAP).toDouble());
                photo.setLongitudeMap(rs.getValue(t_photo.ColumnName.LONGITUDEMAP).toDouble());
                photo.setVideoPath(rs.getValue(t_photo.ColumnName.VIDEOPATH).toString());
                photo.setNumOfComment(rs.getValue(t_photo.ColumnName.NUMOFCOMMENT).toInteger());
                photo.setCreateDate(new Date(rs.getValue(t_photo.ColumnName.CREATEDATE).toTimeStamp().getTime()));
                if (rs.getValue(t_photo.ColumnName.UPDATEDATE).isNull()) {
                    photo.setUpdateDate(null);
                } else {
                    photo.setUpdateDate(new Date(rs.getValue(t_photo.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(photo);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_photo_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }
}
