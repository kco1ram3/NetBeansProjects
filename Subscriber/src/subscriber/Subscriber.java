package subscriber;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author ZigLost
 */
public class Subscriber {

    private static final String URL_GET_LIST = "http://localhost:8080/PublishSubscribeMulticast-war/webresources/topics";
    private static final String URL_GET_MCAST_GROUP = "http://localhost:8080/PublishSubscribeMulticast-war/webresources/topics/subscribe/";
    
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);

            ArrayList<Topic> topics = getListOfTopic();
            ArrayList<model.Subscriber> subscribers = new ArrayList<model.Subscriber>();

            // List all topic
            if (topics != null) {
                int topicLength = topics.size();
                for (int i = 0; i < topicLength; i++) {
                    System.out.println("Topic " + (i + 1) + ": "
                            + topics.get(i).getTopicName());
                }

                while(true) {
                    System.out.print("Please select topic to subscribe (0 for no need to subscribe anymore): ");
                    int choice = sc.nextInt() - 1;
                    
                    if (choice == -1) {
                        break;
                    }
                    
                    model.Subscriber sub = getMulticastGroup(topics.get(choice).getTopicName());
                    if (sub != null) {
                        subscribers.add(sub);
                    }
                }
                
                /*while (true) {
                    System.out.print("Do you want to subscribe other topic (Y or N): ");
                    String otherTopic = sc.nextLine();
                    
                    if (otherTopic.toLowerCase().equals("y")) {
                        System.out.print("Please select topic to subscribe: ");
                        choice = sc.nextInt() - 1;
                        sub = getMulticastGroup(topics.get(choice).getTopicName());
                        if (sub != null) {
                            subscribers.add(sub);
                            latch1.countDown();
                        }
                    } else {
                        break;
                    }
                }*/
                

                int subSize = subscribers.size();
                if (subSize > 0) {
                    CountDownLatch latch = new CountDownLatch(subSize);
                    for (int i = 0; i < subSize; i++) {
                        Thread t = new Thread(new JoinMcastGroup(subscribers.get(i), latch));
                        t.start();
                        try {
                            t.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        latch.await();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                System.out.println("No topic to be subscribed");
            }
        } catch (IOException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ArrayList<Topic> getListOfTopic() throws IOException {
        ArrayList<Topic> topics = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(URL_GET_LIST);
        HttpResponse response = client.execute(get);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String json = readAllLine(rd);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = new TypeToken<ArrayList<Topic>>() {
        }.getType();
        topics = gson.fromJson(json, type);

        // Close connection
        get.releaseConnection();

        return topics;
    }

    private static String readAllLine(BufferedReader rd) {
        String line = "";
        StringBuilder sb = new StringBuilder();

        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sb.toString();
    }

    private static model.Subscriber getMulticastGroup(String topicName)
            throws ClientProtocolException, IOException {
        model.Subscriber sub = null;

        // Create get request
        String url = URL_GET_MCAST_GROUP + topicName;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);

        // Request the multicast group from Hub
        HttpResponse response = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String json = readAllLine(rd);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        sub = gson.fromJson(json, model.Subscriber.class);

        // Close connection
        get.releaseConnection();

        return sub;
    }
}

class JoinMcastGroup implements Runnable {
    private static final String URL_SEND_RESPONSE_TIME = "http://localhost:8080/PublishSubscribeMulticast-war/webresources/topics/performanceTest";
    
    private static model.Subscriber _sub;
    private static CountDownLatch _latch;

    public static model.Subscriber getSub() {
        return _sub;
    }

    public static void setSub(model.Subscriber aSubs) {
        _sub = aSubs;
    }
    
    public JoinMcastGroup(model.Subscriber sub, CountDownLatch latch) {
        _sub = sub;
        _latch = latch;
    }

    @Override
    public void run() {
        try {
            // Join multicast group
            MulticastSocket socket = new MulticastSocket(_sub.getPort());
            socket.joinGroup(_sub.getSubIP());
            System.out.println("Thread Id: " + Thread.currentThread().getId() 
                    + " join multicast IP: " + _sub.getSubIP()
                    + " on Port: " + _sub.getPort());

            // Receive the msg and print if there is some
            while (true) {
                byte buf[] = new byte[256];
                DatagramPacket pack = new DatagramPacket(buf, buf.length);
                socket.receive(pack);
                sendResponseTime(new String(pack.getData()));

                int msgLength = pack.getLength();

                if (msgLength > 0) {
                    System.out.print("Thread Id: " + Thread.currentThread().getId() 
                            + " receive message -> ");
                    System.out.write(pack.getData(), 0, pack.getLength());
                    System.out.println();
                    pack.setLength(buf.length);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendResponseTime(String msg) 
            throws UnsupportedEncodingException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(URL_SEND_RESPONSE_TIME);
        
        String[] m = msg.split(":");
        PerfomanceInfo p = new PerfomanceInfo();
        if (m.length > 0) {
            p.setMessageId(Long.parseLong(m[0]));
            p.setTimeStamp(new java.util.Date().getTime());
            p.setThreadId(Thread.currentThread().getId());
        }
        
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(p, PerfomanceInfo.class);
        
        // Setting Http header
        StringEntity input = new StringEntity(json);
        input.setContentType("application/json");
        post.addHeader("message", json);
        post.setEntity(input);

        // Put Topic to webservice
        client.execute(post);

        // Close connection
        post.releaseConnection();
    }
}