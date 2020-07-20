/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.ac.nida.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import th.ac.nida.model.MyMessage;

/**
 *
 * @author puttipong
 */
public class PostMessage extends HttpServlet {
    
    /*
    @Resource(mappedName = "jms/Topic")
    private Topic topic;
    @Resource(mappedName = "jms/TopicFactory")
    private ConnectionFactory topicFactory;
    */
    @Resource(mappedName = "jms/dest")
    private Topic topic;
    @Resource(mappedName = "jms/queue")
    private ConnectionFactory topicFactory;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // process request
        String message = request.getParameter("message");
        
        MyMessage myMessage = new MyMessage();
        myMessage.setMessage(message);
        
        // send to jms
        try {
            send(myMessage);
        } catch (JMSException ex) {
            Logger.getLogger(PostMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // process response
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet PostMessage</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Sent '" + myMessage.getMessage() + "' to queue complete</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }
    
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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
