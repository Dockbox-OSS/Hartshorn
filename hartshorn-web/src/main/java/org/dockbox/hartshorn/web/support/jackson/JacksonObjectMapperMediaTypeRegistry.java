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

package org.dockbox.hartshorn.web.support.jackson;

import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.MediaType;

import java.util.Map;
import java.util.TreeMap;

/**
 * A registry for mapping format names of Jackson
 * {@link tools.jackson.core.TokenStreamFactory token stream factories} to {@link MediaType}
 * instances.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JacksonObjectMapperMediaTypeRegistry {

    private final Map<String, MediaType> mediaTypeCache = new TreeMap<>(
            String.CASE_INSENSITIVE_ORDER
    );

    /**
     * Adds a mapping from a format name to a {@link MediaType}. Format names should match the
     * names used by Jackson's {@link tools.jackson.core.TokenStreamFactory} implementations.
     *
     * @param formatName the format name to map
     * @param mediaType the media type to map to the format name
     */
    public void addMapping(String formatName, MediaType mediaType) {
        this.mediaTypeCache.put(formatName, mediaType);
    }

    /**
     * Attempts to retrieve a {@link MediaType} for the given format name. If no mapping exists, an
     * empty {@link Option} is returned.
     *
     * @param formatName the format name to retrieve the media type for
     * @return an {@link Option} containing the media type if it exists, or an empty {@link Option}
     * if it does not
     */
    public Option<MediaType> getMediaTypeForFormat(String formatName) {
        return Option.of(this.mediaTypeCache.get(formatName));
    }
}
