/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.scan.classpath;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.introspect.scan.ClassNameReference;
import org.dockbox.hartshorn.util.introspect.scan.TypeCollectionException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link ClasspathTypeReferenceCollector} that collects {@link TypeReference}s from a classpath
 * using a {@link ClassPathScanner}. This automatically includes the default classpath, and filters
 * on the configured package name. Scanning does not include any non-class resources.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ClassPathScannerTypeReferenceCollector extends ClasspathTypeReferenceCollector {

    private final Set<String> excludePackages = new HashSet<>();

    public ClassPathScannerTypeReferenceCollector(Set<String> packageNames) {
        super(packageNames);
    }

    /**
     * Excludes the given package names from being scanned.
     *
     * @param packageNames The package names to exclude.
     * @return This collector, for chaining.
     */
    public ClassPathScannerTypeReferenceCollector excludePackages(Set<String> packageNames) {
        this.excludePackages.addAll(packageNames);
        return this;
    }

    /**
     * Returns the package names that are excluded from being scanned.
     *
     * @return The package names that are excluded from being scanned.
     */
    public Set<String> excludePackages() {
        return this.excludePackages;
    }

    @Override
    protected Set<TypeReference> createCache() throws TypeCollectionException {
        Set<TypeReference> typeReferences = new HashSet<>();
        ClassPathScanner classpathScanner = ClassPathScanner.create()
            .includeDefaultClassPath()
            .classesOnly();

        for (String packageName : this.packageNames()) {
            classpathScanner.includePrefix(packageName);
        }

        for (String excludePackage : this.excludePackages()) {
            classpathScanner.excludePrefix(excludePackage);
        }

        try {
            classpathScanner.scan(resource -> {
                if (resource.isClassResource()) {
                    typeReferences.add(new ClassNameReference(resource.resourceName()));
                }
            });
        }
        catch (ClassPathWalkingException e) {
            throw new TypeCollectionException(
                "Failed to collect types in packages " + this.packageNames(),
                e
            );
        }
        return typeReferences;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("packages")
            .writeStrings(this.packageNames().toArray(String[]::new));
    }
}
