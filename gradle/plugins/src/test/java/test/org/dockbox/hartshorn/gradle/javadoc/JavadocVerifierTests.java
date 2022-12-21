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
        final Set<JavadocFault> faults = collectFaults("ValidSource.java");

        Assertions.assertTrue(faults.isEmpty());
    }

    @Test
    void testUndocumentedClassHasFaults() throws IOException {
        final Set<JavadocFault> faults = collectFaults("UndocumentedSource.java");

        Assertions.assertFalse(faults.isEmpty());
        Assertions.assertEquals(1, faults.size());

        JavadocFault first = faults.iterator().next();

        Assertions.assertEquals("UndocumentedSource", first.name());
        Assertions.assertEquals("Missing Javadoc comment", first.message());
    }

    @Test
    void testUndocumentedMethodInDocumentedClassHasFaults() throws IOException {
        Set<JavadocFault> faults = collectFaults("UndocumentedMethodSource.java");

        Assertions.assertFalse(faults.isEmpty());
        Assertions.assertEquals(1, faults.size());

        JavadocFault first = faults.iterator().next();

        Assertions.assertEquals("undocumentedMethod", first.name());
        Assertions.assertEquals("Missing Javadoc comment", first.message());
    }

    @Test
    void testMissingTagsInDocumentedClassHasFaults() throws IOException {
        Set<JavadocFault> faults = collectFaults("MissingTagSource.java");

        Assertions.assertFalse(faults.isEmpty());
        Assertions.assertEquals(2, faults.size());

        List<String> expectedMessages = Arrays.asList(
                "Missing author tag in javadoc",
                "Missing since tag in javadoc"
        );
        List<String> actualMessages = new ArrayList<>();

        for (JavadocFault fault : faults) {
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
        Set<JavadocFault> faults = collectFaults("MissingDeprecationTagSource.java");

        Assertions.assertFalse(faults.isEmpty());
        Assertions.assertEquals(1, faults.size());

        JavadocFault first = faults.iterator().next();

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
