package model;

import entity.m_member;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.DatabaseHelper;

public class m_member_model {
    
    private DatabaseHelper dbHelper;
    
    public long insert(m_member member) throws SQLException {
        long rowID = 0;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " WHERE " + m_member.ColumnName.ROWID + " = 0", conn, false);
            
            rs.addNew();
            rs.setValue(m_member.ColumnName.NAME, member.getName());
            rs.setValue(m_member.ColumnName.SURNAME, member.getSurname());
            rs.setValue(m_member.ColumnName.EMAIL, member.getEmail());
            rs.setValue(m_member.ColumnName.USERNAME, member.getUsername());
            rs.setValue(m_member.ColumnName.PASSWORD, member.getPassword());
            rs.setValue(m_member.ColumnName.REGISTERDATE, member.getRegisterDate());
            rs.update();
            rs.close();
            
            rowID = rs.getGeneratedKey().toLong();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
        
        return rowID;
    }
    
    public void update(m_member member) throws SQLException {
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " WHERE " + m_member.ColumnName.ROWID + " = " + member.getRowID(), conn, false);
            
            rs.setValue(m_member.ColumnName.NAME, member.getName());
            rs.setValue(m_member.ColumnName.SURNAME, member.getSurname());
            rs.setValue(m_member.ColumnName.EMAIL, member.getEmail());
            rs.setValue(m_member.ColumnName.USERNAME, member.getUsername());
            rs.setValue(m_member.ColumnName.PASSWORD, member.getPassword());
            rs.setValue(m_member.ColumnName.UPDATEDATE, member.getUpdateDate());
            rs.update();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
    }
    
    public List<m_member> listAll() throws SQLException {
        m_member member;
        List<m_member> list = new ArrayList<m_member>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME, conn);
            
            while (rs.hasNext()) {
                member = new m_member();
                member.setRowID(rs.getValue(m_member.ColumnName.ROWID).toLong());
                member.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                member.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                member.setEmail(rs.getValue(m_member.ColumnName.EMAIL).toString());
                member.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                member.setPassword(rs.getValue(m_member.ColumnName.PASSWORD).toString());
                member.setRegisterDate(new Date(rs.getValue(m_member.ColumnName.REGISTERDATE).toTimeStamp().getTime()));
                if (rs.getValue(m_member.ColumnName.UPDATEDATE).isNull()) {
                    member.setUpdateDate(null);
                } else {
                    member.setUpdateDate(new Date(rs.getValue(m_member.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(member);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
        
        return list;
    }
    
    public List<m_member> loadByPrimaryKey(Long rowID) throws SQLException {
        m_member member;
        List<m_member> list = new ArrayList<m_member>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " WHERE " + m_member.ColumnName.ROWID + " = " + rowID, conn);
            
            while (rs.hasNext()) {
                member = new m_member();
                member.setRowID(rs.getValue(m_member.ColumnName.ROWID).toLong());
                member.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                member.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                member.setEmail(rs.getValue(m_member.ColumnName.EMAIL).toString());
                member.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                member.setPassword(rs.getValue(m_member.ColumnName.PASSWORD).toString());
                member.setRegisterDate(new Date(rs.getValue(m_member.ColumnName.REGISTERDATE).toTimeStamp().getTime()));
                if (rs.getValue(m_member.ColumnName.UPDATEDATE).isNull()) {
                    member.setUpdateDate(null);
                } else {
                    member.setUpdateDate(new Date(rs.getValue(m_member.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(member);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
        
        return list;
    }
    
    public List<m_member> login(String username, String password) throws SQLException {
        m_member member;
        List<m_member> list = new ArrayList<m_member>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " "
                    + "WHERE " + m_member.ColumnName.USERNAME + " = '" + username + "' "
                    + "AND " + m_member.ColumnName.PASSWORD + " = '" + password + "'", conn);
            
            while (rs.hasNext()) {
                member = new m_member();
                member.setRowID(rs.getValue(m_member.ColumnName.ROWID).toLong());
                member.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                member.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                member.setEmail(rs.getValue(m_member.ColumnName.EMAIL).toString());
                member.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                member.setPassword(rs.getValue(m_member.ColumnName.PASSWORD).toString());
                member.setRegisterDate(new Date(rs.getValue(m_member.ColumnName.REGISTERDATE).toTimeStamp().getTime()));
                if (rs.getValue(m_member.ColumnName.UPDATEDATE).isNull()) {
                    member.setUpdateDate(null);
                } else {
                    member.setUpdateDate(new Date(rs.getValue(m_member.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(member);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
        
        return list;
    }
    
    public boolean checkExistUsername(String username) throws SQLException {
        boolean isExist = false;
        m_member member;
        List<m_member> list = new ArrayList<m_member>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " "
                    + "WHERE " + m_member.ColumnName.USERNAME + " = '" + username + "'", conn);
            
            if (rs.hasNext()) {
                isExist = true;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
        
        return isExist;
    }
}
