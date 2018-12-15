package org.ben.routebuilders;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ben.exceptions.CannotContinueException;
import org.ben.exceptions.DoSomethingElseException;
import org.ben.processors.ErrorGenerator;
import org.ben.processors.FaultGeneratorInMessage;
import org.ben.processors.FaultGeneratorOutMessage;


public class MQRouteBuilderAndErrorHandling extends RouteBuilder {

    static Logger log = LogManager.getRootLogger();

    @Override
    public void configure() {

        //default error handler defined in the other route builder file

        //error handler must come before routes in the route builder

        //I moved this into the FileRouteBuilder file and this default error handling config didn't apply to this route.
        //so it appears the error handler is route builder scoped - not camel context scoped.
        //so this is context scoped - but it does seem to only apply to routes in this route builder


        //to specify a route scoped errorhandler, you define the error handler within the route definition its self - using the errorHandler method of the route definition
        errorHandler(
                defaultErrorHandler()           //by default default error handler doesn't do redelivery, so first error will be propagated back to caller
                        .maximumRedeliveries(3)         //it redelivers to the specific "interceptor" which failed - doesn't start from the beginning. remember the channels connecting "interceptors"
                                //once delivery attempts are exhausted, it propagates error back to caller - so it doesn't "handle" the error

                        .redeliveryDelay(5000)
                        .onRedelivery((Exchange exchange) -> log.info("processor which gets called before camel makes redelivery attempt. I could set headers on exchange to indicate that this is a redelivery for example"))

                        .logHandled(true)       //defaults to false. when false doesn't clog up log file with transient errors (setting this to true, didn't seem to make it log anything however. I didin't looking into why...)
                        .log("default error handler.")   //sets the log used - doesn't log


        );


        //Caused by: java.lang.IllegalArgumentException: onException must be defined before any routes in the RouteBuilder

        //onException Example 1
        //overrides behaviour of default error handler for this exception type, so default error handler still executes
//        onException(CannotContinueException.class)
//                .process((Exchange exchange) -> log.info("error handled. will now continue."))
//                .continued(true);   //error handler doesn't have continued method. but maximumRedeliveries() for example is common to both.
        //the extra process I've added executes, then the default error handler is invoked, then the route continues to the end.


        //onException Example 2
//        onException(CannotContinueException.class)
//                //.maximumRedeliveries(3)
//                //absence of redeliveryDelay doesn't override redeliveryDelay from defaultErrorHandler
//                //but absence of maximumRedeliveries does - i.e. it will set maximumRedeliveries back to zero the default
//                .process((Exchange exchange) -> log.info("error handled. will now continue."));

        //onException Example 3
//        onException(CannotContinueException.class)
//                .handled(true)  //if this isn't set, then you'll see the caller doing its default 3 or 4 redelivers.
//                .process((Exchange exchange) -> log.info("onException for CannotContinueException 1."))
//                .process((Exchange exchange) -> log.info("onException for CannotContinueException 2."));
                //the default error handler still executes, but the error doesn't seem to get propagated back to the caller - i.e. it doesn't try its default redelivery
                //so it's handled in the sense that the error isn't sent back to the caller, but it's a bit surprising to see the default error handler still being involved.
                //but I guess don't forget that the idea of onExceptions is that they override the behaviour of the errorHandler for specific errors - so errorHandler is always involved.
                //so with this, the default error handler handles the exception in that the caller beleives everything was OK
                //but unlike with continue - the route is not progressed further.


        onException(DoSomethingElseException.class)
                //ONWHEN
                //using an onWhen() here you could choose whether to do the following based on a check
                //I'm guessin't if the onWhen doesn't evaluate to true then default error handler will not have anything overridden at all
                //but perhaps I suppose the redelivery config is still blanked out...

                //RETRYWHILE
                //seems to can pass in a bean which has a shouldRetry method which indicates whether to retry.
                //you can probably acheive the same thing with an ONWHEN, which sets maximumRedeliveries to a certain amount
                //and then having another ONWHEN which sets maximumRedeliveries to something else (provided you can have more than 1 ONWHEN, possibly you can't

                .handled(true)  //handled doesn't have to be before subsequent processors but seems to make some sense for readability for it to be first
                .log("onException has caught a DoSomethingElseException")
                .to("file:/C:/Xylem/Test/backupFolder");

                //you could specify a number of retries for this if you wish.
                //so looks like it will handle the exception, will route to the to above instead of progressing with original route
                //then default error handler fires which does it's thing of logging a summary of what happened, that summary includes which processors
                //executed in original route followed by the processors defined in the onException




            //to set stuff on the camel context scope rather than individual route:
//        getContext().setHandleFault(true);



        //dlq error handler overrides default error handler,  when you specify it.
//        errorHandler(
//                //dead letter expends the default error handler
//                deadLetterChannel("jms:queue:dead")
//                        .useOriginalMessage()
//                        .maximumRedeliveries(2)
//                        .redeliveryDelay(1234)
//                        .logExhaustedMessageHistory(true)
//                        .log("dead letter logger")
//                        .useExponentialBackOff()  //another reason to use camel - built in featues such as this
//
//        );


        //starting the route for the first time caused camelsource queue to be creted - not sure if camel did that itself or whether activeMQ auto created the queue
        //destination queues also get created automatically
        from("activemq:queue:camelsource").routeId("routeFromCamelSourceJMS")
                .log("${header.JMSMessageID}")

                .multicast().id("benMulticast") //you can also set ID at the node level as well if you want to
                .to("activemq:queue:cameldest1", "activemq:queue:cameldest2");

        //more than 1 route built in the same route builder

        //if route throws exception then message, by default message is still ack'd
//        from("activemq:queue:xmlSource")
//                //if content isn't XML, then this will throw exception
//                .setHeader("customer", xpath("//order/@customer"))
//                .log("${header.customer}");


        //basic route
                from("activemq:queue:test.queue?acknowledgementModeName=CLIENT_ACKNOWLEDGE")
                .log("${header.JMSMessageID}")
                .process(new ErrorGenerator(2))
                .process((Exchange exchange) -> {
                    log.info("before sleeping.");
                    Thread.sleep(1000);
                    log.info("after sleeping.");
                });



        from("activemq:queue:test.queue2?acknowledgementModeName=CLIENT_ACKNOWLEDGE")
                .doTry()
                    .process((new ErrorGenerator(1)))
                    .process((Exchange exchange) -> log.info("after error generator (shouldn't get to here)."))
                .doCatch(CannotContinueException.class)
                    //default error handler not involved at all. Just gets caught and continues.
                    .process((Exchange exchange) -> log.info("CannotContinueException caught."))
                .end();


        from("activemq:queue:test.queue3?acknowledgementModeName=CLIENT_ACKNOWLEDGE")
                .process((Exchange exchange) -> {
                    throw new DoSomethingElseException();
                })
            .to("activemq:queue:test.cameldest");


        //fault testing 1
        from("activemq:queue:test.queue4?acknowledgementModeName=CLIENT_ACKNOWLEDGE")
                .log("point a")
                .log("point b")
                //either setting the fault on the in message,  or out message causes route to halt as soon as fault is set
                .process(new FaultGeneratorInMessage())
//                .process(new FaultGeneratorOutMessage())
                .log("point c");

        //fault testing 2
        from("activemq:queue:test.queue5?acknowledgementModeName=CLIENT_ACKNOWLEDGE")
                .log("point a")
                .doTry()                //obviously try catches don't react to the fault, since try catch only catches exceptions without handleFault switched on
                    .log("point b")
                    .process(new FaultGeneratorInMessage())
                    .log("point c")
                .doCatch(Exception.class)
                    .log("point d")
                .end();

        //fault testing 3
        from("activemq:queue:test.queue6?acknowledgementModeName=CLIENT_ACKNOWLEDGE").handleFault()
                .log("point a")
                .doTry()
                    .log("point b")
                    .process(new FaultGeneratorInMessage())
                    .log("point c")
                .doCatch(Exception.class)
                    .log("point d")         //handle fault switched on so fault triggers exception by channel and will be caught here
                .end();

        //fault testing 4
        from("activemq:queue:test.queue7?acknowledgementModeName=CLIENT_ACKNOWLEDGE").handleFault()
                .log("point a")
                .log("point b")
                .process(new FaultGeneratorOutMessage()) //and for this one the default error handler will kick in.. or will it?!
                                                         //this seems to behave same as fault testing 1 (for both in / out message faults)
                .log("point c");
    }
}
