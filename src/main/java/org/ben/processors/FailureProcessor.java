package org.ben.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class FailureProcessor implements Processor {
    static Logger log = LogManager.getRootLogger();
    String type;

    public FailureProcessor(String type) {
        this.type = type;
    }
    public void process(Exchange exchange) throws Exception {

        log.info(type + " failure error processor.");

    }
}
