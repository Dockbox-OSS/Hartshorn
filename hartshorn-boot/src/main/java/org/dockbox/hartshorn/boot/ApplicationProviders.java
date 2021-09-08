package org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.di.annotations.context.LogExclude;
import org.dockbox.hartshorn.di.annotations.inject.Provider;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.slf4j.Logger;

@Service
@LogExclude
public class ApplicationProviders {

    @Provider
    public Logger logger(final ApplicationContext context) {
        return context.log();
    }

}
