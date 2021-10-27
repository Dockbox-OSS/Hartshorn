package org.dockbox.hartshorn.di.services;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.annotations.inject.Context;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.ExecutableElementContext;
import org.dockbox.hartshorn.di.context.element.FieldContext;
import org.dockbox.hartshorn.di.context.element.ParameterContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.context.element.TypedElementContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentContextInjectionProcessor extends ComponentValidator<Service>{

    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        for (final FieldContext<?> field : type.fields(Context.class))
            this.validate(field, type);

        final List<ExecutableElementContext<?>> constructors = type.injectConstructors().stream().map(c -> (ExecutableElementContext<?>) c).collect(Collectors.toList());
        final List<ExecutableElementContext<?>> methods = type.flatMethods().stream().map(m -> (ExecutableElementContext<?>) m).collect(Collectors.toList());
        final Collection<ExecutableElementContext<?>> executables = HartshornUtils.merge(constructors, methods);

        for (final ExecutableElementContext<?> executable : executables)
            for (final ParameterContext<?> parameter : executable.parameters(Context.class))
                this.validate(parameter, type);
    }

    private void validate(final TypedElementContext<?> context, final TypeContext<?> parent) {
        if (!context.type().childOf(org.dockbox.hartshorn.di.context.Context.class))
            throw new ApplicationException("%s is annotated with %s but does not extend %s".formatted(
                    context.qualifiedName(),
                    Context.class.getSimpleName(),
                    org.dockbox.hartshorn.di.context.Context.class.getSimpleName())
            ).runtime();
    }
}
