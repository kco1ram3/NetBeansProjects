package model;

import entity.PerformanceTest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.DatabaseHelper;

public class PerformanceTestModel {

    private DatabaseHelper dbHelper;

    public long insert(PerformanceTest performance) throws SQLException {
        long rowID = 0;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + PerformanceTest.TABLE_NAME + " WHERE " + PerformanceTest.ColumnName.ROWID + " = 0", conn, false);

            rs.addNew();
            rs.setValue(PerformanceTest.ColumnName.ROWID_MESSAGE, performance.getRowID_Message());
            rs.setValue(PerformanceTest.ColumnName.THREADID, performance.getThreadID());
            rs.setValue(PerformanceTest.ColumnName.RESPONSEDATE, performance.getResponseDate());
            rs.update();
            rs.close();

            rowID = rs.getGeneratedKey().toLong();
        } catch (SQLException ex) {
            Logger.getLogger(PerformanceTestModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return rowID;
    }

    public void update(PerformanceTest performance) throws SQLException {
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();

            rs.open(String.format("SELECT * FROM " + PerformanceTest.TABLE_NAME + " WHERE " + PerformanceTest.ColumnName.ROWID + " = %d", performance.getRowID()), conn, false);

            if (performance.getRowID_Message() != 0) {
                rs.setValue(PerformanceTest.ColumnName.ROWID_MESSAGE, performance.getRowID_Message());
            }
            if (performance.getThreadID() != 0) {
                rs.setValue(PerformanceTest.ColumnName.THREADID, performance.getThreadID());
            }
            if (performance.getResponseDate() != 0) {
                rs.setValue(PerformanceTest.ColumnName.RESPONSEDATE, performance.getResponseDate());
            }
            /*
            if (performance.getResponseDate() != null) {
                rs.setValue(PerformanceTest.ColumnName.RESPONSEDATE, performance.getResponseDate());
            }
            */
            rs.update();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PerformanceTestModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
    }

    public List<PerformanceTest> listAll() throws SQLException {
        PerformanceTest performance;
        List<PerformanceTest> list = new ArrayList<PerformanceTest>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + PerformanceTest.TABLE_NAME, conn);

            while (rs.hasNext()) {
                performance = new PerformanceTest();
                if (!rs.getValue(PerformanceTest.ColumnName.ROWID).isNull()) {
                    performance.setRowID(rs.getValue(PerformanceTest.ColumnName.ROWID).toLong());
                }
                if (!rs.getValue(PerformanceTest.ColumnName.ROWID_MESSAGE).isNull()) {
                    performance.setRowID_Message(rs.getValue(PerformanceTest.ColumnName.ROWID_MESSAGE).toLong());
                }
                if (!rs.getValue(PerformanceTest.ColumnName.THREADID).isNull()) {
                    performance.setThreadID(rs.getValue(PerformanceTest.ColumnName.THREADID).toLong());
                }
                if (!rs.getValue(PerformanceTest.ColumnName.RESPONSEDATE).isNull()) {
                    performance.setResponseDate(rs.getValue(PerformanceTest.ColumnName.RESPONSEDATE).toLong());
                }
                /*
                if (!rs.getValue(PerformanceTest.ColumnName.RESPONSEDATE).isNull()) {
                    performance.setResponseDate(new Date(rs.getValue(PerformanceTest.ColumnName.RESPONSEDATE).toTimeStamp().getTime()));
                }
                */
                list.add(performance);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PerformanceTestModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }

    public PerformanceTest loadByPrimaryKey(Long rowID) throws SQLException {
        PerformanceTest performance = new PerformanceTest();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open(String.format("SELECT * FROM " + PerformanceTest.TABLE_NAME + " WHERE " + PerformanceTest.ColumnName.ROWID + " = %d", rowID), conn);

            if (rs.hasNext()) {
                if (!rs.getValue(PerformanceTest.ColumnName.ROWID).isNull()) {
                    performance.setRowID(rs.getValue(PerformanceTest.ColumnName.ROWID).toLong());
                }
                if (!rs.getValue(PerformanceTest.ColumnName.ROWID_MESSAGE).isNull()) {
                    performance.setRowID_Message(rs.getValue(PerformanceTest.ColumnName.ROWID_MESSAGE).toLong());
                }
                if (!rs.getValue(PerformanceTest.ColumnName.THREADID).isNull()) {
                    performance.setThreadID(rs.getValue(PerformanceTest.ColumnName.THREADID).toLong());
                }
                if (!rs.getValue(PerformanceTest.ColumnName.RESPONSEDATE).isNull()) {
                    performance.setResponseDate(rs.getValue(PerformanceTest.ColumnName.RESPONSEDATE).toLong());
                }
                /*
                if (!rs.getValue(PerformanceTest.ColumnName.RESPONSEDATE).isNull()) {
                    performance.setResponseDate(new Date(rs.getValue(PerformanceTest.ColumnName.RESPONSEDATE).toTimeStamp().getTime()));
                }
                */
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PerformanceTestModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return performance;
    }
}
