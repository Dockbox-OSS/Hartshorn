package org.dockbox.hartshorn.gradle.javadoc;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.CompilationUnit.Storage;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.utils.SourceRoot.Callback.Result;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JavadocVerifier {

    public static void verify(final Path path) throws IOException {
        final SourceRoot sourceRoot = new SourceRoot(path);

        final Set<JavadocFault> faults = new HashSet<>();
        sourceRoot.parse("", (localPath, absolutePath, result) -> {
            if (result.getResult().isPresent()) {
                final CompilationUnit compilationUnit = result.getResult().get();
                compilationUnit.accept(new JavadocVerificationVisitor(), faults);
            }
            return Result.DONT_SAVE;
        });

        processFaults(faults);
    }

    private static void processFaults(final Set<JavadocFault> faults) {
        if (faults.isEmpty()) {
            return;
        }

        final Map<CompilationUnit, List<JavadocFault>> faultsByUnit = faults.stream()
                .collect(Collectors.groupingBy(JavadocFault::compilationUnit));

        for (final Entry<CompilationUnit, List<JavadocFault>> entry : faultsByUnit.entrySet()) {
            final List<JavadocFault> faultsForFile = entry.getValue();
            if (faultsForFile.isEmpty()) {
                continue;
            }

            final CompilationUnit unit = entry.getKey();
            final Optional<Storage> storage = unit.getStorage();
            if (storage.isEmpty()) {
                throw new IllegalStateException("No storage found for CompilationUnit");
            }

            final String fileName = storage.get().getFileName();
            System.err.printf("Found %s issue(s) in %s%n", faultsForFile.size(), fileName);
            for (final JavadocFault fault : faultsForFile) {
                System.err.printf("- At %s: %s%n", fault.name(), fault.message());
            }
        }

        throw new IllegalStateException("Found %s issues in %s files".formatted(faults.size(), faultsByUnit.size()));
    }

}
