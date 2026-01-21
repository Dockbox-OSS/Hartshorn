package org.dockbox.hartshorn.web;

public interface WebServer {

    void start() throws ServerException;

    void stop() throws ServerException;

    boolean running();

    int port();
}
