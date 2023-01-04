/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.util.resources;

import org.dockbox.hartshorn.application.Hartshorn;
import org.dockbox.hartshorn.application.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class Resources {

    private Resources() {
    }

    public static URL getResourceURL(final String resource) throws IOException {
        return getResourceURL(getClassLoader(), resource);
    }

    public static URL getResourceURL(final ClassLoader loader, final String resource) throws IOException {
        URL url = null;
        if (loader != null) url = loader.getResource(resource);
        if (url == null) url = ClassLoader.getSystemResource(resource);
        if (url == null) throw new IOException("Could not find resource " + resource);
        return url;
    }

    public static File getResourceAsFile(final String resource) throws IOException {
        return new File(getResourceURL(resource).getFile());
    }

    public static File getResourceAsFile(final ClassLoader loader, final String resource) throws IOException {
        return new File(getResourceURL(loader, resource).getFile());
    }

    public static Set<URL> getResourceURLs(final String resource) throws IOException {
        return getResourceURLs(getClassLoader(), resource);
    }

    public static Set<URL> getResourceURLs(final ClassLoader loader, final String resource) throws IOException {
        final Set<URL> urls = new HashSet<>();
        if (loader != null) {
            final Enumeration<URL> resources = loader.getResources(resource);
            if (resources != null) {
                while (resources.hasMoreElements()) urls.add(resources.nextElement());
            }
        }
        final Enumeration<URL> systemResources = ClassLoader.getSystemResources(resource);
        if (systemResources != null) {
            while (systemResources.hasMoreElements()) urls.add(systemResources.nextElement());
        }
        return Collections.unmodifiableSet(urls);
    }

    public static Set<File> getResourcesAsFiles(final String resource) throws IOException {
        return getResourcesAsFiles(getClassLoader(), resource);
    }

    public static Set<File> getResourcesAsFiles(final ClassLoader loader, final String resource) throws IOException {
        return getResourceURLs(loader, resource).stream()
                .map(url -> new File(url.getFile()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<URI> getResourceURIs(final ApplicationContext applicationContext, final String resource, final ResourceLookupStrategy... strategies) {
        final Set<URI> uris = new HashSet<>();
        for (final ResourceLookupStrategy strategy : strategies) {
            uris.addAll(strategy.lookup(applicationContext, resource));
        }
        return Collections.unmodifiableSet(uris);
    }

    private static ClassLoader getClassLoader() {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (final SecurityException e) {
            return Hartshorn.class.getClassLoader();
        }
    }
}
