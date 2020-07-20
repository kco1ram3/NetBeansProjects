package model;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author L.Ratchata
 */
public class Topic implements Serializable {
    @Expose private String _topicName;
    @Expose private String _msg;
    @Expose private Publisher _publisher;
    @Expose private long _timeStamp;
    
    private ArrayList<Subscriber> _subscriber;
    
    public Topic(String topicName, String msg) {
        _topicName = topicName;
        _msg = msg;
    }

    public String getTopicName() {
        return _topicName;
    }

    public void setTopicName(String topicName) {
        this._topicName = topicName;
    }

    public String getMsg() {
        return _msg;
    }

    public void setMsg(String msg) {
        this._msg = msg;
    }

    public Publisher getPublisher() {
        return _publisher;
    }

    public void setPublisher(Publisher publisher) {
        this._publisher = publisher;
    }

    public ArrayList<Subscriber> getSubscriber() {
        return _subscriber;
    }

    public void setSubscriber(ArrayList<Subscriber> subscriber) {
        this._subscriber = subscriber;
    }
    
    public long getTimeStamp() {
        return _timeStamp;
    }
    
    public void setTimeStamp(long _TimeStam) {
        this._timeStamp = _TimeStam;
    }
}