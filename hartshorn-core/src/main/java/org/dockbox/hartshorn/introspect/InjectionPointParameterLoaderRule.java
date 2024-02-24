package org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

public class InjectionPointParameterLoaderRule implements ParameterLoaderRule<ApplicationBoundParameterLoaderContext> {

    private final ComponentRequestContext requestContext;

    public InjectionPointParameterLoaderRule(ComponentRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public boolean accepts(ParameterView<?> parameter, int index, ApplicationBoundParameterLoaderContext context, Object... args) {
        return requestContext.isForInjectionPoint() && parameter.type().isChildOf(InjectionPoint.class);
    }

    @Override
    public <T> Option<T> load(ParameterView<T> parameter, int index, ApplicationBoundParameterLoaderContext context, Object... args) {
        InjectionPoint injectionPoint = requestContext.injectionPoint();
        return Option.of(parameter.type().cast(injectionPoint));
    }
}
