package services;

import com.google.gson.Gson;
import entity.*;
import model.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Path("topics")
public class TopicsResource {

    @Context
    private UriInfo context;

    public TopicsResource() {
    }

    @GET
    @Produces("application/json")
    public String getJson() throws SQLException, UnknownHostException {
        List<Topic> topic = new ArrayList<Topic>();
        List<Topics> lists = new TopicsModel().listAll();
        for (Topics list : lists) {
            Topic t = new Topic(list.getName(), "");
            Publisher p = new Publisher();
            p.setPubIP(InetAddress.getByName(list.getIP()));
            t.setPublisher(p);
            topic.add(t);
        }
        Gson gson = new Gson();
        String json = gson.toJson(topic);
        return json;
    }
    
    @GET
    @Path("subscribe/{topicName}")
    @Produces("application/json")
    public String setSubscribe(@PathParam("topicName") String topicName) throws SQLException, UnknownHostException {
        Topics topic = new Topics();
        topic = new TopicsModel().loadByName(topicName);
        
        Subscriber s = new Subscriber();
        s.setSubIP(InetAddress.getByName(topic.getIP()));
        s.setPort(topic.getPort());
        
        Gson gson = new Gson();
        String json = gson.toJson(s);
        return json;
    }
    
    /*
    @GET
    @Path("port")
    @Produces("text/plain")
    public int getPort() throws SQLException, UnknownHostException {
        return new TopicsModel().getPort("172.19.228.209");
    }
    */
    
    /*
    @GET
    @Path("{message}")
    @Produces("application/json")
    public String getJson(@PathParam("message") String message) {
        JSONArray list = new JSONArray();
        JSONObject data = new JSONObject();
        
        data.put("username", "root");
        data.put("password", "admin");
        
        list.add(data);
        
        return list.toJSONString();
    }
    */

    /*
    @POST
    @Path("{message}")
    @Consumes("application/json")
    public String postJson(@PathParam("message") String message) {
        String status = "";
        
        Gson gson = new Gson();
        Topic topicFromJson = gson.fromJson(message, Topic.class);
         
        Topics topic = new Topics();
        topic.setName(topicFromJson.getTopicName());
        topic.setIP(topicFromJson.getPublisher().getPubIP().getHostAddress());
        topic.setPort(PORT);
        try {
            status = "complete";
            new TopicsModel().insert(topic);
        } catch (SQLException ex) {
            status = "error";
            Logger.getLogger(TopicsResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return status;
    }
    */
    
    @POST
    @Consumes("application/json")
    public String postJson(@HeaderParam("message") String message) throws SQLException {
        JSONArray list = new JSONArray();
        JSONObject data = new JSONObject();
        
        Gson gson = new Gson();
        Topic topicFromJson = gson.fromJson(message, Topic.class);
        
        String ip = topicFromJson.getPublisher().getPubIP().getHostAddress();
        int port = new TopicsModel().getPort(ip);
        
        Topics topic = new Topics();
        topic.setName(topicFromJson.getTopicName());
        topic.setIP(ip);
        topic.setPort(port);
        try {
            data.put("status", "complete");
            new TopicsModel().insert(topic);
        } catch (SQLException ex) {
            data.put("status", "error");
            Logger.getLogger(TopicsResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        list.add(data);
        return list.toJSONString();
    }
    
    @POST
    @Path("performanceTest")
    @Consumes("application/json")
    public void postPerformanceTest(@HeaderParam("message") String message) throws SQLException {
        Gson gson = new Gson();
        PerfomanceInfo performanceFromJson = gson.fromJson(message, PerfomanceInfo.class);
        
        PerformanceTest performance = new PerformanceTest();
        performance.setRowID_Message(performanceFromJson.getMessageId());
        performance.setThreadID(performanceFromJson.getThreadId());
        performance.setResponseDate(performanceFromJson.getTimeStamp());
        new PerformanceTestModel().insert(performance);
    }

    @PUT
    @Consumes("application/json")
    public void putJson(@HeaderParam("message") String message) throws SQLException, UnknownHostException, SocketException, IOException {
        Gson gson = new Gson();
        Topic topicFromJson = gson.fromJson(message, Topic.class);
        
        Topics topic = new Topics();
        topic = new TopicsModel().loadByName(topicFromJson.getTopicName());
        
        InetAddress addr = InetAddress.getByName(topic.getIP());
        
        Messages messages = new Messages();
        messages.setTopicName(topicFromJson.getTopicName());
        messages.setMessage(topicFromJson.getMsg());
        messages.setSendDate(topicFromJson.getTimeStamp());
        long rowID_Message = new MessagesModel().insert(messages);
        
        String msg = rowID_Message + ":" + topicFromJson.getMsg();
        
        DatagramSocket serverSocket = new DatagramSocket();
        DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, topic.getPort());
        serverSocket.send(msgPacket);
    }
}
