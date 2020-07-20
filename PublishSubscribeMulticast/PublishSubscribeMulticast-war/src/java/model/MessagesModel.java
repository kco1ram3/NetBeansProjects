package model;

import entity.Messages;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.DatabaseHelper;

public class MessagesModel {

    private DatabaseHelper dbHelper;

    public long insert(Messages message) throws SQLException {
        long rowID = 0;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + Messages.TABLE_NAME + " WHERE " + Messages.ColumnName.ROWID + " = 0", conn, false);

            rs.addNew();
            rs.setValue(Messages.ColumnName.TOPICNAME, message.getTopicName());
            rs.setValue(Messages.ColumnName.MESSAGE, message.getMessage());
            rs.setValue(Messages.ColumnName.SENDDATE, message.getSendDate());
            rs.update();
            rs.close();

            rowID = rs.getGeneratedKey().toLong();
        } catch (SQLException ex) {
            Logger.getLogger(MessagesModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return rowID;
    }

    public void update(Messages message) throws SQLException {
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();

            rs.open(String.format("SELECT * FROM " + Messages.TABLE_NAME + " WHERE " + Messages.ColumnName.ROWID + " = %d", message.getRowID()), conn, false);

            if (message.getTopicName() != null) {
                rs.setValue(Messages.ColumnName.TOPICNAME, message.getTopicName());
            }
            if (message.getMessage() != null) {
                rs.setValue(Messages.ColumnName.MESSAGE, message.getMessage());
            }
            if (message.getSendDate() != 0) {
                rs.setValue(Messages.ColumnName.SENDDATE, message.getSendDate());
            }
            /*
            if (message.getSendDate() != null) {
                rs.setValue(Messages.ColumnName.SENDDATE, message.getSendDate());
            }
            */
            rs.update();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessagesModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
    }

    public List<Messages> listAll() throws SQLException {
        Messages message;
        List<Messages> list = new ArrayList<Messages>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + Messages.TABLE_NAME, conn);

            while (rs.hasNext()) {
                message = new Messages();
                if (!rs.getValue(Messages.ColumnName.ROWID).isNull()) {
                    message.setRowID(rs.getValue(Messages.ColumnName.ROWID).toLong());
                }
                if (!rs.getValue(Messages.ColumnName.TOPICNAME).isNull()) {
                    message.setTopicName(rs.getValue(Messages.ColumnName.TOPICNAME).toString());
                }
                if (!rs.getValue(Messages.ColumnName.MESSAGE).isNull()) {
                    message.setMessage(rs.getValue(Messages.ColumnName.MESSAGE).toString());
                }
                if (!rs.getValue(Messages.ColumnName.SENDDATE).isNull()) {
                    message.setSendDate(rs.getValue(Messages.ColumnName.SENDDATE).toLong());
                }
                /*
                if (!rs.getValue(Messages.ColumnName.SENDDATE).isNull()) {
                    message.setSendDate(new Date(rs.getValue(Messages.ColumnName.SENDDATE).toTimeStamp().getTime()));
                }
                */
                list.add(message);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessagesModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }

    public Messages loadByPrimaryKey(Long rowID) throws SQLException {
        Messages message = new Messages();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(String.format("SELECT * FROM " + Messages.TABLE_NAME + " WHERE " + Messages.ColumnName.ROWID + " = %d", rowID), conn);

            if (rs.hasNext()) {
                if (!rs.getValue(Messages.ColumnName.ROWID).isNull()) {
                    message.setRowID(rs.getValue(Messages.ColumnName.ROWID).toLong());
                }
                if (!rs.getValue(Messages.ColumnName.TOPICNAME).isNull()) {
                    message.setTopicName(rs.getValue(Messages.ColumnName.TOPICNAME).toString());
                }
                if (!rs.getValue(Messages.ColumnName.MESSAGE).isNull()) {
                    message.setMessage(rs.getValue(Messages.ColumnName.MESSAGE).toString());
                }
                if (!rs.getValue(Messages.ColumnName.SENDDATE).isNull()) {
                    message.setSendDate(rs.getValue(Messages.ColumnName.SENDDATE).toLong());
                }
                /*
                if (!rs.getValue(Messages.ColumnName.SENDDATE).isNull()) {
                    message.setSendDate(new Date(rs.getValue(Messages.ColumnName.SENDDATE).toTimeStamp().getTime()));
                }
                */
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessagesModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return message;
    }
}
