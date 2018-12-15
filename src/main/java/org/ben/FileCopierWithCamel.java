package org.ben;

import org.apache.camel.CamelContext;
import org.apache.camel.util.IOHelper;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

/*
don't forget when running the main class, intelij adds all dependencies to the classpath (spring, MQ client, etc) for you
so intelij must work with maven to work out what dependencies are needed.

 (and don't forget when running tests, intelij and maven does a similar thing to get the necessary test dependencies)

 but if running directly on the command line it's best to use maven to run the main class for you since then maven will
  sort out all dependencies when running on command line.
 */
public class FileCopierWithCamel {

    static String baseFolder = "data";
    public static String prefixForCreatedFilename = "out";
    //executing this causes idea to execute java from the "context" of the project home folder:  D:\javaProjects-wolv\Other\goodThings\CamelTesting\BasicStandalone
    //so data/inbox refers to ${project home folder}/data/inbox

    public static void main(String... args) throws Exception {

        //spring DSL application / camel context:
//        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        //it appears this is enough to start the context
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");


        //pure java DSL camel context
//        CamelContext context = new DefaultCamelContext();
//        context.addRoutes(
//                new FileRouteBuilder()
//        );

        //context.start();
        //regarding the file poller - it seems file polling thread gets created as deamon thread as you'd expect, once the main thread stops - JVM exits.
        waitForKeyboardInput();


        //context.stop();
        IOHelper.close(context);
    }

    public static void waitForKeyboardInput() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            br.readLine();
        } catch (Exception e) {

        }
    }


    public static void addMQComponentToRoute(CamelContext camelContext) {
        camelContext.addComponent("activemq", activeMQComponent("vm://localhost?broker.persistent=false"));
//        org.apache.activemq.pool.PooledConnectionFactory

        //started going down route of trying to create the MQ stuff with DSL, then it seemed easier to do it with
        //spring IOC since I seemed to have an example of creating connection pool etc with spring IOC but not the DSL
    }

    public static String getSourceFolder() {
        return baseFolder + "/infiles";
//        return baseFolder + "/{{file.infolder}}";   //using a property here
//        return baseFolder + "/${file.infolder}";  //the ${} doesn't work here
    }
    public static String getDestFolder() {
        return baseFolder + "/outbox";
    }

}
