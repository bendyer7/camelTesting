package org.ben;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.ben.routebuilders.FileRouteBuilder;
import org.junit.Test;

import java.io.File;

/*
Testing routes defined in java DSL
 */
public class TestFileCopier_DSL extends CamelTestSupport {


    //the CamelTestSupport class has a @before annotation which calls the setup code

    @Override
    public void setUp() throws Exception {
        deleteDirectory(FileCopierWithCamel.getSourceFolder());
        deleteDirectory(FileCopierWithCamel.getDestFolder());
        super.setUp();
    }

    @Override
    public RouteBuilder createRouteBuilder() {
        return new FileRouteBuilder();
    }


    @Test
    public void testMoveFile() throws Exception {
        //this will actually create file to folder on disk, i.e. it doesn't set exchange of consumer component in memory or anything
        //(but that probably makes more sense since you want to test that the components endpoint configuration is correct)
        template.sendBodyAndHeader("file://" + FileCopierWithCamel.getSourceFolder(), "Hello World",
                Exchange.FILE_NAME, "hello.txt");

        Thread.sleep(1000);
        String createdFilename = FileCopierWithCamel.getDestFolder() + "/" + FileCopierWithCamel.prefixForCreatedFilename + "hello.txt";
        File target = new File(createdFilename);
        assertTrue("File not moved", target.exists());


        org.apache.activemq.pool.PooledConnectionFactory o = null;
    }


}