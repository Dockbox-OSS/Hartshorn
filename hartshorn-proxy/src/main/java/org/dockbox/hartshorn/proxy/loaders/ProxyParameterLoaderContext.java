package org.dockbox.hartshorn.proxy.loaders;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ProxyParameterLoaderContext extends ParameterLoaderContext {

    private final ApplicationProxier applicationProxier;

    public ProxyParameterLoaderContext(final ExecutableElementView<?> executable, final TypeView<?> type, final Object instance,
                                       final ApplicationContext applicationContext, final ApplicationProxier applicationProxier) {
        super(executable, type, instance, applicationContext);
        this.applicationProxier = applicationProxier;
    }

    public ApplicationProxier applicationProxier() {
        return this.applicationProxier;
    }
}
