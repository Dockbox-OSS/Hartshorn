/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.data.config;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.ResourceLookupStrategy;
import org.dockbox.hartshorn.data.context.ConfigurationURIContext;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.util.TypeUtils;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.inject.Singleton;

@Singleton
public class StandardURIConfigProcessor implements URIConfigProcessor {

    @Override
    public void process(final ApplicationContext context, final Set<ConfigurationURIContext> contexts) {
        for (final ConfigurationURIContext uriContext : contexts) {
            final URI uri = uriContext.uri();
            final String source = uriContext.source();

            final FileFormat format = this.lookupFileFormat(uri, source, context, uriContext.strategy());

            if (format == null) {
                context.log().error("Unknown file format: " + source + ", declared by " + uriContext.key().type().getSimpleName());
                return;
            }

            final Map<String, Object> cache = TypeUtils.adjustWildcards(context.get(ObjectMapper.class)
                    .fileType(format)
                    .read(uri, Map.class)
                    .orElseGet(HashMap::new), Map.class);

            context.log().debug("Located " + cache.size() + " properties in " + uri.getPath());
            context.get(PropertyHolder.class).set(cache);
        }
    }

    protected FileFormat lookupFileFormat(final URI uri, final String source, final ApplicationContext context, final ResourceLookupStrategy strategy) {
        // If the source has a file extension, use that
        final FileFormats lookup = FileFormats.lookup(source);
        if (lookup != null) {
            return lookup;
        }
        else {
            final String fileName = new File(uri).getName();
            return FileFormats.lookup(fileName);
        }
    }
}
