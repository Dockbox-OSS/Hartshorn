package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.proxy.Instance;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

@Binds(value = ParameterLoader.class, named = @Named("global_proxy"))
public class GlobalProxyParameterLoader extends ParameterLoader<ParameterLoaderContext> {

    @Override
    public List<Object> loadArguments(final ParameterLoaderContext context, final Object... args) {
        final MethodContext<?, ?> method = context.method();
        final List<Object> arguments = HartshornUtils.emptyList();
        if (method.parameterCount() >= 1 && method.parameters().get(0).annotation(Instance.class).present()) {
            arguments.add(context.instance());
        }
        arguments.addAll(Arrays.asList(args));
        return arguments;
    }
}
