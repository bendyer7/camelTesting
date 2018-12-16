package org.ben.routebuilders;

import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ben.processors.FaultGeneratorInMessage;


public class MQXactedRoutes extends RouteBuilder {
    static Logger log = LogManager.getLogger(RouteBuilder.class);


    @Override
    public void configure() {
        from("activemq:queue:test.queue?acknowledgementModeName=CLIENT_ACKNOWLEDGE")
                .transacted()
                .log("point a")
                .log("point b")
                .log("point c");
    }

}
