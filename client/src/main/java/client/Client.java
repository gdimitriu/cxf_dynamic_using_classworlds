package client;

import application.api.IApplicationMXBean;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Client {
    public static void main(String...args) {
        try {
            JMXServiceURL url =
                    new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName mbeanName = new ObjectName("application.server:type=basic,name=restServer");
            IApplicationMXBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName,
                    IApplicationMXBean.class, true);
            switch (args[0]) {
                case "start":
                    mbeanProxy.start();
                    break;
                case "stop":
                    mbeanProxy.stop();
                    break;
                case "reconfigure":
                    mbeanProxy.reconfigure();
            }
            jmxc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
