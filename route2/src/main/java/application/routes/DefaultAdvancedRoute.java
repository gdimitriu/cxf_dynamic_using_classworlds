package application.routes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Produces("application/text")
@Path("/")
public class DefaultAdvancedRoute {
    @Path("/cxf_classworlds/advanced/info")
    @GET
    public String getInfo() {
        return "server is alive from second advanced route";
    }
}
