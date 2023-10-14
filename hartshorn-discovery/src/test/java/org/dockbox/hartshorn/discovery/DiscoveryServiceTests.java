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

package org.dockbox.hartshorn.discovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

public class DiscoveryServiceTests {

    private static final String IMPLEMENTATION_NAME = "HelloWorldServiceImplementation";
    private static final String IMPLEMENTATION_QUALIFIED_NAME = "%s.%s".formatted(DiscoveryServiceTests.class.getPackageName(), IMPLEMENTATION_NAME);
    private static final String HELLO_WORLD_MESSAGE = "Hello World!";
    private static final String HELLO_WORLD_SERVICE_IMPLEMENTATION_SOURCE = """
            package org.dockbox.hartshorn.discovery;
                        
            @ServiceLoader(HelloWorldService.class)
            public class HelloWorldServiceImplementation implements HelloWorldService {
                        
                @Override
                public String getHelloWorld() {
                    return "%s";
                }
            }
            """.formatted(HELLO_WORLD_MESSAGE);

    @Test
    void testProcessorCreatesValidDiscoveryFile() throws IOException {
        Map<Kind, List<JavaFileObject>> generatedByKind = compileAndGetGeneratedFiles();
        JavaFileObject discoFile = generatedByKind.get(Kind.OTHER).get(0);

        String servicePackageName = HelloWorldService.class.getPackageName();
        String outputLocation = StandardLocation.CLASS_OUTPUT.getName();
        String expectedName = "/%s/META-INF/%s.%s.disco".formatted(
                outputLocation, servicePackageName,
                HelloWorldService.class.getSimpleName());
        Assertions.assertEquals(expectedName, discoFile.getName());

        Reader reader = discoFile.openReader(false);
        BufferedReader in = new BufferedReader(reader);
        String discoFileContent = in.lines().collect(Collectors.joining());

        Assertions.assertEquals(IMPLEMENTATION_QUALIFIED_NAME, discoFileContent);
    }

    @Test
    void testDiscoveryServiceWithOverride() throws IOException {
        Map<Kind, List<JavaFileObject>> generatedByKind = compileAndGetGeneratedFiles();
        JavaFileObject implementationClassFile = generatedByKind.get(Kind.CLASS).get(0);

        // Class is compiled, but not loaded, so we need to load it manually.
        ByteClassLoader classLoader = new ByteClassLoader(
                new URL[]{},
                DiscoveryServiceTests.class.getClassLoader(),
                Collections.singletonMap(IMPLEMENTATION_QUALIFIED_NAME, implementationClassFile.openInputStream().readAllBytes()));

        DiscoveryService.instance().addClassLoader(classLoader);
        DiscoveryService.instance().override(HelloWorldService.class, IMPLEMENTATION_QUALIFIED_NAME);

        HelloWorldService helloWorldService = DiscoveryService.instance().discover(HelloWorldService.class);
        Assertions.assertEquals(HELLO_WORLD_MESSAGE, helloWorldService.getHelloWorld());
    }

    private static Map<Kind, List<JavaFileObject>> compileAndGetGeneratedFiles() {
        Compilation compilation = Compiler.javac()
                .withProcessors(new DiscoveryServiceProcessor())
                .compile(JavaFileObjects.forSourceString(IMPLEMENTATION_NAME, HELLO_WORLD_SERVICE_IMPLEMENTATION_SOURCE));
        Assertions.assertTrue(compilation.errors().isEmpty());

        ImmutableList<JavaFileObject> generatedFiles = compilation.generatedFiles();
        Assertions.assertEquals(2, generatedFiles.size());

        Map<Kind, List<JavaFileObject>> generatedByKind = generatedFiles.stream().collect(Collectors.groupingBy(JavaFileObject::getKind));
        Assertions.assertEquals(1, generatedByKind.get(Kind.CLASS).size()); // HelloWorldServiceImplementation.class
        Assertions.assertEquals(1, generatedByKind.get(Kind.OTHER).size()); // HelloWorldService.disco

        return generatedByKind;
    }

    public static class ByteClassLoader extends URLClassLoader {
        private final Map<String, byte[]> extraClassDefs;

        public ByteClassLoader(URL[] urls, ClassLoader parent, Map<String, byte[]> extraClassDefs) {
            super(urls, parent);
            this.extraClassDefs = Collections.unmodifiableMap(extraClassDefs);
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.get(name);
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }
    }
}
