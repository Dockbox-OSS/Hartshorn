/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.launchpad.environment;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.resources.Resources;
import org.dockbox.hartshorn.util.option.Option;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * The default implementation of {@link ClasspathResourceLocator}. This implementation will copy the
 * resource to a temporary location and return the path to the temporary location.
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public class ClassLoaderClasspathResourceLocator implements ClasspathResourceLocator {

    private final ApplicationEnvironment environment;

    public ClassLoaderClasspathResourceLocator(ApplicationEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Option<URI> resource(String name) throws IOException {
        URL url = Resources.getResourceURL(name);
        try {
            return Option.of(url.toURI());
        }
        catch (URISyntaxException e) {
            throw new IOException("Could not convert URL to URI: " + url, e);
        }
    }

    @Override
    public Set<URI> resources(String name) throws IOException {
        Set<URI> uris = new HashSet<>();
        for (URL url : Resources.getResourceURLs(name)) {
            try {
                uris.add(url.toURI());
            }
            catch (URISyntaxException e) {
                throw new IOException("Could not convert URL to URI: " + url, e);
            }
        }
        return uris;
    }

    @Override
    public URI classpathUri() {
        try {
            URL resource = ApplicationContext.class.getClassLoader().getResource("");
            if (resource == null) {
                return null;
            }
            return resource.toURI();
        }
        catch (URISyntaxException e) {
            this.environment.handle("Could not look up classpath base", e);
            return null;
        }
    }
}
