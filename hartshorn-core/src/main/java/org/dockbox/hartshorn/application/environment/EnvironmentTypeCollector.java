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

package org.dockbox.hartshorn.application.environment;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.util.introspect.scan.ClassReferenceLoadException;
import org.dockbox.hartshorn.util.introspect.scan.TypeCollectionException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollectorContext;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.NotNull;

public class EnvironmentTypeCollector {

    private final ApplicationEnvironment environment;

    public EnvironmentTypeCollector(ApplicationEnvironment environment) {
        ConditionMatcher matcher = new ConditionMatcher(environment.applicationContext());
        this.environment = environment;
    }

    public <T> Collection<TypeView<? extends T>> types(Predicate<TypeView<?>> predicate) {
        Option<TypeReferenceCollectorContext> collectorContext = environment.applicationContext().first(TypeReferenceCollectorContext.class);
        if (collectorContext.absent()) {
            this.environment.log().warn("TypeReferenceCollectorContext not available, falling back to no-op type lookup");
            return Collections.emptyList();
        }
        return collectTypes(predicate, collectorContext);
    }

    @NotNull
    private <T> Collection<TypeView<? extends T>> collectTypes(Predicate<TypeView<?>> predicate,
            Option<TypeReferenceCollectorContext> collectorContext) {
        try {
            Set<TypeReference> references = collectorContext.get().collector().collect();
            Collection<Class<?>> classes = this.loadClasses(references);
            return classes.stream()
                    .map(environment.introspector()::introspect)
                    .filter(predicate)
                    .map(reference -> (TypeView<T>) reference)
                    .collect(Collectors.toSet());
        }
        catch (TypeCollectionException e) {
            environment.handle(e);
            return Collections.emptyList();
        }
    }

    private Collection<Class<?>> loadClasses(Collection<TypeReference> references) {
        return references.stream()
                .map(reference -> {
                    try {
                        return reference.getOrLoad();
                    }
                    catch (ClassReferenceLoadException e) {
                        environment.handle(e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
