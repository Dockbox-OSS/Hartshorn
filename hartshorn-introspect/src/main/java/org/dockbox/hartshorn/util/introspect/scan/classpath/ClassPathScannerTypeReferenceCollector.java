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

package org.dockbox.hartshorn.util.introspect.scan.classpath;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.introspect.scan.ClassNameReference;
import org.dockbox.hartshorn.util.introspect.scan.TypeCollectionException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;

import java.util.HashSet;
import java.util.Set;

public class ClassPathScannerTypeReferenceCollector extends ClasspathTypeReferenceCollector {

    public ClassPathScannerTypeReferenceCollector(final String packageName) {
        super(packageName);
    }

    @Override
    protected Set<TypeReference> createCache() throws TypeCollectionException {
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
            throw new TypeCollectionException("Failed to collect types in package " + this.packageName(), e);
        }
        return typeReferences;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        collector.property("package").write(this.packageName());
    }
}