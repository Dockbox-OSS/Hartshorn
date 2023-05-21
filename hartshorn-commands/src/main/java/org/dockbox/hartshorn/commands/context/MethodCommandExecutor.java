package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.CommandExecutor;
import org.dockbox.hartshorn.commands.CommandResources;
import org.dockbox.hartshorn.commands.arguments.CommandParameterLoaderContext;
import org.dockbox.hartshorn.commands.events.CommandEvent;
import org.dockbox.hartshorn.commands.events.CommandEvent.Before;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.condition.ProvidedParameterContext;
import org.dockbox.hartshorn.events.parents.Cancellable;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.util.List;

public class MethodCommandExecutor<T> implements CommandExecutor {
    private final ConditionMatcher conditionMatcher;
    private final MethodCommandExecutorContext<T> executorContext;
    private final ApplicationContext applicationContext;
    private final MethodView<T, ?> method;

    public MethodCommandExecutor(final ConditionMatcher conditionMatcher, final MethodCommandExecutorContext<T> context) {
        this.conditionMatcher = conditionMatcher;
        this.executorContext = context;
        this.applicationContext = context.applicationContext();
        this.method = context.method();
    }

    @Override
    public void execute(final CommandContext commandContext) {
        final Cancellable before = new Before(commandContext.source(), commandContext).with(this.applicationContext).post();
        if (before.cancelled()) {
            this.applicationContext.log().debug("Execution cancelled for " + this.method.qualifiedName());
            final Message cancelled = this.applicationContext.get(CommandResources.class).cancelled();
            commandContext.source().send(cancelled);
            return;
        }

        final T instance = this.applicationContext.get(this.executorContext.key());
        final CommandParameterLoaderContext loaderContext = new CommandParameterLoaderContext(this.method, null, this.applicationContext, commandContext, this.executorContext);
        final List<Object> arguments = this.executorContext.parameterLoader().loadArguments(loaderContext);

        if (this.conditionMatcher.match(this.method, ProvidedParameterContext.of(this.method, arguments))) {
            this.applicationContext.log().debug("Invoking command method %s with %d arguments".formatted(this.method.qualifiedName(), arguments.size()));
            this.method.invoke(instance, arguments.toArray())
                    .peekError(error -> this.applicationContext.handle("Encountered unexpected error while performing command executor", error));
            new CommandEvent.After(commandContext.source(), commandContext).with(this.applicationContext).post();
        }
        else {
            this.applicationContext.log().debug("Conditions didn't match for " + this.method.qualifiedName());
            final Message cancelled = this.applicationContext.get(CommandResources.class).cancelled();
            commandContext.source().send(cancelled);
        }
    }
}
