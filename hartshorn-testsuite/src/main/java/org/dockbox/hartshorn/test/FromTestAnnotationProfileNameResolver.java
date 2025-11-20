/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.test;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dockbox.hartshorn.profiles.ProfileNameResolver;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.test.annotations.TestProfiles;

/**
 * A {@link ProfileNameResolver} that resolves profile names from {@link TestProfiles} annotations
 * present on the provided test component sources.
 *
 * @param testComponentSources the list of annotated elements to scan for {@link TestProfiles}
 * annotations
 *
 * @author Guus Lieben
 * @see TestProfiles
 * @since 0.7.0
 */
public record FromTestAnnotationProfileNameResolver(
    List<AnnotatedElement> testComponentSources
) implements ProfileNameResolver {

    @Override
    public SequencedSet<String> resolveProfileNames(PropertyRegistry rootRegistry) {
        return this.testComponentSources.stream()
            .filter(element -> element.isAnnotationPresent(TestProfiles.class))
            .map(element -> element.getAnnotation(TestProfiles.class))
            .map(TestProfiles::value)
            .flatMap(Stream::of)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
