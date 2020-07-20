/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.ac.nida.message;

import java.io.Serializable;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import th.ac.nida.model.MyMessage;

/**
 *
 * @author puttipong
 */
public class MessageSender implements Serializable {
    
    @Resource(mappedName = "jms/Topic")
    private Topic topic;
    @Resource(mappedName = "jms/TopicFactory")
    private ConnectionFactory topicFactory;
    
    public void send(MyMessage myMessage) throws JMSException {
        
        Connection connection = null;
        Session session = null;
        
        try {
            // create connection
            connection = topicFactory.createConnection();
            
            // create session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // create producer
            MessageProducer messageProducer = session.createProducer(topic);
            
            // create object message
            ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setObject(myMessage);
            
            // send
            messageProducer.send(objectMessage);
            
        } finally {
            if (session != null) {
                try { session.close(); } catch (JMSException e) {}
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
    
}
