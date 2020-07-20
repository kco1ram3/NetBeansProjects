package model;

import com.google.gson.annotations.Expose;
import java.net.InetAddress;

public class Subscriber {
    @Expose private InetAddress _subIP;
    @Expose private int _port;
    

    public InetAddress getSubIP() {
        return _subIP;
    }

    public void setSubIP(InetAddress subIP) {
        this._subIP = subIP;
    }

    public int getPort() {
        return _port;
    }

    public void setPort(int port) {
        this._port = port;
    }
}
