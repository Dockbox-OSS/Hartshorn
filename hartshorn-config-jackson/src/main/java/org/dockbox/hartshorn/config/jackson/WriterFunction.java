package org.dockbox.hartshorn.config.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

public interface WriterFunction {

    void write() throws IOException, StreamWriteException, DatabindException;

}
