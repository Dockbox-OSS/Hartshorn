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

package org.dockbox.hartshorn.config.properties;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.ConfigurationURIContext;
import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.util.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardURIConfigProcessor implements URIConfigProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(StandardURIConfigProcessor.class);

    @Override
    public void process(ApplicationContext context, Set<ConfigurationURIContext> contexts) {
        for (ConfigurationURIContext uriContext : contexts) {
            URI uri = uriContext.uri();
            String source = uriContext.source();

            FileFormat format = this.lookupFileFormat(uri, source);

            if (format == null) {
                LOG.error("Unknown file format: " + source + ", declared by " + uriContext.key().type().getSimpleName());
                return;
            }

            try {
                Map<String, Object> cache = context.get(ObjectMapper.class)
                        .fileType(format)
                        .read(uri, new GenericType<Map<String, Object>>() {})
                        .orElseGet(HashMap::new);

                LOG.debug("Located " + cache.size() + " properties in " + uri.getPath());
                context.get(PropertyHolder.class).set(cache);
            }
            catch (ObjectMappingException e) {
                context.handle("Failed to read properties from " + uri.getPath(), e);
            }
        }
    }

    protected FileFormat lookupFileFormat(URI uri, String source) {
        // If the source has a file extension, use that
        FileFormat lookup = FileFormats.lookup(source);
        if (lookup != null) {
            return lookup;
        }
        else {
            String fileName = new File(uri).getName();
            return FileFormats.lookup(fileName);
        }
    }
}
