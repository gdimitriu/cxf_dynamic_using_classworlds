package application.server;

import application.api.IApplication;
import application.common.CustomClassLoader;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.jaxrs.swagger.ui.SwaggerUiConfig;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

public class RestServer implements IApplication {

    private OpenApiFeature feature;
    private JAXRSServerFactoryBean restServer;
    private CustomClassLoader customClassLoader;
    private Server server;
    private Bus bus;

    public RestServer(CustomClassLoader customClassLoader) {
        this.customClassLoader = customClassLoader;
        Thread.currentThread().setContextClassLoader(customClassLoader);
    }

    @Override
    public void start() {
        Thread.currentThread().setContextClassLoader(customClassLoader);
        restServer = new JAXRSServerFactoryBean();
        bus = CXFBusFactory.getThreadDefaultBus();
        bus.setExtension(customClassLoader, ClassLoader.class);
        if (restServer.getClass().getClassLoader() != customClassLoader) {
            System.out.println(restServer.getClass().getClassLoader());
            System.out.println(Thread.currentThread().getContextClassLoader());
            System.out.println("Different class loaders");
        }

        restServer.setBus(bus);
        feature = new OpenApiFeature();
        feature.setSwaggerUiConfig(
                new SwaggerUiConfig()
                        .url("/openapi.json"));
        restServer.setProvider(new JacksonJaxbJsonProvider());
        restServer.setProvider(new AuthenticationHandler());
        InputStream propIS = customClassLoader.getResourceAsStream("routes.properties");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(propIS))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                restServer.setServiceBean(customClassLoader.loadClass(line).getConstructor().newInstance());
            }
        } catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
            System.out.println("Could not read/load route classes " + e.getMessage());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("Could not instantiate class");
        }
        restServer.getFeatures().add(feature);
        String hostname = "localhost";
        try {
            hostname = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
        }
        restServer.setAddress("http://" + hostname + ":" + 8080);
        server = restServer.create();
    }
    @Override
    public void reconfigure() {
        stop();
        start();
    }

    @Override
    public void stop() {
        server.stop();
        server.destroy();
        if (bus != null) {
            bus.shutdown(true);
            bus = null;
        }
        feature = null;
    }
}
