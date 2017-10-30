package com.sully90.server;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

public abstract class Service {

    @Context
    ServletContext servletContext;

}
