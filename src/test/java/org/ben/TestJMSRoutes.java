package org.ben;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestJMSRoutes extends CamelSpringTestSupport {

    protected AbstractXmlApplicationContext createApplicationContext() {

        //notice that we're using the same application context for testing as what gets used properly.
        //so among other things, templates can use components defined in the context for producing
        return new ClassPathXmlApplicationContext(
                "spring.xml");
    }


    @Override
    public String isMockEndpointsAndSkip() {
        //seems you just return some regex to match the endpoint uri, and components for all matching endpoints are swapped with a mock rather than the
        //real component
        return "activemq:queue:cameldest1|activemq:queue:cameldest2";   //just regex
    }

    @Test
    public void testJMSRoutes() {

        MockEndpoint dest1Before = getMockEndpoint("mock:activemq:queue:cameldest1");
        MockEndpoint dest2Before = getMockEndpoint("mock:activemq:queue:cameldest2");

        dest1Before.setExpectedCount(1);
        dest2Before.setExpectedCount(1);

        template.sendBody("activemq:queue:camelsource", "Hello World");

        try {
            dest1Before.assertIsSatisfied();
            dest2Before.assertIsSatisfied();
        } catch (Exception e) {

        }



        //junit also throws this core java error
        java.lang.AssertionError e;
    }

}
