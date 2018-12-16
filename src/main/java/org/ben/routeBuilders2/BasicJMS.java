package org.ben.routeBuilders2;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class BasicJMS extends RouteBuilder {
    static Logger log = LogManager.getLogger(RouteBuilder.class);


    @Override
    public void configure() {

        from("activemq:queue:camelsource").routeId("basicJMS1")

                .log("${header.JMSMessageID}")
                .process(new Waiter(500))
                .to("activemq:queue:cameldest1")
                //it waits for message to successfully be written to queue before continuing
                //tried with network delay and it doesn't just fire and forget
                //so it must be waiting for an ack to come back from the server
                .process(new Waiter(30000));
    }



    private static class Waiter implements Processor {

        long sleeptime;

        public Waiter(long sleepTime) {
            this.sleeptime = sleepTime;
        }

        public void process(Exchange exchange) throws Exception {

            log.info("waiter before sleep " + sleeptime);
            Thread.sleep(sleeptime);
            log.info("waiter after sleep " + sleeptime);
        }
    }

}
