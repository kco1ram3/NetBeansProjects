package model;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.Date;

public class PerfomanceInfo implements Serializable {
    @Expose private long _timeStamp;
    @Expose private long _threadId;
    @Expose private long _messageId;

    public long getTimeStamp() {
        return _timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this._timeStamp = timeStamp;
    }

    public long getThreadId() {
        return _threadId;
    }

    public void setThreadId(long threadId) {
        this._threadId = threadId;
    }

    public long getMessageId() {
        return _messageId;
    }

    public void setMessageId(long messageId) {
        this._messageId = messageId;
    }
}
