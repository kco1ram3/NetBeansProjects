/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.ac.nida.mdb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import th.ac.nida.model.MyMessage;

/**
 *
 * @author puttipong
 */
/*
@MessageDriven(mappedName = "jms/Topic", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "MyMDB"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "MyMDB")
})
*/
@MessageDriven(mappedName = "jms/dest", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "durability", propertyValue = "durable"),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "jms/dest"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "jms/dest")
})
public class MyMDB implements MessageListener {
    
    public MyMDB() {
    }
    
    @Override
    public void onMessage(Message message) {
        
        ObjectMessage objectMessage = (ObjectMessage) message;
        
        try {
            
            MyMessage myMessage = (MyMessage) objectMessage.getObject();
            
            System.out.println("onMessage(): message = " + myMessage.getMessage());
            
        } catch (JMSException ex) {
            Logger.getLogger(MyMDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
