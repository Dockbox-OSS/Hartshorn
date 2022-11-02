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

package org.dockbox.hartshorn.application.scan;

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;

import java.util.HashSet;
import java.util.Set;

public class ClassPathScannerTypeReferenceCollector extends ClasspathTypeReferenceCollector {

    public ClassPathScannerTypeReferenceCollector(final ApplicationEnvironment environment, final String packageName) {
        super(environment, packageName);
    }

    @Override
    protected Set<TypeReference> createCache() {
        final Set<TypeReference> typeReferences = new HashSet<>();
        final ClassPathScanner classpathScanner = ClassPathScanner.create();
        classpathScanner
                .filterBeginResourceName(this.packageName())
                .filterClassOnly()
                .scan(resource -> {
                    if (resource.isClassResource()) {
                        typeReferences.add(new ClassNameReference(resource.getResourceName()));
                    }
                });
        this.environment().log().debug("Located {} classes in package {} in {} seconds", typeReferences.size(), this.packageName(), (classpathScanner.scanTime() / 1000.0));
        return typeReferences;
    }
}
