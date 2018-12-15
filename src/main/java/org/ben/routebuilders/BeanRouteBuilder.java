package org.ben.routebuilders;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.ben.beans.BeanWithOneMethod;
import org.ben.beans.BeanWithXPathAnnotations;

public class BeanRouteBuilder extends RouteBuilder {

    public void configure() {
        from("timer:bentimer?period=30s")
                .process((Exchange exchange) -> exchange.getIn().setBody("12345"))

                //.convertBodyTo(Integer.class)

                // if you want to explicitly convert to a particular type
                //perhaps the bean method had more than one method which could accept more than 1 type
                //and you wanted to force it down a particular way

                //or an upstream processor might examine the type of the message body, and do different things depending on what type it is.
                //i think for example some of the JMS components may send either text messages or binary to the JMS queue depending on the
                //type of the message body
                .to("direct:beanTesting");

        from("direct:beanTesting")
                .log("beanTesting start")
                //use beanRef() to reference a bean already created in the spring context
                .bean(BeanWithOneMethod.class);     //bean() method will create bean on startup. Since this class only has 1 method, it'll just call that method

                //You should regard the Camel Bean component as the Camel implementation of the Service Activator pattern.
                //the theory behind how Camel works with beans is the Service Activator pattern
                //input is adapted for the bean,  but is not adapted / not translated on the way back.

                //BEAN SELECTION ALGORITHM

                //BEAN PARAMETER BINDING




        //to show using XPath to bind XML content to bean parameters
        from("timer:bentimer2?period=30s")
                .process(
                        (Exchange exchange) -> {
                            exchange.getOut().setBody("<a><Customer><Firstname>Phil</Firstname><Lastname>Smith</Lastname></Customer></a>");
                        }
                )
                .bean(BeanWithXPathAnnotations.class);
    }

}
