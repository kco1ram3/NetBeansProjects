/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author Administrator
 */
@MessageDriven(mappedName = "jms/dest", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "durability", propertyValue = "durable"),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "jms/dest"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "jms/dest")
})
public class MBean implements MessageListener {
    
    public MBean() {
    }
    
    @Override
    public void onMessage(Message message) {
        TextMessage tmsg = null;
        tmsg = (TextMessage) message;
        try {
            System.out.println(tmsg.getText());
            try {
 
		URL url = new URL("http://localhost:8080/MyMdb-war/webresources/generic");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
                conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "text/plain");
                conn.setRequestProperty("param1", "test");
 
		String input = tmsg.getText(); //"{\"qty\":100,\"name\":\"iPad 4\"}";
 
		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();
 
		if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ conn.getResponseCode());
		}
 
		conn.disconnect();
                /*
                conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/plain");
 
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
 
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
 
		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
 
		conn.disconnect();
                        */
	  } catch (MalformedURLException e) {
 
		e.printStackTrace();
 
	  } catch (IOException e) {
 
		e.printStackTrace();
 
	 }
        } catch (JMSException ex) {
            Logger.getLogger(MBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
