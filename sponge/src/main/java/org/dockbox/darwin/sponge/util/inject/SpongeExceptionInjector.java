package org.dockbox.darwin.sponge.util.inject;

import org.dockbox.darwin.core.util.exceptions.ExceptionHelper;
import org.dockbox.darwin.core.util.exceptions.SimpleExceptionHelper;
import org.dockbox.darwin.core.util.inject.AbstractExceptionInjector;

public class SpongeExceptionInjector extends AbstractExceptionInjector {

    @Override
    protected void configure() {
        bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
    }

}
