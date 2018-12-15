package org.ben.beans;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BeanWithOneMethod {
    static Logger log = LogManager.getRootLogger();

    //by default, the inMessage body is bound into the single parameter
    //and the body is assumed to be convertible to the type of the parmeter (in this example I'm setting the body to the string "12345"
    //and will be converted to that type using the built in Camel type conveters
    //(if it can't be converted to the type the type converter will blow up)
    public void doSomething(int inMessageBody) {
        log.info("BeanWithOneMethod. " + inMessageBody);
    }
}
