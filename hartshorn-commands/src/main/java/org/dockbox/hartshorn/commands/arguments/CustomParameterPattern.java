/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.CommandParameterResources;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.di.context.element.ConstructorContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * The type used to provide a argument pattern which can be used to construct types decorated with {@link Parameter}.
 */
public interface CustomParameterPattern {

    /**
     * Attempts to parse a {@code raw} argument into the requested {@code type}.
     *
     * @param type
     *         The target type to parse into
     * @param source
     *         The source of the command, provided in case the parser is context sensitive
     * @param raw
     *         The raw argument
     * @param <T>
     *         The generic type of the target
     *
     * @return An instance of {@code T}, wrapped in a {@link Exceptional}, or {@link Exceptional#empty()} if {@code null}
     */
    default <T> Exceptional<T> request(final TypeContext<T> type, final CommandSource source, final String raw) {
        final Exceptional<Boolean> preconditionsMatch = this.preconditionsMatch(type, source, raw);
        if (preconditionsMatch.caught()) return Exceptional.of(preconditionsMatch.error());

        final List<String> rawArguments = this.splitArguments(raw);
        final List<TypeContext<?>> argumentTypes = HartshornUtils.emptyList();
        final List<Object> arguments = HartshornUtils.emptyList();

        for (final String rawArgument : rawArguments) {
            final Exceptional<String> argumentIdentifier = this.parseIdentifier(rawArgument);
            if (argumentIdentifier.absent()) {
                // If a non-pattern argument is required, the converter needs to be looked up by type instead of by its identifier. This will be done when the constructor is being looked up
                argumentTypes.add(null);
                arguments.add(rawArgument);
                continue;
            }
            final String typeIdentifier = argumentIdentifier.get();

            final Exceptional<ArgumentConverter<?>> converter = source.applicationContext()
                    .first(ArgumentConverterContext.class)
                    .flatMap(context -> context.converter(typeIdentifier));

            if (converter.absent())
                return Exceptional.of(new IllegalArgumentException(source.applicationContext()
                        .get(CommandParameterResources.class)
                        .missingConverter(type.qualifiedName())
                        .asString())
                );

            argumentTypes.add(converter.get().type());
            arguments.add(converter.get().convert(source, rawArgument).orNull());
        }


        return this.constructor(argumentTypes, arguments, type, source)
                .flatMap(constructor -> constructor.createInstance(arguments.toArray(new Object[0])));
    }

    <T> Exceptional<Boolean> preconditionsMatch(TypeContext<T> type, CommandSource source, String raw);

    List<String> splitArguments(String raw);

    Exceptional<String> parseIdentifier(String argument);

    default <T> Exceptional<ConstructorContext<T>> constructor(final List<TypeContext<?>> argumentTypes, final List<Object> arguments, final TypeContext<T> type, final CommandSource source) {
        for (final ConstructorContext<T> constructor : type.constructors()) {
            if (constructor.parameterCount() != arguments.size()) continue;
            final LinkedList<TypeContext<?>> parameters = constructor.parameterTypes();

            boolean passed = true;
            for (int i = 0; i < parameters.size(); i++) {
                final TypeContext<?> parameter = parameters.get(i);
                final TypeContext<?> argument = argumentTypes.get(i);

                if (argument == null) {
                    final Exceptional<? extends ArgumentConverter<?>> converter = source.applicationContext()
                            .first(ArgumentConverterContext.class)
                            .flatMap(context -> context.converter(parameter));
                    if (converter.present()) {
                        final Exceptional<?> result = converter.get().convert(source, (String) arguments.get(i));
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
            if (passed) return Exceptional.of(constructor);
        }
        return Exceptional.of(new IllegalArgumentException(source.applicationContext().get(CommandParameterResources.class).notEnoughArgs().asString()));
    }
}
