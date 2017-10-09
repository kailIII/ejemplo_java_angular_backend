package com.example.rest.services;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author ruber
 */
@ApplicationPath("api")
public class AppConfig extends ResourceConfig {

    public AppConfig() {
        packages("com.example.rest.services");
         register(MultiPartFeature.class);
    }

}

