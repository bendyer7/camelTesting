package org.ben;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class TestGenerateErrors extends CamelSpringTestSupport {

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
    public void generateErrorsWithMock() {

        MockEndpoint dest1Before = getMockEndpoint("mock:activemq:queue:cameldest1");
        dest1Before.whenAnyExchangeReceived((Exchange exchange) -> {throw new IOException();});
        template.sendBody("activemq:queue:camelsource", "Hello World");

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void generateErrorsWithInterceptors() throws Exception {

        RouteDefinition routeDefinition = context.getRouteDefinition("routeFromCamelSourceJMS");


        //adds extra items to the route, normally only used for testing so that we can modify behaviour of route
        //without "touching" it.
        //if something is always needed on a route then normally you would just add it to the route directly.
        routeDefinition.adviceWith(context, new AddIntercetpr());

        template.sendBody("activemq:queue:camelsource", "Hello World");

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static class AddIntercetpr extends RouteBuilder {
        static Logger log = LogManager.getLogger(AddIntercetpr.class);

        public void configure() throws Exception {

            //there is also a send FROM endpoint, so this will not intercept things sent to the queue receiver
            interceptSendToEndpoint("activemq:*")
                    .skipSendToOriginalEndpoint()
                    .process((Exchange exchange) -> {
                        log.info("interceptSendToEndpoint (for activemq:*). throwing Exception ");
                        throw new IOException();
                    });
        }
    }



}
