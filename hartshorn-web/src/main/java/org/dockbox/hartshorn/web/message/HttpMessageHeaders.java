/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

/**
 * Represents the headers of an HTTP message.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HttpMessageHeaders {

    /**
     * Retrieves the value of the header with the given name.
     *
     * @param name The name of the header.
     *
     * @return An {@link Option} containing the value of the header, or an empty option if the
     * header is not present.
     */
    Option<String> get(String name);

    /**
     * Returns a map representation of the headers.
     *
     * @return A map containing all headers.
     */
    Map<String, String> asMap();
}
