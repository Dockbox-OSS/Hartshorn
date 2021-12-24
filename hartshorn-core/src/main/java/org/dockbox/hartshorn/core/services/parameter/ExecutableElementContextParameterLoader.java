package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.inject.Enable;
import org.dockbox.hartshorn.core.annotations.inject.Required;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import javax.inject.Named;

public class ExecutableElementContextParameterLoader extends RuleBasedParameterLoader<ParameterLoaderContext> {

    public ExecutableElementContextParameterLoader() {
        this.add(new ContextParameterLoaderRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterContext<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        final Named named = parameter.annotation(Named.class).orNull();
        final Key<T> key = Key.of(parameter.type(), named);
        final boolean enable = parameter.annotation(Enable.class).map(Enable::value).or(true);
        final T out = context.applicationContext().get(key, enable);

        final boolean required = parameter.annotation(Required.class).map(Required::value).or(false);
        if (required && out == null) return ExceptionHandler.unchecked(new ApplicationException("Parameter " + parameter.name() + " on " + parameter.declaredBy().qualifiedName() + " is required"));

        return out;
    }
}
