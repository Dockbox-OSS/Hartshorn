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

package test.org.dockbox.hartshorn.gradle.javadoc;

import org.dockbox.hartshorn.gradle.javadoc.JavadocFault;
import org.dockbox.hartshorn.gradle.javadoc.JavadocVerifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JavadocVerifierTests {

    @Test
    void testValidSourceHasNoFaults() throws IOException {
        final Set<JavadocFault> faults = this.collectFaults("ValidSource.java");

        Assertions.assertTrue(faults.isEmpty());
    }

    @Test
    void testUndocumentedClassHasFaults() throws IOException {
        final Set<JavadocFault> faults = this.collectFaults("UndocumentedSource.java");

        Assertions.assertFalse(faults.isEmpty());
        Assertions.assertEquals(1, faults.size());

        final JavadocFault first = faults.iterator().next();

        Assertions.assertEquals("UndocumentedSource", first.name());
        Assertions.assertEquals("Missing Javadoc comment", first.message());
    }

    @Test
    void testUndocumentedMethodInDocumentedClassHasFaults() throws IOException {
        final Set<JavadocFault> faults = this.collectFaults("UndocumentedMethodSource.java");

        Assertions.assertFalse(faults.isEmpty());
        Assertions.assertEquals(1, faults.size());

        final JavadocFault first = faults.iterator().next();

        Assertions.assertEquals("undocumentedMethod", first.name());
        Assertions.assertEquals("Missing Javadoc comment", first.message());
    }

    @Test
    void testMissingTagsInDocumentedClassHasFaults() throws IOException {
        final Set<JavadocFault> faults = this.collectFaults("MissingTagSource.java");

        Assertions.assertFalse(faults.isEmpty());
        Assertions.assertEquals(2, faults.size());

        final List<String> expectedMessages = Arrays.asList(
                "Missing author tag in javadoc",
                "Missing since tag in javadoc"
        );
        final List<String> actualMessages = new ArrayList<>();

        for (final JavadocFault fault : faults) {
            Assertions.assertEquals("MissingTagSource", fault.name());
            Assertions.assertTrue(expectedMessages.contains(fault.message()));
            actualMessages.add(fault.message());
        }

        Assertions.assertTrue(actualMessages.containsAll(expectedMessages));
        // Ensure there are no additional messages
        Assertions.assertEquals(expectedMessages.size(), actualMessages.size());
    }

    @Test
    void testMissingDeprecationInDocumentedClassHasFaults() throws IOException {
        final Set<JavadocFault> faults = this.collectFaults("MissingDeprecationTagSource.java");

        Assertions.assertFalse(faults.isEmpty());
        Assertions.assertEquals(1, faults.size());

        final JavadocFault first = faults.iterator().next();

        Assertions.assertEquals("MissingDeprecationTagSource", first.name());
        Assertions.assertEquals("Missing deprecated tag in javadoc", first.message());
    }

    private Set<JavadocFault> collectFaults(final String fileName) throws IOException {
        final Path path = Path.of("src/test/resources");
        final Set<JavadocFault> faults = JavadocVerifier.collectFaults(path);
        return faults.stream()
                .filter(fault -> fault.compilationUnit().getStorage()
                        .map(storage -> storage.getFileName().equals(fileName))
                        .orElse(false)
                )
                .collect(Collectors.toSet());
    }
}
