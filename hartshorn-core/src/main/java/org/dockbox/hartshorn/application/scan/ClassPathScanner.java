package org.dockbox.hartshorn.application.scan;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassPathScanner {

    @FunctionalInterface
    public interface ResourceHandler {
        void handle(ClassPathResource resource);
    }

    public interface ClassPathResource {

        ClassLoader getClassloader();

        Path getPath();

        String getResourceName();

        boolean isClassResource();
    }

    private final List<String> messages = new ArrayList<>();
    private final List<Path> paths = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();
    private long scanTime = -1;
    private final Set<ClassLoader> classLoaders = new HashSet<>();
    private final List<String> beginResourceNameFilters = new ArrayList<>(0);
    private boolean filterOnlyResources = false;
    private boolean filterOnlyClasses = true;
    private Class<? extends Annotation> methodAnnotation;
    private Class<? extends Annotation> typeAnnotation;
    private boolean excludeInnerClasses = false;

    public ClassPathScanner() {
        this.addSystemPropertyPaths("java.class.path");
    }

    public static ClassPathScanner create() {
        return new ClassPathScanner();
    }

    public ClassPathScanner addSystemPropertyPaths(final String key) {
        final String value = System.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return this;
        }

        for (final String path : value.split("" + File.pathSeparatorChar, -1)) {
            if (path == null || path.trim().isEmpty()) continue;
            final File file = new File(path);
            if (!file.exists()) continue;

            try {
                final URL url = file.toURI().toURL();
                this.classLoaders.add(new URLClassLoader(new URL[] { url }) {
                    public String toString() {
                        return super.toString() + " [url=" + url.toExternalForm() + "]";
                    }
                });
            }
            catch (final Exception e) {
                // Ignore
            }
        }
        return this;
    }

    public ClassPathScanner addClass(final Class<?> clazz) {
        if (clazz == null) {
            return this;
        }
        ClassLoader classLoader = clazz.getClassLoader();
        while (classLoader != null) {
            this.classLoaders.add(classLoader);
            classLoader = classLoader.getParent();
        }

        return this;
    }

    public Set<ClassLoader> getClassLoaders() {
        return Collections.unmodifiableSet(this.classLoaders);
    }

    public ClassPathScanner scan(final ResourceHandler handler) {
        this.reset();

        final long start = System.currentTimeMillis();
        for (final ClassLoader classLoader : this.classLoaders) {
            if (classLoader instanceof URLClassLoader) {
                this.scanByClassLoader(handler, (URLClassLoader) classLoader);
            }
            else {
                this.messages.add("unsupported ClassLaoder: " + classLoader);
            }
        }

        this.scanTime = System.currentTimeMillis() - start;
        return this;
    }

    private void reset() {
        this.classNames.clear();
        this.paths.clear();
        this.messages.clear();
    }

    private void scanByClassLoader(final ResourceHandler handler, final URLClassLoader classLoader) {
        for (final URL url : classLoader.getURLs()) {
            if (url.getFile() != null && !url.getFile().isEmpty()) {

                final File file = new File(url.getFile());
                if (file.exists() && file.isDirectory())
                    this.scanByDirectory(handler, classLoader, file);

                else if (file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(".jar"))
                    this.scanByJar(handler, classLoader, url, file);

                else this.messages.add("not found: " + url);
            }
        }
    }

    public List<String> getMessages() {
        return new ArrayList<>(this.messages);
    }

    public long scanTime() {
        return this.scanTime;
    }

    private void scanByJar(final ResourceHandler handler, final URLClassLoader classLoader, final URL url, final File jarFile) {
        try {
            final FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(url.toURI()), (ClassLoader) null);
            Files.walkFileTree(fileSystem.getRootDirectories().iterator().next(), new JarFileWalker(handler, classLoader));
        }
        catch (final Exception e) {
            this.messages.add("failed scanning jar: " + jarFile + ": " + e);
        }
    }

    private void scanByDirectory(final ResourceHandler handler, final URLClassLoader classLoader, final File directory) {
        try {
            final File rootDir = directory.getCanonicalFile();
            final int rootDirNameLen = rootDir.getCanonicalPath().length();
            Files.walkFileTree(rootDir.toPath(), new DirectoryFileTreeWalker(rootDirNameLen, handler, classLoader));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void scanByPath(final ResourceHandler handler, final URLClassLoader classLoader, final String resourceName, final Path inPath) {
        this.paths.add(inPath);
        if (handler == null) return;

        final boolean isClassResource = resourceName.toLowerCase().endsWith(".class");
        final String checkedResourceName = !isClassResource
                ? resourceName
                : resourceName.substring(0, resourceName.length() - 6).replace('/', '.').replace('\\', '.');

        if (isClassResource && this.filterOnlyClasses && this.classNames.contains(checkedResourceName)) return;

        if (this.filterOnlyClasses && !isClassResource) return;
        if (this.filterOnlyResources && isClassResource) return;
        if (this.typeAnnotation != null && !isClassResource) return;
        if (this.methodAnnotation != null && !isClassResource) return;
        if (this.excludeInnerClasses && isClassResource && checkedResourceName.indexOf('$') > -1) return;

        for (final String beginFilterName : this.beginResourceNameFilters) {
            if (!checkedResourceName.startsWith(beginFilterName)) return;
        }

        final ClassPathResource resource = new ClassCandidateResource(classLoader, inPath, checkedResourceName, isClassResource);

        try {
            if (isClassResource && this.typeAnnotation != null) {
                final Class<?> clazz = classLoader.loadClass(checkedResourceName);
                if (clazz.getDeclaredAnnotation(this.typeAnnotation) == null) return;
            }
        }
        catch (final Throwable e) {
            this.messages.add("failed to load class: " + e);
            return;
        }

        try {
            if (isClassResource && this.methodAnnotation != null) {
                final Class<?> clazz = classLoader.loadClass(checkedResourceName);
                boolean isFound = false;
                for (final Method method : clazz.getDeclaredMethods()) {
                    if (method.getDeclaredAnnotation(this.methodAnnotation) != null) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) return;
            }

        }
        catch (final Throwable e) {
            this.messages.add("failed to scan methods at class " + checkedResourceName + " : " + e);
            return;
        }

        handler.handle(resource);
    }

    public ClassPathScanner filterBeginResourceName(final String beginResourceName) {
        if (beginResourceName != null) {
            this.beginResourceNameFilters.add(beginResourceName);
        }
        return this;
    }

    public ClassPathScanner filterClassOnly() {
        this.filterOnlyClasses = true;
        this.filterOnlyResources = false;
        return this;
    }

    public ClassPathScanner filterResourceOnly() {
        this.filterOnlyClasses = false;
        this.filterOnlyResources = true;
        return this;
    }

    public ClassPathScanner filterMethodAnnotation(final Class<? extends Annotation> annotation) {
        this.methodAnnotation = annotation;
        return this;
    }

    public ClassPathScanner filterTypeAnnotation(final Class<? extends Annotation> annotation) {
        this.typeAnnotation = annotation;
        return this;
    }

    public ClassPathScanner filterExcludeInnerClasses() {
        this.excludeInnerClasses = true;
        return this;
    }

    private static class ClassCandidateResource implements ClassPathResource {

        private final URLClassLoader classLoader;
        private final Path path;
        private final String resourceName;
        private final boolean isClassResource;

        public ClassCandidateResource(final URLClassLoader classLoader, final Path path, final String resourceName, final boolean isClassResource) {
            this.classLoader = classLoader;
            this.path = path;
            this.resourceName = resourceName;
            this.isClassResource = isClassResource;
        }

        @Override
        public ClassLoader getClassloader() {
            return this.classLoader;
        }

        @Override
        public Path getPath() {
            return this.path;
        }

        @Override
        public String getResourceName() {
            return this.resourceName;
        }

        @Override
        public boolean isClassResource() {
            return this.isClassResource;
        }
    }

    private class JarFileWalker implements FileVisitor<Path> {

        private final ResourceHandler handler;
        private final URLClassLoader classLoader;

        public JarFileWalker(final ResourceHandler handler, final URLClassLoader classLoader) {
            this.handler = handler;
            this.classLoader = classLoader;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
            final String resourceName = file.toString().substring(1);
            ClassPathScanner.this.scanByPath(this.handler, this.classLoader, resourceName, file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) {
            return FileVisitResult.CONTINUE;
        }
    }

    private class DirectoryFileTreeWalker implements FileVisitor<Path> {

        private final int rootDirNameLength;
        private final ResourceHandler handler;
        private final URLClassLoader classLoader;

        public DirectoryFileTreeWalker(final int rootDirNameLength, final ResourceHandler handler, final URLClassLoader classLoader) {
            this.rootDirNameLength = rootDirNameLength;
            this.handler = handler;
            this.classLoader = classLoader;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            final String resourceName = file.toFile().getCanonicalPath().substring(this.rootDirNameLength + 1);
            ClassPathScanner.this.scanByPath(this.handler, this.classLoader, resourceName, file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) {
            return FileVisitResult.CONTINUE;
        }
    }
}
// TODO: Go through the code and refactor where needed, also split into multiple files and add tests
