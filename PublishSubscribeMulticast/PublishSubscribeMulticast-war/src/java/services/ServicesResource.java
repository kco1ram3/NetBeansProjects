package services;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Path("services")
public class ServicesResource {

    @Context
    private UriInfo context;

    public ServicesResource() {
    }

    @GET
    @Produces("application/json")
    public String getJson() {
        return "Hello World";
    }

    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
