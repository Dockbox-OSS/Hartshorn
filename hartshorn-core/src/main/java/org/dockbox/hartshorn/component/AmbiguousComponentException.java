package org.dockbox.hartshorn.component;

import java.util.Set;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;

public class AmbiguousComponentException extends ApplicationRuntimeException {
    public AmbiguousComponentException(ComponentKey<?> lookupKey, Set<ComponentKey<?>> foundKeys) {
        super(
            "Ambiguous component lookup for key " + lookupKey
                + ". Found " + foundKeys.size() + " components: " + foundKeys.stream()
                .map(key -> key.qualifiedName(true))
                .collect(Collectors.joining(", ")));
    }
}
