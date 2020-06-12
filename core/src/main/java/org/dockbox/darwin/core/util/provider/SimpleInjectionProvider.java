package org.dockbox.darwin.core.util.provider;

import com.google.inject.AbstractModule;

import org.dockbox.darwin.core.util.exceptions.ExceptionHelper;
import org.dockbox.darwin.core.util.exceptions.SimpleExceptionHelper;
import org.dockbox.darwin.core.util.module.ModuleLoader;
import org.dockbox.darwin.core.util.module.ModuleScanner;
import org.dockbox.darwin.core.util.module.SimpleModuleLoader;
import org.dockbox.darwin.core.util.module.SimpleModuleScanner;

public class SimpleInjectionProvider extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
        bind(ModuleScanner.class).to(SimpleModuleScanner.class);
        bind(ModuleLoader.class).to(SimpleModuleLoader.class);
    }
}
