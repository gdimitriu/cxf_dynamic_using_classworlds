package application.launcher;

import application.api.IApplication;
import application.api.IApplicationMXBean;
import application.common.CustomClassLoader;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.launcher.Launcher;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;

import javax.management.*;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;

public class RESTApplication implements IApplicationMXBean, IApplication {

    private ClassWorld world;

    private IApplication application;

    private  ClassLoader origClassloader;

    public RESTApplication(ClassWorld world, MBeanServer jmxServer) {
        this.world = world;
        origClassloader = Thread.currentThread().getContextClassLoader();
        try {
            ObjectName objectName = new ObjectName("application.server:type=basic,name=restServer");
            jmxServer.registerMBean(this, objectName);
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        Thread.currentThread().setContextClassLoader(origClassloader);
        URL url = origClassloader.getResource ("classworlds.conf");

        Launcher launcher = new Launcher();

        try (InputStream is = url.openStream()){
            launcher.configure(is);
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }

        ClassRealm realm = null;
        try {
            realm = launcher.getWorld().getRealm("cxf");
        } catch (NoSuchRealmException e) {
            e.printStackTrace();
            return;
        }
        ClassRealm realmApplication = null;
        try {
            realmApplication = launcher.getWorld().getRealm("application");
        } catch (NoSuchRealmException e) {
            e.printStackTrace();
            return;
        }
        realm.setParentClassLoader(realmApplication);
        CustomClassLoader classLoader = new CustomClassLoader();
        classLoader.addClassLoader(realm);
        classLoader.addClassLoader(origClassloader);
        classLoader.addClassLoader(realmApplication);
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            Class<?> applicationClass = classLoader.loadClass("application.server.RestServer");
            if (applicationClass == null) {
                Arrays.stream(realm.getURLs()).forEach(System.out::println);
                System.out.println("Could not load RestServer");
                System.exit(-1);
            }
            application = (IApplication) applicationClass
                    .getDeclaredConstructor(CustomClassLoader.class)
                    .newInstance(classLoader);
            application.start();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Thread.currentThread().setContextClassLoader(origClassloader);
            return;
        }
    }

    @Override
    public void reconfigure() {
        stop();
        start();
    }

    @Override
    public void stop() {
        if (application != null){
            application.stop();
        }
        Thread.currentThread().setContextClassLoader(origClassloader);
    }
}
