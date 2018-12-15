package org.ben.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ben.exceptions.CannotContinueException;

public class ErrorGenerator implements Processor {

    static Logger log = LogManager.getRootLogger();
    int maxNumOfErrors = 0;
    int errorCount = 0;

    public ErrorGenerator(int numOfErrors) {
        this.maxNumOfErrors = numOfErrors;
    }


    public void process(Exchange exchange) throws CannotContinueException {

        if (errorCount < maxNumOfErrors) {
            errorCount++;
            log.info("error generator.  about to throw exception.");

//            throw new Exception("something went wrong.");
            throw new CannotContinueException("something went wrong.");

        } else {
            log.info("error generator.  not throwing exception.");
        }

    }
}
