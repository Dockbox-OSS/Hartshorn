package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.application.context.ApplicationContext;

import java.util.Map;

import jakarta.servlet.Servlet;

public interface WebContextLoader {

    Map<String, Servlet> loadServlets(ApplicationContext applicationContext, HttpWebServer webServer);

    void initializeContext(ApplicationContext applicationContext, HttpWebServer webServer);

}
