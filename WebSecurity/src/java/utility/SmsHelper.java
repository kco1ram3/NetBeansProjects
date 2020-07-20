package utility;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmsHelper {
    
    public void send(String mobilePhoneNo, String message) {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("socket.properties"));
            Socket s = new Socket(prop.getProperty("ip"), Integer.parseInt(prop.getProperty("port")));
            PrintWriter socketOut = new PrintWriter(s.getOutputStream());
            socketOut.print(mobilePhoneNo + ":" + message);
            socketOut.flush();
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(SmsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
