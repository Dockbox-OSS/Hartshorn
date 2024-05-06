/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TODO: #1062 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ArgumentSerializationSourceConverter implements SerializationSourceConverter {

    @Override
    public InputStream inputStream(AnnotatedElementView context, Object... args) throws ApplicationException {
        if (args.length != 1) {
            return null;
        }
        Object arg = args[0];
        InputStream inputStream;

        try {
            if (arg instanceof InputStream stream) {
                inputStream = stream;
            }
            else if (arg instanceof String string) {
                inputStream = new ByteArrayInputStream(string.getBytes());
            }
            else if (arg instanceof byte[] bytes) {
                inputStream = new ByteArrayInputStream(bytes);
            }
            else if (arg instanceof Path path) {
                inputStream = Files.newInputStream(path);
            }
            else if (arg instanceof File file) {
                inputStream = new FileInputStream(file);
            }
            else {
                throw new IllegalArgumentException("Expected a valid serialization source, but found " + (arg != null ? arg.getClass() : null));
            }
        }
        catch (IOException e) {
            throw new ApplicationException(e);
        }

        return new BufferedInputStream(inputStream);
    }

    @Override
    public OutputStream outputStream(AnnotatedElementView context, Object... args) throws ApplicationException {
        if (args.length != 1) {
            return null;
        }
        Object arg = args[0];
        OutputStream outputStream;

        try {
            if (arg instanceof OutputStream stream) {
                outputStream = stream;
            }
            else if (arg instanceof Path path) {
                outputStream = Files.newOutputStream(path);
            }
            else if (arg instanceof File file) {
                outputStream = new FileOutputStream(file);
            }
            else {
                return null;
            }
        }
        catch (IOException e) {
            throw new ApplicationException(e);
        }

        return new BufferedOutputStream(outputStream);
    }
}
