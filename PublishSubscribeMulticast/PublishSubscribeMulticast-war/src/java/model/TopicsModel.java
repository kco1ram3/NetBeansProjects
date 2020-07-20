package model;

import entity.Topics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.DatabaseHelper;

public class TopicsModel {

    private DatabaseHelper dbHelper;

    public void insert(Topics topic) throws SQLException {
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + Topics.TABLE_NAME + " WHERE " + Topics.ColumnName.NAME + " = ''", conn, false);

            rs.addNew();
            rs.setValue(Topics.ColumnName.NAME, topic.getName());
            rs.setValue(Topics.ColumnName.IP, topic.getIP());
            rs.setValue(Topics.ColumnName.PORT, topic.getPort());
            rs.update();
            rs.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(TopicsModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
    }

    public List<Topics> listAll() throws SQLException {
        Topics topic;
        List<Topics> list = new ArrayList<Topics>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + Topics.TABLE_NAME, conn);

            while (rs.hasNext()) {
                topic = new Topics();
                topic.setName(rs.getValue(Topics.ColumnName.NAME).toString());
                topic.setIP(rs.getValue(Topics.ColumnName.IP).toString());
                topic.setPort(Integer.parseInt(rs.getValue(Topics.ColumnName.PORT).toString()));
                list.add(topic);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(TopicsModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }

    public Topics loadByName(String topicName) throws SQLException {
        Topics topic = new Topics();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(String.format("SELECT * FROM " + Topics.TABLE_NAME + " WHERE " + Topics.ColumnName.NAME + " = '%s'", topicName), conn);

            if (rs.hasNext()) {
                topic.setName(rs.getValue(Topics.ColumnName.NAME).toString());
                topic.setIP(rs.getValue(Topics.ColumnName.IP).toString());
                topic.setPort(Integer.parseInt(rs.getValue(Topics.ColumnName.PORT).toString()));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(TopicsModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return topic;
    }

    public boolean checkExistTopic(String topicName) throws SQLException {
        boolean isExist = false;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(String.format("SELECT * FROM " + Topics.TABLE_NAME + " "
                    + "WHERE " + Topics.ColumnName.NAME + " = '%s'", topicName), conn);

            if (rs.hasNext()) {
                isExist = true;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(TopicsModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return isExist;
    }
    
    public int getPort(String ip) {
        int port = 10000;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(String.format("SELECT MAX(" + Topics.ColumnName.PORT + ") AS '" + Topics.ColumnName.PORT + "' FROM " + Topics.TABLE_NAME + " WHERE " + Topics.ColumnName.IP + " = '%s'", ip), conn);

            if (rs.hasNext()) {
                port = Integer.parseInt(rs.getValue(Topics.ColumnName.PORT).toString()) + 1;
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(TopicsModel.class.getName()).log(Level.SEVERE, null, ex);
            //throw ex;
        } 
        conn.close();

        return port;
    }
}
