package org.dockbox.darwin.sponge.util.inject;

import org.dockbox.darwin.core.util.inject.AbstractModuleInjector;
import org.dockbox.darwin.core.util.module.ModuleLoader;
import org.dockbox.darwin.core.util.module.ModuleScanner;
import org.dockbox.darwin.core.util.module.SimpleModuleLoader;
import org.dockbox.darwin.core.util.module.SimpleModuleScanner;

public class SpongeModuleInjector extends AbstractModuleInjector {

    @Override
    protected void configure() {
        bind(ModuleLoader.class).to(SimpleModuleLoader.class);
        bind(ModuleScanner.class).to(SimpleModuleScanner.class);
    }

}
