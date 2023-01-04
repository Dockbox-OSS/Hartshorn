/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.CommandParameterResources;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * The type used to provide an argument pattern which can be used to construct types decorated with {@link Parameter}.
 */
public interface CustomParameterPattern {

    /**
     * Attempts to parse a {@code raw} argument into the requested {@code type}.
     *
     * @param type The target type to parse into
     * @param source The source of the command, provided in case the parser is context-sensitive
     * @param raw The raw argument
     * @param <T> The generic type of the target
     *
     * @return An instance of {@code T}, wrapped in a {@link Option}, or {@link Option#empty()} if {@code null}
     */
    default <T> Attempt<T, ConverterException> request(final Class<T> type, final CommandSource source, final String raw) {
        final ApplicationContext context = source.applicationContext();
        final TypeView<T> typeView = context.environment().introspect(type);

        final Attempt<Boolean, ConverterException> preconditionsMatch = this.preconditionsMatch(type, source, raw);
        if (preconditionsMatch.errorPresent()) {
            context.log().debug("Preconditions yielded exception, rejecting raw argument " + raw);
            return Attempt.of(preconditionsMatch.error());
        }
        else if (Boolean.FALSE.equals(preconditionsMatch.orElse(false))) {
            context.log().debug("Preconditions failed, rejecting raw argument " + raw);
            return Attempt.empty();
        }

        final List<String> rawArguments = this.splitArguments(raw);
        final List<Class<?>> argumentTypes = new ArrayList<>();
        final List<Object> arguments = new ArrayList<>();

        for (final String rawArgument : rawArguments) {
            context.log().debug("Parsing raw argument " + rawArgument);
            final Option<String> argumentIdentifier = this.parseIdentifier(rawArgument);
            if (argumentIdentifier.absent()) {
                context.log().debug("Could not determine argument identifier for raw argument '%s', this is not a error as the value likely needs to be looked up by its type instead.".formatted(rawArgument));
                // If a non-pattern argument is required, the converter needs to be looked up by type instead of by its identifier. This will be done when the constructor is being looked up
                argumentTypes.add(null);
                arguments.add(rawArgument);
                continue;
            }
            final String typeIdentifier = argumentIdentifier.get();

            final ContextKey<ArgumentConverterContext> argumentConverterContextKey = ContextKey.builder(ArgumentConverterContext.class)
                    .fallback(ArgumentConverterContext::new)
                    .build();
            final Option<ArgumentConverter<?>> converter = context
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

    <T> Attempt<Boolean, ConverterException> preconditionsMatch(Class<T> type, CommandSource source, String raw);

    List<String> splitArguments(String raw);

    Attempt<String, ConverterException> parseIdentifier(String argument);

    default <T> Attempt<ConstructorView<T>, ConverterException> constructor(final List<Class<?>> argumentTypes, final List<Object> arguments, final TypeView<T> type, final CommandSource source) {
        for (final ConstructorView<T> constructor : type.constructors().all()) {
            if (constructor.parameters().count() != arguments.size()) continue;
            final List<TypeView<?>> parameters = constructor.parameters().types();

            boolean passed = true;
            for (int i = 0; i < parameters.size(); i++) {
                final TypeView<?> parameter = parameters.get(i);
                final Class<?> argument = argumentTypes.get(i);

                if (argument == null) {
                    final ContextKey<ArgumentConverterContext> argumentConverterContextKey = ContextKey.builder(ArgumentConverterContext.class)
                            .fallback(ArgumentConverterContext::new)
                            .build();

                    final Option<? extends ArgumentConverter<?>> converter = source.applicationContext()
                            .first(argumentConverterContextKey)
                            .flatMap(context -> context.converter(parameter));

                    if (converter.present()) {
                        final Option<?> result = converter.get().convert(source, (String) arguments.get(i));
                        if (result.present()) {
                            arguments.set(i, result.get());
                            continue; // Generic type, will be parsed later
                        }
                    }
                }
                else if (parameter.is(argument)) continue;

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
