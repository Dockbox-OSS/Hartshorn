package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.List;
import java.util.Set;

public class RuleBasedParameterLoader<C extends ParameterLoaderContext> extends ParameterLoader<C>{

    private final Set<ParameterLoaderRule<C>> rules = HartshornUtils.emptyConcurrentSet();

    public RuleBasedParameterLoader add(final ParameterLoaderRule<C> rule) {
        this.rules.add(rule);
        return this;
    }

    @Override
    public List<Object> loadArguments(final C context, final Object... args) {
        final List<Object> arguments = HartshornUtils.emptyList();
        parameters:
        for (final ParameterContext<?> parameter : context.method().parameters()) {
            for (final ParameterLoaderRule<C> rule : this.rules) {
                if (rule.accepts(parameter, context, args)) {
                    final Exceptional<Object> argument = rule.load((ParameterContext<Object>) parameter, context, args);
                    arguments.add(argument.orNull());
                    continue parameters;
                }
            }
            arguments.add(null);
        }
        return arguments;
    }
}
