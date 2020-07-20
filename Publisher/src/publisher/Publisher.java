package publisher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import model.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class Publisher {

    private static final String WS_URL = "http://localhost:8080/PublishSubscribeMulticast-war/webresources/topics";

    public enum HttpMethod {
        post,
        put
    }

    public static void main(String[] args) 
            throws ClientProtocolException, IOException, InterruptedException {
        String topicName = "", msg = "";
        Scanner sc = new Scanner(System.in);

        System.out.print("Please input topic Name: ");
        topicName = sc.nextLine();
        registerTopic(topicName);

        while (true) {
            System.out.print("Please input message (0 for exit): ");
            msg = sc.nextLine();
            if(msg.equals("0")) break;
            /*
            for(int i = 0; i < 20; i++) {
                Thread.sleep(1000);
                sendMsg(topicName, msg);
            }
            */
            sendMsg(topicName, msg);
        }
    }

    private static HttpEntityEnclosingRequestBase getHttpMethod(HttpMethod method) {
        HttpEntityEnclosingRequestBase httpMethod = null;

        switch (method) {
            case post:
                httpMethod = new HttpPost(WS_URL);
                break;
            case put:
                httpMethod = new HttpPut(WS_URL);
                break;
        }

        return httpMethod;
    }

    private static void registerTopic(String topicName)
            throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = (HttpPost) getHttpMethod(HttpMethod.post);

        // Create Topic to be send
        Topic t = new Topic(topicName, "");
        model.Publisher p = new model.Publisher();
        // Generate random multicast address
        Random r = new Random();
        String ip = "224." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
        //p.setPubIP(InetAddress.getLocalHost());
        p.setPubIP(InetAddress.getByName(ip));
        t.setPublisher(p);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(t, Topic.class);

        // Setting Http header
        StringEntity input = new StringEntity(json);
        input.setContentType("application/json");
        post.addHeader("message", json);
        post.setEntity(input);

        // Post Topic to webservice
        client.execute(post);

        // Close connection
        post.releaseConnection();
    }

    private static void sendMsg(String topicName, String msg)
            throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPut put = (HttpPut) getHttpMethod(HttpMethod.put);

        // Create Topic to be send
        Topic t = new Topic(topicName, msg);
        model.Publisher p = new model.Publisher();
        p.setPubIP(InetAddress.getLocalHost());
        t.setPublisher(p);
        t.setTimeStamp(new Date().getTime());

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(t, Topic.class);

        // Setting Http header
        StringEntity input = new StringEntity(json);
        input.setContentType("application/json");
        put.addHeader("message", json);
        put.setEntity(input);

        // Put Topic to webservice
        client.execute(put);

        // Close connection
        put.releaseConnection();
    }
}
