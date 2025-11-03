package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.profiles.ProfileNameResolver;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.test.annotations.TestProfiles;

import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link ProfileNameResolver} that resolves profile names from {@link TestProfiles}
 * annotations present on the provided test component sources.
 *
 * @param testComponentSources the list of annotated elements to scan for {@link TestProfiles} annotations
 *
 * @see TestProfiles
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public record FromTestAnnotationProfileNameResolver(
        List<AnnotatedElement> testComponentSources
) implements ProfileNameResolver {

    @Override
    public Set<String> resolveProfileNames(PropertyRegistry rootRegistry) {
        return this.testComponentSources.stream()
                .filter(element -> element.isAnnotationPresent(TestProfiles.class))
                .map(element -> element.getAnnotation(TestProfiles.class))
                .map(TestProfiles::value)
                .flatMap(Stream::of)
                .collect(Collectors.toSet());
    }
}
