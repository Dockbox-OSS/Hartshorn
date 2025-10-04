package org.dockbox.hartshorn.hsl.modules;

import org.dockbox.hartshorn.launchpad.ApplicationContext;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class UtilityClassNativeModule extends AbstractNativeModule {

    private final Class<?> utilityClass;
    private final ApplicationContext applicationContext;

    public UtilityClassNativeModule(Class<?> utilityClass, ApplicationContext applicationContext) {
        this.utilityClass = utilityClass;
        this.applicationContext = applicationContext;
    }

    @Override
    protected Class<?> moduleClass() {
        return this.utilityClass;
    }

    @Override
    protected Object instance() {
        return null;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
