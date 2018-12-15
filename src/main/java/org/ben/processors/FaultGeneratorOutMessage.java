package org.ben.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class FaultGeneratorOutMessage implements Processor {

    static Logger log = LogManager.getRootLogger();

    public void process(Exchange exchange) throws Exception {
        exchange.getOut().setFault(true);

    }
}
