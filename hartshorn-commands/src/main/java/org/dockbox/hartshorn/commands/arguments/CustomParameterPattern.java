/*
 * Copyright 2019-2022 the original author or authors.
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
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.ArrayList;
import java.util.LinkedList;
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
     * @return An instance of {@code T}, wrapped in a {@link Result}, or {@link Result#empty()} if {@code null}
     */
    default <T> Result<T> request(final TypeContext<T> type, final CommandSource source, final String raw) {
        final ApplicationContext context = source.applicationContext();
        final Result<Boolean> preconditionsMatch = this.preconditionsMatch(type, source, raw);
        if (preconditionsMatch.caught()) {
            context.log().debug("Preconditions yielded exception, rejecting raw argument " + raw);
            return Result.of(preconditionsMatch.error());
        }
        else if (Boolean.FALSE.equals(preconditionsMatch.or(false))) {
            context.log().debug("Preconditions failed, rejecting raw argument " + raw);
            return Result.empty();
        }

        final List<String> rawArguments = this.splitArguments(raw);
        final List<TypeContext<?>> argumentTypes = new ArrayList<>();
        final List<Object> arguments = new ArrayList<>();

        for (final String rawArgument : rawArguments) {
            context.log().debug("Parsing raw argument " + rawArgument);
            final Result<String> argumentIdentifier = this.parseIdentifier(rawArgument);
            if (argumentIdentifier.absent()) {
                context.log().debug("Could not determine argument identifier for raw argument '%s', this is not a error as the value likely needs to be looked up by its type instead.".formatted(rawArgument));
                // If a non-pattern argument is required, the converter needs to be looked up by type instead of by its identifier. This will be done when the constructor is being looked up
                argumentTypes.add(null);
                arguments.add(rawArgument);
                continue;
            }
            final String typeIdentifier = argumentIdentifier.get();

            final Result<ArgumentConverter<?>> converter = context
                    .first(ArgumentConverterContext.class)
                    .flatMap(argumentConverterContext -> argumentConverterContext.converter(typeIdentifier));

            if (converter.absent()) {
                context.log().debug("Could not locate converter for identifier '%s'".formatted(typeIdentifier));
                return Result.of(new MissingConverterException(context, type)
                );
            }

            context.log().debug("Found converter for identifier '%s'".formatted(typeIdentifier));
            argumentTypes.add(converter.get().type());
            arguments.add(converter.get().convert(source, rawArgument).orNull());
        }

        return this.constructor(argumentTypes, arguments, type, source)
                .flatMap(constructor -> constructor.createInstance(arguments.toArray(new Object[0])));
    }

    <T> Result<Boolean> preconditionsMatch(TypeContext<T> type, CommandSource source, String raw);

    List<String> splitArguments(String raw);

    Result<String> parseIdentifier(String argument);

    default <T> Result<ConstructorContext<T>> constructor(final List<TypeContext<?>> argumentTypes, final List<Object> arguments, final TypeContext<T> type, final CommandSource source) {
        for (final ConstructorContext<T> constructor : type.constructors()) {
            if (constructor.parameterCount() != arguments.size()) continue;
            final LinkedList<TypeContext<?>> parameters = constructor.parameterTypes();

            boolean passed = true;
            for (int i = 0; i < parameters.size(); i++) {
                final TypeContext<?> parameter = parameters.get(i);
                final TypeContext<?> argument = argumentTypes.get(i);

                if (argument == null) {
                    final Result<? extends ArgumentConverter<?>> converter = source.applicationContext()
                            .first(ArgumentConverterContext.class)
                            .flatMap(context -> context.converter(parameter));
                    if (converter.present()) {
                        final Result<?> result = converter.get().convert(source, (String) arguments.get(i));
                        if (result.present()) {
                            arguments.set(i, result.get());
                            continue; // Generic type, will be parsed later
                        }
                    }
                }
                else if (parameter.equals(argument)) continue;

                passed = false;
                break; // Parameter is not what we expected, do not continue
            }
            if (passed) {
                source.applicationContext().log().debug("Found matching constructor for " + type.name() + " with " + argumentTypes.size() + " arguments.");
                return Result.of(constructor);
            }
        }
        return Result.of(new ArgumentMatchingFailedException(source.applicationContext().get(CommandParameterResources.class).notEnoughArgs()));
    }
}
