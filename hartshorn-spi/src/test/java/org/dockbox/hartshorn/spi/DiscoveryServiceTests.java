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

package org.dockbox.hartshorn.spi;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.MultiMapCollector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class
DiscoveryServiceTests {

    private static final String IMPLEMENTATION_NAME = "HelloWorldSupplierImplementation";
    private static final String IMPLEMENTATION_QUALIFIED_NAME =
        "%s.%s".formatted(DiscoveryServiceTests.class.getPackageName(), IMPLEMENTATION_NAME);
    private static final String HELLO_WORLD_MESSAGE = "Hello World!";
    private static final String HELLO_WORLD_SUPPLIER_IMPLEMENTATION_SOURCE = """
        package org.dockbox.hartshorn.spi;
        public class HelloWorldSupplierImplementation implements HelloWorldSupplier {
            @Override
            public String getHelloWorld() {
                return "%s";
            }
        }
        """.formatted(HELLO_WORLD_MESSAGE);

    @AfterEach
    void tearDown() throws NoSuchFieldException {
        Field discoveryService = DiscoveryService.class.getDeclaredField("DISCOVERY_SERVICE");
        discoveryService.setAccessible(true);
        try {
            discoveryService.set(null, null);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void discoveryServiceWithOverride() throws Exception {
        ByteClassLoader classLoader = createImplementationAwareClassLoader();

        DiscoveryService.instance().addClassLoader(classLoader);
        DiscoveryService.instance()
            .override(HelloWorldSupplier.class, IMPLEMENTATION_QUALIFIED_NAME);

        HelloWorldSupplier helloWorldSupplier = DiscoveryService.instance()
                .discover(HelloWorldSupplier.class);
        assertThat(helloWorldSupplier.getHelloWorld())
                .isEqualTo(HELLO_WORLD_MESSAGE);
    }

    @NonNull
    private static ByteClassLoader createImplementationAwareClassLoader() throws IOException {
        JavaFileObject implementationClassFile = compileAndGetRuntimeImplementation();

        // Class is compiled, but not loaded, so we need to load it manually.
        return new ByteClassLoader(
            new URL[] {},
            DiscoveryServiceTests.class.getClassLoader(),
            Collections.singletonMap(IMPLEMENTATION_QUALIFIED_NAME,
                implementationClassFile.openInputStream().readAllBytes()));
    }

    @Test
    void registryContainsTypeAfterRegistration() throws Exception {
        ByteClassLoader classLoader = createImplementationAwareClassLoader();
        DiscoveryService.instance().addClassLoader(classLoader);

        assertThat(
                DiscoveryService.instance().contains(HelloWorldSupplier.class)
        ).isFalse();

        DiscoveryService.instance()
            .override(HelloWorldSupplier.class, IMPLEMENTATION_QUALIFIED_NAME);

        assertThat(
                DiscoveryService.instance().contains(HelloWorldSupplier.class)
        ).isTrue();
    }

    @Test
    void discoveryFailsIfNoImplementationExists() {
        assertThatThrownBy(() -> {
            DiscoveryService.instance().discover(HelloWorldSupplier.class);
        }).isInstanceOf(ServiceDiscoveryException.class);
    }

    @Test
    void discoveryFailsIfImplementationClassDoesNotExist() {
        assertThatThrownBy(() -> {
            DiscoveryService.instance()
                    .override(HelloWorldSupplier.class, "org.dockbox.hartshorn.spi.DoesNotExist");
            DiscoveryService.instance().discover(HelloWorldSupplier.class);
        }).isInstanceOf(ServiceDiscoveryException.class);
    }

    @Test
    void overrideFailsIfImplementationNotAssignable() {
        assertThatThrownBy(() -> {
            DiscoveryService.instance().override(
                    HelloWorldSupplier.class,
                    DiscoveryServiceTests.class
            );
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void discoveryFailsIfImplementationHasNoDefaultConstructor() {
        assertThatThrownBy(() -> {
            DiscoveryService.instance().override(
                    HelloWorldSupplier.class,
                    HelloWorldSupplierImplementationWithNoDefaultConstructor.class
            );
            DiscoveryService.instance().discover(HelloWorldSupplier.class);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void discoveryForMultipleImplementations() throws Exception {
        DiscoveryService.instance()
            .override(HelloWorldSupplier.class, HelloWorldSupplierImplementationA.class);
        DiscoveryService.instance()
            .override(HelloWorldSupplier.class, HelloWorldSupplierImplementationB.class);

        assertThatThrownBy(() -> {
            DiscoveryService.instance().discover(HelloWorldSupplier.class);
        }).isInstanceOf(ServiceDiscoveryException.class);

        Set<HelloWorldSupplier> suppliers =
            DiscoveryService.instance().discoverAll(HelloWorldSupplier.class);
        assertThat(suppliers)
                .hasSize(2)
                .anyMatch(HelloWorldSupplierImplementationA.class::isInstance)
                .anyMatch(HelloWorldSupplierImplementationB.class::isInstance);
    }

    @Test
    void discoveryFailsIfImplementationConstructorFails() {
        DiscoveryService.instance().override(
                HelloWorldSupplier.class,
                HelloWorldSupplierImplementationWithErrorInConstructor.class
        );
        assertThatThrownBy(() -> {
            DiscoveryService.instance().discover(HelloWorldSupplier.class);
        }).isInstanceOf(ServiceDiscoveryException.class);
    }

    private static JavaFileObject compileAndGetRuntimeImplementation() {
        Compilation compilation = Compiler.javac()
            .compile(JavaFileObjects.forSourceString(IMPLEMENTATION_NAME,
                HELLO_WORLD_SUPPLIER_IMPLEMENTATION_SOURCE));

        assertThat(compilation.errors()).isEmpty();

        ImmutableList<JavaFileObject> generatedFiles = compilation.generatedFiles();
        assertThat(generatedFiles).hasSize(1);

        MultiMap<Kind, JavaFileObject> generatedByKind = generatedFiles.stream()
            .collect(MultiMapCollector.groupingBy(JavaFileObject::getKind));
        assertThat(generatedByKind.get(Kind.CLASS))
                        .hasSize(1); // HelloWorldSupplierImplementation.class

        return CollectionUtilities.first(generatedByKind.get(Kind.CLASS));
    }

    public static class ByteClassLoader extends URLClassLoader {
        private final Map<String, byte[]> extraClassDefs;

        public ByteClassLoader(URL[] urls, ClassLoader parent, Map<String, byte[]> extraClassDefs) {
            super(urls, parent);
            this.extraClassDefs = Collections.unmodifiableMap(extraClassDefs);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.get(name);
            if (classBytes != null) {
                return this.defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }
    }

    public static class HelloWorldSupplierImplementationWithNoDefaultConstructor
        implements HelloWorldSupplier {
        private final String arg;

        public HelloWorldSupplierImplementationWithNoDefaultConstructor(String arg) {
            this.arg = arg;
        }

        @Override
        public String getHelloWorld() {
            return this.arg;
        }
    }

    public static class HelloWorldSupplierImplementationWithErrorInConstructor
        implements HelloWorldSupplier {

        public HelloWorldSupplierImplementationWithErrorInConstructor() {
            throw new RuntimeException("Error in constructor");
        }

        @Override
        public String getHelloWorld() {
            return "Hello World!";
        }
    }

    public static class HelloWorldSupplierImplementationA implements HelloWorldSupplier {
        @Override
        public String getHelloWorld() {
            return "Hello World!";
        }
    }

    public static class HelloWorldSupplierImplementationB implements HelloWorldSupplier {
        @Override
        public String getHelloWorld() {
            return "Hello Hartshorn!";
        }
    }
}
