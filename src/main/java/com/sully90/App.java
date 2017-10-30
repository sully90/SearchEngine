package com.sully90;

import com.sully90.server.SearchEngineService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.TracingConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

public class App extends ResourceConfig {

    public App() {
        // Resources.
        SearchEngineService.init();
        packages(SearchEngineService.class.getPackage().getName());

        // MVC.
        register(JspMvcFeature.class);
        property(JspMvcFeature.TEMPLATES_BASE_PATH, "/WEB-INF/jsp");

        // Logging.
//        register(LoggingFeature.class);

        // Tracing support.
        property(ServerProperties.TRACING, TracingConfig.ON_DEMAND.name());
    }
}
