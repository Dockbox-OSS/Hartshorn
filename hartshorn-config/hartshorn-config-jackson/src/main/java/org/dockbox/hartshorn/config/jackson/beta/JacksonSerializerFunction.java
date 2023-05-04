package org.dockbox.hartshorn.config.jackson.beta;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.config.beta.SerializerFunction;
import org.dockbox.hartshorn.config.jackson.JacksonDataMapper;
import org.dockbox.hartshorn.config.jackson.JacksonObjectMapperConfigurator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

public class JacksonSerializerFunction implements SerializerFunction {

    private final Object object;
    private final FileFormat format;
    private final ObjectMapper mapper;

    public JacksonSerializerFunction(final JacksonObjectMapperConfigurator configurator,
                                     final JacksonDataMapper dataMapper,
                                     final Object object, final FileFormat format) {
        this.object = object;
        this.format = format;
        this.mapper = this.createObjectMapper(dataMapper, configurator);
    }

    private ObjectMapper createObjectMapper(final JacksonDataMapper dataMapper,
                                            final JacksonObjectMapperConfigurator configurator) {
        return configurator
                .configure(dataMapper.get(), this.format)
                .build();
    }

    @Override
    public String asString() throws ObjectMappingException {
        try {
            return this.mapper.writeValueAsString(this.object);
        }
        catch (final JsonProcessingException e) {
            throw new ObjectMappingException(e);
        }
    }

    @Override
    public void to(final Path path) throws ObjectMappingException {
        this.to(path.toUri());
    }

    @Override
    public void to(final URI uri) throws ObjectMappingException {
        try {
            this.to(uri.toURL());
        }
        catch (final MalformedURLException e) {
            throw new ObjectMappingException(e);
        }
    }

    @Override
    public void to(final URL url) throws ObjectMappingException {
        try {
            final OutputStream stream = url.openConnection().getOutputStream();
            this.to(stream);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void to(final OutputStream stream) throws ObjectMappingException {
        try {
            this.mapper.writeValue(stream, this.object);
        }
        catch (final IOException e) {
            throw new ObjectMappingException(e);
        }
    }

    @Override
    public FileFormat format() {
        return this.format;
    }
}
