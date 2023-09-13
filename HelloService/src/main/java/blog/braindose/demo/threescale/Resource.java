package blog.braindose.demo.threescale;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.jboss.resteasy.reactive.RestHeader;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/hello")
public class Resource {

    private static Logger LOGGER = LoggerFactory.getLogger(Resource.class);
    
    @Path("/get/{name}")
    @GET
    public Response hello(String name) {
        LOGGER.info("Hello {}", name);
        return Response.created(null).entity("Hello " + name).build();
    }

    @Path("/get")
    @GET
    public Response helloHeader(@RestHeader("MY-HEADER") String myHeader) {
        LOGGER.info("Hello {}", myHeader);
        return Response.created(null).entity("Hello " + myHeader).build();
    }
}
