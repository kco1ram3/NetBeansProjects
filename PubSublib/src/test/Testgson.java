package test;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.*;

public class Testgson {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        //Type topicType = new TypeToken<Topic<Publisher>>(){}.getType(); 
        
        Publisher pub = new Publisher();
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Testgson.class.getName()).log(Level.SEVERE, null, ex);
        }
        pub.setPubIP(addr);
        
        //Topic<Publisher> t = new Topic<Publisher>("test", "Publisher", pub);
        //String json = gson.toJson(t, topicType);
        
        //Topic<Publisher> j = gson.fromJson(json, topicType);
    }
}
