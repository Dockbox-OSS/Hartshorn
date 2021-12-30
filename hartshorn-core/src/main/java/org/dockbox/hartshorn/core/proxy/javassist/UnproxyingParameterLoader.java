package org.dockbox.hartshorn.core.proxy.javassist;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.services.parameter.RuleBasedParameterLoader;

public class UnproxyingParameterLoader extends RuleBasedParameterLoader<ParameterLoaderContext> {

    public UnproxyingParameterLoader() {
        this.add(new UnproxyParameterLoaderRule());
        this.add(new ObjectEqualsParameterLoaderRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterContext<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        return (T) args[index];
    }
}
