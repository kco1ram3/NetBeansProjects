package entity;

import java.util.Date;

public class Topics {

    public static final String TABLE_NAME = "Topics";

    public class ColumnName {
        public static final String NAME = "Name";
        public static final String IP = "IP";
        public static final String PORT = "Port";
    }

    private String name;
    private String ip;
    private int port;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIP() {
        return this.ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
