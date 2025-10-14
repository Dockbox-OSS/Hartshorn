package org.dockbox.hartshorn.launchpad.launch;

import org.dockbox.hartshorn.launchpad.resources.Resources;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.introspect.scan.ClassNameReference;
import org.dockbox.hartshorn.util.introspect.scan.TypeCollectionException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link TypeReferenceCollector} that collects type references from resource configuration files.
 * These files are expected to be located at "META-INF/hartshorn.environment.types" and contain
 * fully qualified class names, one per line.
 *
 * <p>This collector serves as a convenient way to gather type references for environment configurations
 * without requiring classpath scanning, which can be costly in terms of performance.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ResourceConfigurationTypeReferenceCollector implements TypeReferenceCollector {

    @Override
    public Set<TypeReference> collect() throws TypeCollectionException {
        try {
            Set<InputStream> resourceConfigurations = Resources.getResourcesAsInputStreams("META-INF/hartshorn.environment.types");
            Set<TypeReference> typeReferences = new HashSet<>();
            for (InputStream resourceConfiguration : resourceConfigurations) {
                typeReferences.addAll(this.collectConfigurationTypes(resourceConfiguration));
            }
            return typeReferences;
        }
        catch (final IOException e) {
            throw new TypeCollectionException("Could not access resource configurations", e);
        }
    }

    private List<TypeReference> collectConfigurationTypes(InputStream resourceConfiguration) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceConfiguration));
        return reader.lines()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(ClassNameReference::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {

    }
}
