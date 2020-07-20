package entity;

import java.util.Date;

public class Messages {

    public static final String TABLE_NAME = "Message";

    public class ColumnName {
        public static final String ROWID = "RowID";
        public static final String TOPICNAME = "TopicName";
        public static final String MESSAGE = "Message";
        public static final String SENDDATE = "SendDate";
    }

    private long rowID;
    private String topicName;
    private String message;
    private long sendDate;

    public long getRowID() {
        return this.rowID;
    }

    public void setRowID(long rowID) {
        this.rowID = rowID;
    }

    public String getTopicName() {
        return this.topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public long getSendDate() {
        return this.sendDate;
    }

    public void setSendDate(long sendDate) {
        this.sendDate = sendDate;
    }
}
