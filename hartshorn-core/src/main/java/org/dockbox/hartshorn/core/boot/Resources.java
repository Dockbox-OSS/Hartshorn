package org.dockbox.hartshorn.core.boot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class Resources {

    private static ClassLoader defaultClassLoader;

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
        return getResourceURLs(resource).stream()
                .map(url -> new File(url.getFile()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<File> getResourcesAsFiles(final ClassLoader loader, final String resource) throws IOException {
        return getResourceURLs(loader, resource).stream()
                .map(url -> new File(url.getFile()))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static ClassLoader getClassLoader() {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (final SecurityException e) {
            return Hartshorn.class.getClassLoader();
        }
    }
}
