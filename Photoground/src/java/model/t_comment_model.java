package model;

import entity.m_member;
import entity.t_comment;
import entity.t_photo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.DatabaseHelper;

public class t_comment_model {
    
    private DatabaseHelper dbHelper;
    
    public class Sort {
        public static final String ASC = "ASC";
        public static final String DESC = "DESC";
    }
    
    public long insert(t_comment comment) throws SQLException {
        long rowID = 0;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + t_comment.TABLE_NAME + " WHERE " + t_comment.ColumnName.ROWID + " = 0", conn, false);
            
            rs.addNew();
            rs.setValue(t_comment.ColumnName.ROWID_M_MEMBER, comment.getRowID_M_Member());
            rs.setValue(t_comment.ColumnName.ROWID_T_PHOTO, comment.getRowID_T_Photo());
            rs.setValue(t_comment.ColumnName.COMMENT, comment.getComment());
            rs.setValue(t_comment.ColumnName.COMMENTDATE, comment.getCommentDate());
            rs.update();
            rs.close();
            
            rowID = rs.getGeneratedKey().toLong();
            
            conn.execute("UPDATE " + t_photo.TABLE_NAME + " "
                        + "SET " + t_photo.ColumnName.NUMOFCOMMENT + " = " + t_photo.ColumnName.NUMOFCOMMENT + " + 1 "
                        + "WHERE " + t_photo.ColumnName.ROWID + " = " + comment.getRowID_T_Photo());
        } catch (SQLException ex) {
            Logger.getLogger(t_comment_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
        
        return rowID;
    }
    
    public void update(t_comment comment) throws SQLException {
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + t_comment.TABLE_NAME + " WHERE " + t_comment.ColumnName.ROWID + " = " + comment.getRowID(), conn, false);
            
            rs.setValue(t_comment.ColumnName.COMMENT, comment.getComment());
            rs.update();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_comment_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
    }
    
    public List<t_comment> listAll(String sort) throws SQLException {
        t_comment comment;
        List<t_comment> list = new ArrayList<t_comment>();
        String sql = "";
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            sql += "SELECT ";
            sql += "c." + t_comment.ColumnName.ROWID + ", ";
            sql += "c." + t_comment.ColumnName.ROWID_M_MEMBER + ", ";
            sql += "m." + m_member.ColumnName.NAME + ", ";
            sql += "m." + m_member.ColumnName.SURNAME + ", ";
            sql += "m." + m_member.ColumnName.USERNAME + ", ";
            sql += "c." + t_comment.ColumnName.ROWID_T_PHOTO + ", ";
            sql += "c." + t_comment.ColumnName.COMMENT + ", ";
            sql += "c." + t_comment.ColumnName.COMMENTDATE + " ";
            sql += "FROM " + t_comment.TABLE_NAME + " c ";
            sql += "INNER JOIN " + m_member.TABLE_NAME + " m ON m." + m_member.ColumnName.ROWID + " = c." + t_comment.ColumnName.ROWID_M_MEMBER + " ";
            if (sort != null && !sort.equals("")) {
                sql += "ORDER BY c." + t_comment.ColumnName.ROWID + " " + sort;
            }
            
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(sql, conn);
            
            while (rs.hasNext()) {
                comment = new t_comment();
                comment.setRowID(rs.getValue(t_comment.ColumnName.ROWID).toLong());
                comment.setRowID_M_Member(rs.getValue(t_comment.ColumnName.ROWID_M_MEMBER).toLong());
                comment.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                comment.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                comment.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                comment.setRowID_T_Photo(rs.getValue(t_comment.ColumnName.ROWID_T_PHOTO).toLong());
                comment.setComment(rs.getValue(t_comment.ColumnName.COMMENT).toString());
                comment.setCommentDate(new Date(rs.getValue(t_comment.ColumnName.COMMENTDATE).toTimeStamp().getTime()));
                list.add(comment);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_comment_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
        
        return list;
    }
    
    public List<t_comment> loadByPhoto(Long rowID_T_Photo, String sort) throws SQLException {
        t_comment comment;
        List<t_comment> list = new ArrayList<t_comment>();
        String sql = "";
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            sql += "SELECT ";
            sql += "c." + t_comment.ColumnName.ROWID + ", ";
            sql += "c." + t_comment.ColumnName.ROWID_M_MEMBER + ", ";
            sql += "m." + m_member.ColumnName.NAME + ", ";
            sql += "m." + m_member.ColumnName.SURNAME + ", ";
            sql += "m." + m_member.ColumnName.USERNAME + ", ";
            sql += "c." + t_comment.ColumnName.ROWID_T_PHOTO + ", ";
            sql += "c." + t_comment.ColumnName.COMMENT + ", ";
            sql += "c." + t_comment.ColumnName.COMMENTDATE + " ";
            sql += "FROM " + t_comment.TABLE_NAME + " c ";
            sql += "INNER JOIN " + m_member.TABLE_NAME + " m ON m." + m_member.ColumnName.ROWID + " = c." + t_comment.ColumnName.ROWID_M_MEMBER + " ";
            sql += "WHERE c." + t_comment.ColumnName.ROWID_T_PHOTO + " = " + rowID_T_Photo + " ";
            if (sort != null && !sort.equals("")) {
                sql += "ORDER BY c." + t_comment.ColumnName.ROWID + " " + sort;
            }
            
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(sql, conn);
            
            while (rs.hasNext()) {
                comment = new t_comment();
                comment.setRowID(rs.getValue(t_comment.ColumnName.ROWID).toLong());
                comment.setRowID_M_Member(rs.getValue(t_comment.ColumnName.ROWID_M_MEMBER).toLong());
                comment.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                comment.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                comment.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                comment.setRowID_T_Photo(rs.getValue(t_comment.ColumnName.ROWID_T_PHOTO).toLong());
                comment.setComment(rs.getValue(t_comment.ColumnName.COMMENT).toString());
                comment.setCommentDate(new Date(rs.getValue(t_comment.ColumnName.COMMENTDATE).toTimeStamp().getTime()));
                list.add(comment);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_comment_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
        
        return list;
    }
    
    public List<t_comment> loadByPhotoSize(Long rowID_T_Photo, int size, String sort) throws SQLException {
        t_comment comment;
        List<t_comment> list = new ArrayList<t_comment>();
        String sql = "";
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            sql += "SELECT ";
            sql += "c." + t_comment.ColumnName.ROWID + ", ";
            sql += "c." + t_comment.ColumnName.ROWID_M_MEMBER + ", ";
            sql += "m." + m_member.ColumnName.NAME + ", ";
            sql += "m." + m_member.ColumnName.SURNAME + ", ";
            sql += "m." + m_member.ColumnName.USERNAME + ", ";
            sql += "c." + t_comment.ColumnName.ROWID_T_PHOTO + ", ";
            sql += "c." + t_comment.ColumnName.COMMENT + ", ";
            sql += "c." + t_comment.ColumnName.COMMENTDATE + " ";
            sql += "FROM " + t_comment.TABLE_NAME + " c ";
            sql += "INNER JOIN " + m_member.TABLE_NAME + " m ON m." + m_member.ColumnName.ROWID + " = c." + t_comment.ColumnName.ROWID_M_MEMBER + " ";
            sql += "WHERE c." + t_comment.ColumnName.ROWID_T_PHOTO + " = " + rowID_T_Photo + " ";
            if (sort != null && !sort.equals("")) {
                sql += "ORDER BY c." + t_comment.ColumnName.ROWID + " " + sort + " ";
            } 
            if (size > 0) {
                sql += "LIMIT " + size + " ";
            }
            
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(sql, conn);
            
            while (rs.hasNext()) {
                comment = new t_comment();
                comment.setRowID(rs.getValue(t_comment.ColumnName.ROWID).toLong());
                comment.setRowID_M_Member(rs.getValue(t_comment.ColumnName.ROWID_M_MEMBER).toLong());
                comment.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                comment.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                comment.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                comment.setRowID_T_Photo(rs.getValue(t_comment.ColumnName.ROWID_T_PHOTO).toLong());
                comment.setComment(rs.getValue(t_comment.ColumnName.COMMENT).toString());
                comment.setCommentDate(new Date(rs.getValue(t_comment.ColumnName.COMMENTDATE).toTimeStamp().getTime()));
                list.add(comment);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(t_comment_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
        
        return list;
    }
}
