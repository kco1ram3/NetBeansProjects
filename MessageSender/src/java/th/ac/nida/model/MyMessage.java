/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.ac.nida.model;

import java.io.Serializable;

/**
 *
 * @author puttipong
 */
public class MyMessage implements Serializable {
    
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
