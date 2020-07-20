package packt;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
/*
@MessageDriven(mappedName = "jms/AvailabilityTopic", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "durability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "SubscriberBean"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "SubscriberBean")
    })
*/
@MessageDriven(mappedName = "jms/dest", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "durability", propertyValue = "durable"),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "jms/dest"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "jms/dest")
})
public class SubscriberBean implements MessageListener {
    
    public SubscriberBean() {
    }

    public void onMessage(Message message) {
        System.out.println("---> subscriber ");
    }
    
}
