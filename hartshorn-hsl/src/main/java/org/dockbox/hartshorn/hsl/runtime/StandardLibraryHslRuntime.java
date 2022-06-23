package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.callable.ApplicationBoundNativeModule;
import org.dockbox.hartshorn.hsl.callable.NativeModule;
import org.dockbox.hartshorn.hsl.modules.MathLibrary;
import org.dockbox.hartshorn.hsl.modules.SystemLibrary;

import java.util.Map;

public class StandardLibraryHslRuntime extends AbstractHslRuntime {

    public StandardLibraryHslRuntime(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected Map<String, NativeModule> standardLibraries() {
        return Map.of(
                "math", new ApplicationBoundNativeModule(MathLibrary.class, this.applicationContext()),
                "system", new ApplicationBoundNativeModule(SystemLibrary.class, this.applicationContext())
        );
    }
}
