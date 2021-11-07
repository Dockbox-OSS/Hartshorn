package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;

public class RuleBasedParameterLoader<C extends ParameterLoaderContext> extends ParameterLoader<C>{

    @Getter(AccessLevel.PROTECTED)
    private final Set<ParameterLoaderRule<C>> rules = HartshornUtils.emptyConcurrentSet();

    public RuleBasedParameterLoader add(final ParameterLoaderRule<C> rule) {
        this.rules.add(rule);
        return this;
    }

    @Override
    public List<Object> loadArguments(final C context, final Object... args) {
        final List<Object> arguments = HartshornUtils.emptyList();
        final LinkedList<ParameterContext<?>> parameters = context.method().parameters();
        parameters:
        for (int i = 0; i < parameters.size(); i++) {
            final ParameterContext<?> parameter = parameters.get(i);
            for (final ParameterLoaderRule<C> rule : this.rules) {
                if (rule.accepts(parameter, context, args)) {
                    final Exceptional<Object> argument = rule.load((ParameterContext<Object>) parameter, context, args);
                    arguments.add(argument.orNull());
                    continue parameters;
                }
            }
            arguments.add(this.loadDefault(parameter, i, context, args));
        }
        return arguments;
    }

    protected <T> T loadDefault(final ParameterContext<T> parameter, final int index, final C context, final Object... args) {
        return parameter.type().defaultOrNull();
    }
}
