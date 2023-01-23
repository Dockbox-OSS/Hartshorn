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
        final Set<JavadocFault> faults = collectFaults(path);
        processFaults(faults);
    }

    public static Set<JavadocFault> collectFaults(final Path path) throws IOException {
        final SourceRoot sourceRoot = new SourceRoot(path);

        final Set<JavadocFault> faults = new HashSet<>();
        sourceRoot.parse("", (localPath, absolutePath, result) -> {
            if (result.getResult().isPresent()) {
                final CompilationUnit compilationUnit = result.getResult().get();
                compilationUnit.accept(new JavadocVerificationVisitor(), faults);
            }
            return Result.DONT_SAVE;
        });

        return faults;
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
