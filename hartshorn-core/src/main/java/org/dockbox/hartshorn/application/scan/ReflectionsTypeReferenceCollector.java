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
import org.reflections.Reflections;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionsTypeReferenceCollector extends ClasspathTypeReferenceCollector {

    public ReflectionsTypeReferenceCollector(final ApplicationEnvironment environment, final String packageName) {
        super(environment, packageName);
    }

    @Override
    protected Set<TypeReference> createCache() {
        final FilterBuilder inputsFilter = new FilterBuilder();
        inputsFilter.includePackage(this.packageName());

        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage(this.packageName())
                .setInputsFilter(inputsFilter)
                .setScanners(new TypeElementsScanner())
                .setExpandSuperTypes(false)
        );
        return reflections.getStore().get(TypeElementsScanner.class.getSimpleName())
                .keySet().stream()
                .map(ClassNameReference::new)
                .collect(Collectors.toSet());
    }
}
