package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.hsl.runtime.StandardLibraryHslRuntime;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@HartshornTest
public class PredefinedScriptTests {

    public static Stream<Arguments> scripts() {
        final Path resources = Paths.get("src", "test", "resources");
        return Arrays.stream(resources.toFile().listFiles())
                .filter(file -> file.getName().endsWith(".hsl"))
                .map(file -> {
                    try {
                        return Files.readAllLines(file.toPath());
                    }
                    catch (final IOException e) {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .map(lines -> {
                    return String.join("\n", lines);
                }).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("scripts")
    void testPredefinedScript(final String content) {
        final StandardLibraryHslRuntime runtime = new StandardLibraryHslRuntime();
        Assertions.assertDoesNotThrow(() -> runtime.run(content));
        Assertions.assertTrue(runtime.errors().isEmpty());
        Assertions.assertTrue(runtime.runtimeErrors().isEmpty());
    }
}
