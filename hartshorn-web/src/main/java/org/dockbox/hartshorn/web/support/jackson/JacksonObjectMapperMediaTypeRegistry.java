package org.dockbox.hartshorn.web.support.jackson;

import org.dockbox.hartshorn.util.collections.ConcurrentHashBiMap;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.MediaType;

import java.util.Map;

public class JacksonObjectMapperMediaTypeRegistry {

    private final Map<String, MediaType> mediaTypeCache = new ConcurrentHashBiMap<>();

    public void addMapping(String formatName, MediaType mediaType) {
        this.mediaTypeCache.put(formatName, mediaType);
    }

    public Option<MediaType> getMediaTypeForFormat(String formatName) {
        return Option.of(this.mediaTypeCache.get(formatName));
    }
}
