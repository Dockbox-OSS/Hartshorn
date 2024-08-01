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

package org.dockbox.hartshorn.config.jackson;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import java.io.IOException;

/**
 * A functional interface that writes to a destination that may throw an {@link IOException}, {@link StreamWriteException}
 * or {@link DatabindException}. This is used internally by the {@link org.dockbox.hartshorn.config.ObjectMapper}.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface WriterFunction {

    /**
     * Writes to a destination that may throw an {@link IOException}, {@link StreamWriteException} or {@link DatabindException}.
     *
     * @throws IOException When the destination throws an {@link IOException}
     * @throws StreamWriteException When the destination throws an {@link StreamWriteException}
     * @throws DatabindException When the destination throws an {@link DatabindException}
     */
    void write() throws IOException, StreamWriteException, DatabindException;

}
