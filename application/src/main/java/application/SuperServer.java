package application;

import application.api.IApplication;
import application.launcher.RESTApplication;
import org.codehaus.plexus.classworlds.ClassWorld;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class SuperServer {

    private ClassWorld world;

    private IApplication application;

    public SuperServer(ClassWorld world) {
        this.world = world;
    }
    public static void main(String[] args, ClassWorld world) {
        SuperServer superServer = new SuperServer(world);
        superServer.start();
    }
    public void start() {
        JMXConnectorServer jmxServer = null;
        MBeanServer server = null;
        try {
            LocateRegistry.createRegistry(9999);
            server = ManagementFactory.getPlatformMBeanServer();
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost:10000/jndi/rmi://localhost:9999/jmxrmi");
            jmxServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
            jmxServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        application = new RESTApplication(world, server);
        System.out.println("Press s to stop");
        Scanner in = new Scanner(System.in);
        String str = in.nextLine();
        while(!str.equals("s")) {
            in.nextLine();
        }
        application.stop();
        try {
            if (jmxServer != null)
                jmxServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
