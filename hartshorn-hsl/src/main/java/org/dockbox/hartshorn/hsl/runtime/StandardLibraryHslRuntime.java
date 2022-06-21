package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.modules.MathLibrary;
import org.dockbox.hartshorn.hsl.modules.SystemLibrary;

import java.util.Map;

public class StandardLibraryHslRuntime extends AbstractHslRuntime {

    @Override
    protected Map<String, Class<?>> getLibraries() {
        return Map.of(
                "math", MathLibrary.class,
                "system", SystemLibrary.class
        );
    }
}
