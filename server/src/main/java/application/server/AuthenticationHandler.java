package application.server;

import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class AuthenticationHandler implements ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationHandler.class);
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(requestContext.getUriInfo().getPath().equals("openapi.json")) {
            return;
        }
        if(requestContext.getUriInfo().getPath().contains("api-docs")) {
            return;
        }
        String authorization = requestContext.getHeaderString("Authorization");
        if (authorization == null) {
            requestContext.abortWith(createFaultResponse());
            return;
        }
        String[] parts = authorization.split(" ");
        if (parts.length != 2 || !"Basic".equals(parts[0])) {
            requestContext.abortWith(createFaultResponse());
            return;
        }

        String decodedValue = null;
        try {
            decodedValue = new String(Base64Utility.decode(parts[1]));
        } catch (Base64Exception ex) {
            requestContext.abortWith(createFaultResponse());
            return;
        }
        String[] namePassword = decodedValue.split(":");
        if (isAuthenticated(namePassword[0], namePassword[1])) {
            // let request to continue
        } else {
            // authentication failed, request the authetication, add the realm name if needed to the value of WWW-Authenticate
            requestContext.abortWith(Response.status(401).header("WWW-Authenticate", "Basic").build());
        }
    }

    private boolean isAuthenticated(String s, String s1) {
        return s.equals(s1);
    }

    private Response createFaultResponse() {
        return Response.status(401).header("WWW-Authenticate", "Basic realm=\"cxf_classworlds\"").build();
    }
}