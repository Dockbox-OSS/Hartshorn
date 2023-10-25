package org.dockbox.hartshorn.commands.arguments;

import java.util.ArrayList;
import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.CommandParameterResources;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

public abstract class AbstractParameterPattern implements CustomParameterPattern {

    @Override
    public <T> Attempt<T, ConverterException> request(Class<T> type, CommandSource source, String raw) {
        ApplicationContext context = source.applicationContext();
        TypeView<T> typeView = context.environment().introspector().introspect(type);

        Attempt<Boolean, ConverterException> preconditionsMatch = this.preconditionsMatch(type, source, raw);
        if (preconditionsMatch.errorPresent()) {
            context.log().debug("Preconditions yielded exception, rejecting raw argument " + raw);
            return Attempt.of(preconditionsMatch.error());
        }
        else if (Boolean.FALSE.equals(preconditionsMatch.orElse(false))) {
            context.log().debug("Preconditions failed, rejecting raw argument " + raw);
            return Attempt.empty();
        }

        List<String> rawArguments = this.splitArguments(raw);
        List<Class<?>> argumentTypes = new ArrayList<>();
        List<Object> arguments = new ArrayList<>();

        for (String rawArgument : rawArguments) {
            context.log().debug("Parsing raw argument " + rawArgument);
            Option<String> argumentIdentifier = this.parseIdentifier(rawArgument);
            if (argumentIdentifier.absent()) {
                context.log().debug("Could not determine argument identifier for raw argument '%s', this is not a error as the value likely needs to be looked up by its type instead.".formatted(rawArgument));
                // If a non-pattern argument is required, the converter needs to be looked up by type instead of by its identifier. This will be done when the constructor is being looked up
                argumentTypes.add(null);
                arguments.add(rawArgument);
                continue;
            }
            String typeIdentifier = argumentIdentifier.get();

            ContextKey<ArgumentConverterContext> argumentConverterContextKey = ContextKey.builder(ArgumentConverterContext.class)
                    .fallback(ArgumentConverterContext::new)
                    .build();
            Option<ArgumentConverter<?>> converter = context
                    .first(argumentConverterContextKey)
                    .flatMap(argumentConverterContext -> argumentConverterContext.converter(typeIdentifier));

            if (converter.absent()) {
                context.log().debug("Could not locate converter for identifier '%s'".formatted(typeIdentifier));
                return Attempt.of(new MissingConverterException(context, typeView));
            }

            context.log().debug("Found converter for identifier '%s'".formatted(typeIdentifier));
            argumentTypes.add(converter.get().type());
            arguments.add(converter.get().convert(source, rawArgument).orNull());
        }

        return this.constructor(argumentTypes, arguments, typeView, source)
                .flatMap(constructor -> constructor.create(arguments.toArray(new Object[0])))
                .attempt(ArgumentMatchingFailedException.class)
                // Inferred downcast from ArgumentMatchingFailedException to ConverterException
                .mapError(error -> error);
    }

    public abstract <T> Attempt<Boolean, ConverterException> preconditionsMatch(Class<T> type, CommandSource source, String raw);

    public abstract List<String> splitArguments(String raw);

    public abstract Attempt<String, ConverterException> parseIdentifier(String argument);

    protected <T> Attempt<ConstructorView<T>, ConverterException> constructor(List<Class<?>> argumentTypes, List<Object> arguments, TypeView<T> type, CommandSource source) {
        for (ConstructorView<T> constructor : type.constructors().all()) {
            if (constructor.parameters().count() != arguments.size()) {
                continue;
            }
            List<TypeView<?>> parameters = constructor.parameters().types();

            boolean passed = true;
            for (int i = 0; i < parameters.size(); i++) {
                TypeView<?> parameter = parameters.get(i);
                Class<?> argument = argumentTypes.get(i);

                if (argument == null) {
                    ContextKey<ArgumentConverterContext> argumentConverterContextKey = ContextKey.builder(ArgumentConverterContext.class)
                            .fallback(ArgumentConverterContext::new)
                            .build();

                    Option<? extends ArgumentConverter<?>> converter = source.applicationContext()
                            .first(argumentConverterContextKey)
                            .flatMap(context -> context.converter(parameter));

                    if (converter.present()) {
                        Option<?> result = converter.get().convert(source, (String) arguments.get(i));
                        if (result.present()) {
                            arguments.set(i, result.get());
                            continue; // Generic type, will be parsed later
                        }
                    }
                }
                else if (parameter.is(argument)) {
                    continue;
                }

                passed = false;
                break; // Parameter is not what we expected, do not continue
            }
            if (passed) {
                source.applicationContext().log().debug("Found matching constructor for " + type.name() + " with " + argumentTypes.size() + " arguments.");
                return Attempt.of(constructor);
            }
        }
        return Attempt.of(new ArgumentMatchingFailedException(source.applicationContext().get(CommandParameterResources.class).notEnoughArgs()));
    }
}
