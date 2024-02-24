/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.CommandParameterResources;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.context.ArgumentConverterRegistry;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractParameterPattern implements CustomParameterPattern {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractParameterPattern.class);

    private final ArgumentConverterRegistry argumentConverterRegistry;

    protected AbstractParameterPattern(ArgumentConverterRegistry argumentConverterRegistry) {
        this.argumentConverterRegistry = argumentConverterRegistry;
    }

    @Override
    public <T> Option<T> request(Class<T> type, CommandSource source, String raw) throws ConverterException {
        ApplicationContext context = source.applicationContext();
        TypeView<T> typeView = context.environment().introspector().introspect(type);

        if (!this.preconditionsMatch(type, source, raw)) {
            LOG.debug("Preconditions failed, rejecting raw argument " + raw);
            return Option.empty();
        }

        List<String> rawArguments = this.splitArguments(raw);
        List<Class<?>> argumentTypes = new ArrayList<>();
        List<Object> arguments = new ArrayList<>();

        for (String rawArgument : rawArguments) {
            LOG.debug("Parsing raw argument " + rawArgument);
            Option<String> argumentIdentifier = this.parseIdentifier(rawArgument);
            if (argumentIdentifier.absent()) {
                LOG.debug("Could not determine argument identifier for raw argument '%s', this is not a error as the value likely needs to be looked up by its type instead.".formatted(rawArgument));
                // If a non-pattern argument is required, the converter needs to be looked up by type instead of by its identifier. This will be done when the constructor is being looked up
                argumentTypes.add(null);
                arguments.add(rawArgument);
                continue;
            }
            String typeIdentifier = argumentIdentifier.get();

            Option<ArgumentConverter<?>> converter = this.argumentConverterRegistry.converter(typeIdentifier);
            if (converter.absent()) {
                LOG.debug("Could not locate converter for identifier '%s'".formatted(typeIdentifier));
                throw new MissingConverterException(context, typeView);
            }

            LOG.debug("Found converter for identifier '%s'".formatted(typeIdentifier));
            argumentTypes.add(converter.get().type());
            arguments.add(converter.get().convert(source, rawArgument).orNull());
        }

        Option<ConstructorView<T>> constructor = this.constructor(argumentTypes, arguments, typeView, source);
        if (constructor.present()) {
            try {
                return constructor.get().create(arguments.toArray());
            }
            catch (ConverterException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new ConverterException(t);
            }
        }
        else {
            return Option.empty();
        }
    }

    public abstract <T> boolean preconditionsMatch(Class<T> type, CommandSource source, String raw) throws ConverterException;

    public abstract List<String> splitArguments(String raw);

    public abstract Option<String> parseIdentifier(String argument) throws ConverterException;

    protected <T> Option<ConstructorView<T>> constructor(List<Class<?>> argumentTypes, List<Object> arguments, TypeView<T> type, CommandSource source) throws ConverterException {
        for (ConstructorView<T> constructor : type.constructors().all()) {
            if (this.constructorMatchesArguments(argumentTypes, arguments, type, source, constructor)) {
                return Option.of(constructor);
            }
        }
        throw new ArgumentMatchingFailedException(source.applicationContext().get(CommandParameterResources.class).notEnoughArgs());
    }

    private <T> boolean constructorMatchesArguments(List<Class<?>> argumentTypes, List<Object> arguments, TypeView<T> type, CommandSource source, ConstructorView<T> constructor) {
        if (constructor.parameters().count() != arguments.size()) {
            return false;
        }
        List<TypeView<?>> parameters = constructor.parameters().types();

        boolean passed = true;
        for (int i = 0; i < parameters.size(); i++) {
            TypeView<?> parameter = parameters.get(i);
            Class<?> argument = argumentTypes.get(i);

            if (argument == null) {
                if (this.tryProvideArgument(arguments, source, parameter, i)) {
                    continue; // Generic type, will be parsed later
                }
            }
            else if (parameter.is(argument)) {
                continue;
            }

            passed = false;
            break; // Parameter is not what we expected, do not continue
        }
        if (passed) {
            LOG.debug("Found matching constructor for " + type.name() + " with " + argumentTypes.size() + " arguments.");
            return true;
        }
        return false;
    }

    private boolean tryProvideArgument(List<Object> arguments, CommandSource source, TypeView<?> parameter, int i) {
        Option<? extends ArgumentConverter<?>> converter = this.argumentConverterRegistry.converter(parameter);
        if (converter.present()) {
            Option<?> result = converter.get().convert(source, (String) arguments.get(i));
            if (result.present()) {
                arguments.set(i, result.get());
                return true;
            }
        }
        return false;
    }
}
