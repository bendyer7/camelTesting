package org.ben;


import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.ben.routebuilders.FileRouteBuilder;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.concurrent.TimeUnit;

/*
Testing context definined in spring xml dsl


CamelTestSupport (and parents) provide:
    additional asserts
    util methods such as deleteDirectory()
    ...




 */
public class TestSpringDSLandNotificationBuilder extends CamelSpringTestSupport {         //CamelSpringTestSupport extends CamelTestSupport

    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
                "spring.xml");
    }

    @Override
    public void setUp() throws Exception {
        deleteDirectory(FileCopierWithCamel.getSourceFolder());
        deleteDirectory(FileCopierWithCamel.getDestFolder());
        super.setUp();
    }

    @Test
    public void testMoveFile() throws Exception {

        template.sendBodyAndHeader("file://" + FileCopierWithCamel.getSourceFolder(), "Hello World",
                Exchange.FILE_NAME, "hello.txt");
        Thread.sleep(1000);
        String createdFilename = FileCopierWithCamel.getDestFolder() + "/" + FileCopierWithCamel.prefixForCreatedFilename + "hello.txt";
        File target = new File(createdFilename);
        assertTrue("File not moved", target.exists());

        org.apache.activemq.pool.PooledConnectionFactory o = null;
    }

    @Test
    public void testWithNotificationBuilder() throws Exception {


        NotifyBuilder notify = new NotifyBuilder(context)
                .from(FileRouteBuilder.getFromEndpoint())
//                .whenAnyDoneMatches(body().isEqualTo("Hello World"))
                .whenDone(1)

                .create(); //creates a notification when conditions are met

        template.sendBodyAndHeader("file://" + FileCopierWithCamel.getSourceFolder(), "Hello World",
                Exchange.FILE_NAME, "hello.txt");


//        Thread.sleep(2000);
//        java.util.concurrent.TimeUnit timeunitisajavathing = null;
//
        boolean result = notify.matches(3, TimeUnit.SECONDS);    //wait for the notification, or times out.
        assertTrue(result);

    }
}
