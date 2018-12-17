package org.ben;

import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/*
Testing routes defined in java DSL
 */
public class TestBasicsOfTemplate extends CamelTestSupport {


    //the CamelTestSupport class has a @before annotation which calls the setup code




    @Test
    public void testMoveFile() throws Exception {

        //so we're just using one of the components to send to a destnation. the file component can be used to produce or consume.
        //use the component to send data to where ever you want
        //I didn't bother loading the route, but the route could have been running and comsuming from a different location, using the same component (file)
        template.sendBodyAndHeader("file://Xylem/Test", "Hello World",
                Exchange.FILE_NAME, "hello.txt");


        String out = template.requestBody("", null, String.class);

    }


}