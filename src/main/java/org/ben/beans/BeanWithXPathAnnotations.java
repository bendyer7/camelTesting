package org.ben.beans;


import org.apache.camel.language.XPath;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BeanWithXPathAnnotations {
    static Logger log = LogManager.getLogger(BeanWithXPathAnnotations.class);

    public void receiveXMLFields(@XPath("//Customer/Firstname") String firstname,
                                 @XPath("//Customer/Lastname") String lastname) {
        log.info("firstname: " + firstname + ". lastname: " + lastname);
    }
}
