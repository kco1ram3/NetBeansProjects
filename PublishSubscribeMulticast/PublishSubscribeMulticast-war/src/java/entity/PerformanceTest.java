package entity;

import java.util.Date;

public class PerformanceTest {

    public static final String TABLE_NAME = "PerformanceTest";

    public class ColumnName {
        public static final String ROWID = "RowID";
        public static final String ROWID_MESSAGE = "RowID_Message";
        public static final String THREADID = "ThreadID";
        public static final String RESPONSEDATE = "ResponseDate";
    }

    private long rowID;
    private long rowID_Message;
    private long threadID;
    private long responseDate;

    public long getRowID() {
        return this.rowID;
    }

    public void setRowID(long rowID) {
        this.rowID = rowID;
    }
    
    public long getRowID_Message() {
        return this.rowID_Message;
    }

    public void setRowID_Message(long rowID_Message) {
        this.rowID_Message = rowID_Message;
    }

    public long getThreadID() {
        return this.threadID;
    }

    public void setThreadID(long threadID) {
        this.threadID = threadID;
    }
    
    public long getResponseDate() {
        return this.responseDate;
    }

    public void setResponseDate(long responseDate) {
        this.responseDate = responseDate;
    }
}
