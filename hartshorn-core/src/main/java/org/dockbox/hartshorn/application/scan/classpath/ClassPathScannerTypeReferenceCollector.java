/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.application.scan.classpath;

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.scan.ClassNameReference;
import org.dockbox.hartshorn.application.scan.TypeReference;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassPathScannerTypeReferenceCollector extends ClasspathTypeReferenceCollector {

    public ClassPathScannerTypeReferenceCollector(final ApplicationEnvironment environment, final String packageName) {
        super(environment, packageName);
    }

    @Override
    protected Set<TypeReference> createCache() {
        final Set<TypeReference> typeReferences = new HashSet<>();
        final ClassPathScanner classpathScanner = ClassPathScanner.create()
                .includeDefaultClassPath()
                .filterPrefix(this.packageName())
                .classesOnly();

        try {
            classpathScanner.scan(resource -> {
                if(resource.isClassResource()) {
                    typeReferences.add(new ClassNameReference(resource.resourceName()));
                }
            });
        }
        catch(final ClassPathWalkingException e) {
            this.environment().handle("Could not scan classpath for types", e);
            return Collections.emptySet();
        }

        this.environment().log().debug("Located {} classes in package {} in {} seconds", typeReferences.size(), this.packageName(), (classpathScanner.scanTime() / 1000.0));
        return typeReferences;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        collector.property("package").write(this.packageName());
    }
}
