/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.common.files.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.WstxDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import org.dockbox.selene.api.annotations.entity.Metadata;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.SeleneInformation;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Utility class which configures XStream with some default settings and permissions. For simple
 * usage (custom configuration file with only standard fields and collections):
 *
 * <p>
 *
 * <pre>{@code
 * MyConfigFile config = XStreamUtils.fromXMLQuietly(file,
 *      MyConfigFile.class, () -> new MyConfigFile());
 *
 * XStreamUtils.toXMLQuietly(config, file);
 * }</pre>
 *
 * <p>For complex usage, for example to white list more classes, use a {@link XStreamBuilder} from
 * {@link #create()}.
 *
 * @see <a href="http://x-stream.github.io/tutorial.html">XStream tutorial</a>
 * @see <a href="http://x-stream.github.io/security.html">XStream security</a>
 */
@SuppressWarnings("unused")
public final class XStreamUtils {

    private static final String UTF_8 = "UTF-8";
    private static Map<String, Class<?>> aliasedTypes;

    private XStreamUtils() {}

    /**
     * Reads an XML file with XStream using UTF-8 encoding.
     *
     * @param type
     *         the type of object to load
     * @param file
     *         the file to load from
     * @param allowedTypes
     *         any other types allowed to be loaded
     */
    public static <T> T fromXML(Class<T> type, File file, Class<?>... allowedTypes) throws IOException {
        XStream stream = getXStream();
        stream.allowTypes(new Class[]{ type });
        stream.allowTypes(allowedTypes);
        return fromXml(stream, type, file, UTF_8);
    }

    /**
     * NOTE: Make sure you call fromXML and toXML with UTF-8 streams!
     *
     * @return an XStream object with the correct aliases
     */
    public static XStream getXStream() {
        XStream xstream = new XStream(new WstxDriver());
        configureXStream(xstream);
        return xstream;
    }

    /**
     * @param type
     *         does not allow type, only check is instance
     */
    private static <T> @Nullable T fromXml(XStream stream, Class<T> type, File file, String charSet) throws IOException {
        Object object = fromXml(stream, file, charSet);
        if (type.isInstance(object)) {
            return type.cast(object);
        }
        else {
            return null;
        }
    }

    private static void configureXStream(XStream xstream) {
        if (null == aliasedTypes) {
            aliasedTypes = SeleneUtils.emptyConcurrentMap();
            Collection<Class<?>> annotatedTypes = Reflect.getAnnotatedTypes(SeleneInformation.PACKAGE_PREFIX, Metadata.class);
            annotatedTypes.forEach(type -> {
                Metadata metadata = type.getAnnotation(Metadata.class);
                if (aliasedTypes.containsKey(metadata.alias()))
                    Selene.log().warn("Attempting to register a duplicate entity alias '" + metadata.alias() + "'");
                xstream.alias(metadata.alias(), type);
                // Put the alias last, so if xstream.alias ever throws a Exception this won't be called
                aliasedTypes.put(metadata.alias(), type);
            });
        }

        xstream.addPermission(NoTypePermission.NONE);

        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypes(new Class[]{ String.class, LocalDate.class, LocalDateTime.class });
        xstream.allowTypeHierarchy(Collection.class);
        xstream.allowTypeHierarchy(Map.class);
        xstream.allowTypeHierarchy(Enum.class);

        xstream.allowTypes(new Class[]{ Mapper.Null.class });

        xstream.allowTypesByWildcard(new String[]{ "org.dockbox.selene.**" });

        // we don't want to use references
        xstream.setMode(XStream.NO_REFERENCES);
    }

    private static Object fromXml(XStream stream, File file, String charSet) throws IOException {
        InputStreamReader reader = null;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            if (null != charSet) {
                reader = new InputStreamReader(in, charSet);
            }
            else {
                reader = new InputStreamReader(in);
            }

            return stream.fromXML(reader);
        }
        finally {
            if (null != reader) {
                reader.close();
            }
        }
    }

    /** Writes an Object to an XML file with XStream using UTF-8 encoding. */
    public static void toXML(Object object, File file) throws IOException {
        XStream stream = getXStream(object.getClass().getClassLoader());
        stream.allowTypes(new Class[]{ object.getClass() });
        toXml(stream, object, file);
    }

    /**
     * NOTE: Make sure you call fromXML and toXML with UTF-8 streams!
     *
     * @return an XStream object with the correct aliases
     */
    public static XStream getXStream(ClassLoader classLoader) {
        if (null == classLoader) {
            return getXStream();
        }

        XStream xstream = new XStream(new WstxDriver());
        xstream.setClassLoader(classLoader);

        configureXStream(xstream);
        return xstream;
    }

    private static void toXml(XStream stream, Object object, File file) throws IOException {
        toXml(stream, object, file, UTF_8);
    }

    /** Prefer not to call this method directly and used the default UTF-8 export functions */
    public static void toXml(XStream stream, Object object, File file, String encoding) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), encoding)) {
            stream.marshal(object, new PrettyPrintWriter(writer));
        }
    }

    /**
     * Reads an XML file with XStream using UTF-8 encoding using the type's ClassLoader and ignoring
     * any IO exception (returning the fall back). You may still can exception if the XML is not as
     * expected. If you do not need backward compatibility, user {@link
     * XStreamBuilder#ignoreUnknownElements()} instead.
     *
     * @param <T>
     *         the type of object to load, be default this type will be white listed, if additional
     *         types are needed, use create().allowTypes(types).readQuietly(file, type, fallBack).
     */
    public static <T> @Nullable T fromXMLQuietly(File file, Class<T> type, Supplier<T> fallback) {
        if (!file.exists() || !file.isFile()) {
            return null != fallback ? fallback.get() : null;
        }

        XStream stream = getXStream(type.getClassLoader());
        stream.allowTypes(new Class[]{ type });
        return fromXmlQuietly(stream, file, UTF_8, type, fallback);
    }

    private static <T> @Nullable T fromXmlQuietly(XStream stream, File file, String charSet, Class<T> type, Supplier<T> fallback) {
        if (!file.exists() || !file.isFile()) {
            return null != fallback ? fallback.get() : null;
        }

        try {
            Object object = fromXml(stream, type, file, charSet);
            if (type.isInstance(object)) {
                return type.cast(object);
            }
            else {
                String message = "Unexpected object type! expected: " + type + ", got " + (object != null ? object.getClass() : "null");
                Selene.log().warn(message);
                assert false : message;
                return null != fallback ? fallback.get() : null;
            }
        }
        catch (Exception e) {
            Selene.handle(e);
            return null != fallback ? fallback.get() : null;
        }
    }

    /**
     * Writes an Object to an XML file with XStream using UTF-8 encoding using the object class
     * ClassLoader and ignoring any exception.
     */
    public static void toXMLQuietly(Object object, File file) {
        XStream stream = getXStream(object.getClass().getClassLoader());
        toXmlQuietly(stream, object, file);
    }

    private static void toXmlQuietly(XStream stream, Object object, File file) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), StandardCharsets.UTF_8)) {
            stream.toXML(object, writer);
        }
        catch (IOException e) {
            Selene.handle(e);
        }
    }

    /** @return a builder to configure the XStream before reading or writing a XML file. */
    public static XStreamBuilder create() {
        return new XStreamBuilder(getXStream());
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class XStreamBuilder {

        private final XStream stream;

        private boolean classLoaderSet;
        private boolean fallBackToPlatformEncoding;

        private XStreamBuilder(XStream stream) {
            this.stream = stream;
        }

        /** @see XStream#alias(String, Class) */
        public XStreamBuilder alias(String name, Class<?> type) {
            this.stream.alias(name, type);
            return this;
        }

        /** @see XStream#alias(String, Class, Class) */
        public <T> XStreamBuilder alias(
                String name, Class<T> type, Class<? extends T> defaultImplementation) {
            this.stream.alias(name, type, defaultImplementation);
            return this;
        }

        /** @see XStream#aliasPackage(String, String) */
        public XStreamBuilder aliasPackage(String name, String pkgName) {
            this.stream.aliasPackage(name, pkgName);
            return this;
        }

        /** @see XStream#aliasField(String, Class, String) */
        public XStreamBuilder aliasField(String alias, Class<?> definedIn, String fieldName) {
            if (Reflect.hasFieldRecursive(definedIn, fieldName))
                this.stream.aliasField(alias, definedIn, fieldName);
            return this;
        }

        /** @see XStream#aliasAttribute(String, String) */
        public XStreamBuilder aliasAttribute(String alias, String attributeName) {
            this.stream.aliasAttribute(alias, attributeName);
            return this;
        }

        /** @see XStream#aliasAttribute(Class, String, String) */
        public XStreamBuilder aliasAttribute(Class<?> definedIn, String attributeName, String alias) {
            if (Reflect.hasFieldRecursive(definedIn, attributeName))
                this.stream.aliasAttribute(definedIn, attributeName, alias);
            return this;
        }

        /** @see XStream#aliasSystemAttribute(String, String) */
        public XStreamBuilder aliasSystemAttribute(String alias, String systemAttributeName) {
            this.stream.aliasSystemAttribute(alias, systemAttributeName);
            return this;
        }

        /** @see XStream#aliasType(String, Class) */
        public XStreamBuilder aliasType(String name, Class<?> type) {
            this.stream.aliasType(name, type);
            return this;
        }

        /** @see XStream#allowTypeHierarchy(Class) */
        public XStreamBuilder allowTypeHierarchy(Class<?> type) {
            this.stream.allowTypeHierarchy(type);
            return this;
        }

        /** @see XStream#allowTypes(Class[]) */
        public XStreamBuilder allowTypes(Collection<? extends Class<?>> types) {
            return this.allowTypes(types.toArray(new Class[0]));
        }

        /** @see XStream#allowTypes(Class[]) */
        public XStreamBuilder allowTypes(Class<?>... types) {
            this.stream.allowTypes(types);
            return this;
        }

        /** @see XStream#allowTypesByRegExp(Pattern[]) */
        public XStreamBuilder allowTypesByRegExp(Pattern... patterns) {
            this.stream.allowTypesByRegExp(patterns);
            return this;
        }

        /** @see XStream#allowTypesByRegExp(String[]) */
        public XStreamBuilder allowTypesByRegExp(String... patterns) {
            this.stream.allowTypesByRegExp(patterns);
            return this;
        }

        /** @see XStream#allowTypesByWildcard(String[]) */
        public XStreamBuilder allowTypesByWildcard(String... patterns) {
            this.stream.allowTypesByWildcard(patterns);
            return this;
        }

        /** @see XStream#denyTypes(Class[]) */
        public XStreamBuilder denyTypes(Class<?>... types) {
            this.stream.denyTypes(types);
            return this;
        }

        /** @see XStream#denyTypeHierarchy(Class) */
        public XStreamBuilder denyTypeHierarchy(Class<?> type) {
            this.stream.denyTypeHierarchy(type);
            return this;
        }

        /** @see XStream#denyTypesByRegExp(Pattern[]) */
        public XStreamBuilder denyTypesByRegExp(Pattern... regexps) {
            this.stream.denyTypesByRegExp(regexps);
            return this;
        }

        /** @see XStream#denyTypesByRegExp(String[]) */
        public XStreamBuilder denyTypesByRegExp(String... regexps) {
            this.stream.denyTypesByRegExp(regexps);
            return this;
        }

        /** @see XStream#denyTypesByWildcard(String[]) */
        public XStreamBuilder denyTypesByWildcard(String... patterns) {
            this.stream.denyTypesByWildcard(patterns);
            return this;
        }

        /** @see XStream#ignoreUnknownElements() */
        public XStreamBuilder ignoreUnknownElements() {
            this.stream.ignoreUnknownElements();
            return this;
        }

        /** @see XStream#ignoreUnknownElements(Pattern) */
        public XStreamBuilder ignoreUnknownElements(Pattern pattern) {
            this.stream.ignoreUnknownElements(pattern);
            return this;
        }

        /** @see XStream#ignoreUnknownElements(String) */
        public XStreamBuilder ignoreUnknownElements(String pattern) {
            this.stream.ignoreUnknownElements(pattern);
            return this;
        }

        /** @see XStream#omitField(Class, String) */
        public XStreamBuilder omitField(Class<?> type, String field) {
            if (Reflect.hasFieldRecursive(type, field))
                this.stream.omitField(type, field);
            return this;
        }

        /** @see XStream#processAnnotations(Class[]) */
        public XStreamBuilder processAnnotations(Class<?>... types) {
            this.stream.processAnnotations(types);
            return this;
        }

        /** @see XStream#setMode(int) */
        public XStreamBuilder setMode(int mode) {
            this.stream.setMode(mode);
            return this;
        }

        /** @see XStream#registerConverter(Converter) */
        public XStreamBuilder registerConverter(Converter converter) {
            this.stream.registerConverter(converter);
            return this;
        }

        /** @see XStream#registerConverter(Converter, int) */
        public XStreamBuilder registerConverter(Converter converter, int priority) {
            this.stream.registerConverter(converter, priority);
            return this;
        }

        /** @see XStream#registerConverter(SingleValueConverter) */
        public XStreamBuilder registerConverter(SingleValueConverter converter) {
            this.stream.registerConverter(converter);
            return this;
        }

        /** @see XStream#registerConverter(SingleValueConverter, int) */
        public XStreamBuilder registerConverter(SingleValueConverter converter, int priority) {
            this.stream.registerConverter(converter, priority);
            return this;
        }

        /** @see XStream#registerLocalConverter(Class, String, Converter) */
        public XStreamBuilder registerLocalConverter(
                Class<?> definedIn, String fieldName, Converter converter) {
            if (Reflect.hasFieldRecursive(definedIn, fieldName))
                this.stream.registerLocalConverter(definedIn, fieldName, converter);
            return this;
        }

        /** @see XStream#registerLocalConverter(Class, String, SingleValueConverter) */
        public XStreamBuilder registerLocalConverter(
                Class<?> definedIn, String fieldName, SingleValueConverter converter) {
            if (Reflect.hasFieldRecursive(definedIn, fieldName))
                this.stream.registerLocalConverter(definedIn, fieldName, converter);
            return this;
        }

        /**
         * First try to load the XML as UTF-8, re-try using the platform encoding on any exception.
         *
         * @see #read(File)
         * @see #read(Class, File)
         * @see #readCollection(Class, File)
         * @see #readQuietly(File, Class, Supplier)
         */
        public XStreamBuilder fallBackToPlatformEncoding() {
            this.fallBackToPlatformEncoding = true;
            return this;
        }

        /**
         * Allowing the given type and using its class loader if not yet set, read an instance of that
         * type from the given file.
         */
        public <T> T read(Class<T> type, File file) throws IOException {
            if (!this.classLoaderSet) {
                this.classLoader(type.getClassLoader());
            }
            this.allowTypes(type);

            try {
                return fromXml(this.stream, type, file, UTF_8);
            }
            catch (IOException e) {
                if (this.fallBackToPlatformEncoding) {
                    return fromXml(this.stream, type, file, null);
                }
                else {
                    throw e;
                }
            }
        }

        /** @see XStream#setClassLoader(ClassLoader) */
        public XStreamBuilder classLoader(ClassLoader loader) {
            this.stream.setClassLoader(loader);
            this.classLoaderSet = true;
            return this;
        }

        /**
         * Allowing the given type and using its class loader if not yet set, read an instance of that
         * type from the given file.
         *
         * @return the read instance of the given type, or the fall back on any error
         */
        public <T> T readQuietly(File file, Class<T> type, Supplier<T> fallBack) {
            if (!this.classLoaderSet) {
                this.classLoader(type.getClassLoader());
            }
            this.allowTypes(type);

            if (this.fallBackToPlatformEncoding) {
                return fromXmlQuietly(
                        this.stream,
                        file,
                        UTF_8,
                        type,
                        () -> fromXmlQuietly(this.stream, file, null, type, fallBack));
            }
            else {
                return fromXmlQuietly(this.stream, file, UTF_8, type, fallBack);
            }
        }

        /**
         * Allowing the given element type and using its class loader if not yet set, read an collection
         * of that type from the given file.
         */
        public <T> Collection<T> readCollection(Class<T> elementType, File file) throws IOException {
            if (!this.classLoaderSet) {
                this.classLoader(elementType.getClassLoader());
            }
            this.allowTypes(elementType);

            @SuppressWarnings("unchecked")
            Collection<T> loaded = (Collection<T>) this.read(file);
            return loaded;
        }

        public Object read(File file) throws IOException {
            try {
                return fromXml(this.stream, file, UTF_8);
            }
            catch (IOException e) {
                if (this.fallBackToPlatformEncoding) {
                    return fromXml(this.stream, file, null);
                }
                else {
                    throw e;
                }
            }
        }

        public void write(Object object, File file) throws IOException {
            if (!this.classLoaderSet) {
                // probably not needed for write...
                this.objectClassLoader(object);
            }
            toXml(this.stream, object, file);
        }

        /** @see XStream#setClassLoader(ClassLoader) */
        public XStreamBuilder objectClassLoader(Object object) {
            return this.classLoader(object.getClass().getClassLoader());
        }

        public void writeQuietly(Object object, File file) {
            if (!this.classLoaderSet) {
                this.objectClassLoader(object);
            }
            toXmlQuietly(this.stream, object, file);
        }

        /**
         * Avoid using this method unless really needed, for example, to configure the stream with
         * properties not supported by the builder or trying to load a legacy xml file.
         *
         * @return the actual {@link XStream} configured so far
         */
        public XStream getStream() {
            return this.stream;
        }
    }
}
