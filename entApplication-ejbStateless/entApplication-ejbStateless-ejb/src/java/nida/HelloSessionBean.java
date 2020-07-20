/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nida;

import javax.ejb.Stateless;

/**
 *
 * @author Seksit
 */
@Stateless
public class HelloSessionBean implements HelloSessionBeanRemote {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public String sayHello(String name) {
        return "Application EJB..." + name;
    }
}
