package org.dockbox.hartshorn.web.message;

public interface ResponseWriter<T> {

    void write(WebResponse response, T body) throws Exception;
}
