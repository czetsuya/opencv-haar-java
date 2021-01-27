package com.hacarus.controller;

import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This endpoint is use exclusively for testing.
 *
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @version 1.0.0
 * @since 1.0.0
 */
@Path("/hello")
public class HelloResource {

    @Operation(summary = "Test resource")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }
}