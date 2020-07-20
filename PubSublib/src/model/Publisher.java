/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.google.gson.annotations.Expose;
import java.net.InetAddress;

/**
 *
 * @author L.Ratchata
 */
public class Publisher {
    @Expose
    private InetAddress _pubIP;

    public InetAddress getPubIP() {
        return _pubIP;
    }

    public void setPubIP(InetAddress pubIP) {
        this._pubIP = pubIP;
    }
}
