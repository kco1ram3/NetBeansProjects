/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaappclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import nida.HelloSessionBeanRemote;

/**
 *
 * @author Seksit
 */
public class JavaAppClient {

    static final String PROPERTIES_FILENAME = "jndi.properties";
    static final String SESSION_REMOTE_INTERFACE = "nida.HelloSessionBeanRemote";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            // Load jndi.properties (Properties for remote from Client)
            Properties prop = new Properties();
            prop.load(new FileInputStream(PROPERTIES_FILENAME));
            // New Context from Properties
            InitialContext context = new InitialContext(prop);
            // Cast context lookup to HelloSessionRemote
            HelloSessionBeanRemote hello = (HelloSessionBeanRemote) context.lookup(SESSION_REMOTE_INTERFACE);
            
            System.out.println(hello.sayHello("Seksit Yodpetch!!!"));
            
        } catch (NamingException nex) {
            nex.printStackTrace();
        } catch (FileNotFoundException fnfex) {
            fnfex.printStackTrace();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }
    
}
