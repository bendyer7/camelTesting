package org.ben.routebuilders;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.ben.FileCopierWithCamel;


public class FileRouteBuilder extends RouteBuilder {

    public static String getFromEndpoint() {
        return "file:"+ FileCopierWithCamel.getSourceFolder() + "?noop=true";
    }
    public void configure() {



        //camel automatically creates the src folder if it doesn't exist
        from(getFromEndpoint())   //FYI, docs say noop=true causes files to not be deleted once moved. yet the process remains idempotent - i.e. same file doesn't get processed twice
                .log("${header.CamelFileName}")
                .log("header.CamelFileName")    //not sure how I could log literally "header.CamelFileName" but you'd probably never actually need to do  this

                        //probably bad practice to change headers, but this causes created filename to differ.
                .setHeader(Exchange.FILE_NAME).simple(FileCopierWithCamel.prefixForCreatedFilename + "${header.CamelFileName}")
                .log("header.CamelFileName")

                        //see default config. things such as if the file exists then it replaces it etc..
                .to("file:" + FileCopierWithCamel.getDestFolder());
    }
}
