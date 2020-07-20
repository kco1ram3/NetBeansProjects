/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package timer;

import java.util.Date;
import javax.ejb.Stateless;

/**
 *
 * @author xusuihong
 */
@Stateless
public class Timer {

        public Date getTime() {
        
        Date time = new Date();

        return time;
        }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
