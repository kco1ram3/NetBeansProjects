package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import persist.Customer;
import persist.CustomerFacadeLocal;

public class CustomerInfoServlet extends HttpServlet {
    
    @EJB
    private CustomerFacadeLocal customerFacade;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        long id;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException ex) {
            id = 0;
        }
        String flag = request.getParameter("flag") != null ? request.getParameter("flag") : "";
        if (flag.equals("delete")) {
            Customer customer = new Customer(id);
            customerFacade.remove(customer);
            
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                out.println("<script type=\"text/javascript\">");  
                out.println("alert('delete complete');");  
                out.println("window.location.href='" + request.getContextPath() + "/CustomerList';");
                out.println("</script>"); 
            } finally {            
                out.close();
            }
        } else {
            request.setAttribute("id", id);
            request.setAttribute("customer", customerFacade.find(id));
            RequestDispatcher view = request.getRequestDispatcher("customerInfo.jsp");
            view.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        long id = Long.parseLong(request.getParameter("id"));
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        
        Customer customer = new Customer(id);
        customer.setName(name);
        customer.setSurname(surname);
        customer.setEmail(email);
        customer.setPhone(phone);
        
        String message = "";
        if (id > 0) {
            customer.setCreateDate(customerFacade.find(id).getCreateDate());
            customer.setUpdateDate(new Date());
            customerFacade.edit(customer);
            message = "update complete";
        } else {
            customer.setCreateDate(new Date());
            customerFacade.create(customer);
            message = "create complete";
        }
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<script type=\"text/javascript\">");  
            out.println("alert('" + message + "');");  
            out.println("window.location.href='" + request.getContextPath() + "/CustomerList';");
            out.println("</script>"); 
        } finally {            
            out.close();
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
