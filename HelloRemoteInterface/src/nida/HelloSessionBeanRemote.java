/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nida;

import javax.ejb.Remote;

/**
 *
 * @author Seksit
 */
@Remote
public interface HelloSessionBeanRemote {

    String sayHello(String name);
    
}
