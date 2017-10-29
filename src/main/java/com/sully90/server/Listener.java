package com.sully90.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author Crunchify.com
 */

@SuppressWarnings("serial")
public class Listener extends HttpServlet
{

    public void init() throws ServletException
    {
        System.out.println("----------");
        System.out.println("---------- Servlet Initialized successfully ----------");
        System.out.println("----------");

        // Initialise the DeprecatedSearchEngine on App launch
        System.out.println("----------");
        System.out.println("---------- Initializing SearchEngine ----------");
        System.out.println("----------");
//        SearchEngineService.init();

        System.out.println("----------");
        System.out.println("---------- Done! ----------");
        System.out.println("----------");
    }
}
