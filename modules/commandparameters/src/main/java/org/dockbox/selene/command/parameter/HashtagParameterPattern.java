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

package org.dockbox.selene.command.parameter;

import org.dockbox.selene.annotations.command.CustomParameter;
import org.dockbox.selene.api.command.context.ArgumentConverter;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.i18n.entry.IntegratedResource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.common.command.convert.ArgumentConverterRegistry;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts Hashtag-patterns into type instances used by command executors. The pattern follows the HashtagPatternParser from WorldEdit.
 * Patterns are expected to start with a hashtag (#) followed by the alias of the type which is ignored while parsing (due to the
 * availability of the Class for the type. Arguments are to be surrounded with square brackets.
 *
 * <p>An example of this pattern is as follows: if we have a constructor for a Shape type
 * <pre>{@code
 * public Shape(String name, int sides) { ... }
 * }</pre>
 * The pattern for this type is expected to be <pre>#shape[square][4]</pre>
 */
public class HashtagParameterPattern implements CustomParameterPattern {

    private static final Pattern ARGUMENT = Pattern.compile("\\[[^\\[\\]]+\\]");

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T> Exceptional<T> request(Class<T> type, CommandSource source, String raw) {
        if (!raw.startsWith("#")) return Exceptional.of(new IllegalArgumentException("Pattern has to be formatted as #type[arg1][arg2][etc.]"));

        List<String> rawArguments = SeleneUtils.emptyList();
        Matcher matcher = ARGUMENT.matcher(raw);
        while (matcher.find()) {
            String argument = matcher.group();
            argument = argument.substring(1, argument.length() - 1);
            rawArguments.add(argument);
        }

        Exceptional<Constructor<T>> exceptionalCtor = getParameterConstructor(type, rawArguments.size());
        if (exceptionalCtor.isAbsent()) return Exceptional.of(exceptionalCtor.getError());

        Constructor<T> constructor = exceptionalCtor.get();
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        if (rawArguments.size() != parameters.length) {
            String usage = type.getAnnotation(CustomParameter.class).usage();
            if (!usage.equals("")) usage = IntegratedResource.USAGE.format(usage).asString();
            if (rawArguments.size() < parameters.length) return Exceptional.of(new IllegalArgumentException("Not enough arguments"));
            else if (rawArguments.size() > parameters.length) return Exceptional.of(new IllegalArgumentException("Too many arguments"));
        }

        for (int i = 0; i < rawArguments.size(); i++) {
            String argument = rawArguments.get(i);
            Class<?> parameterType = parameterTypes[i];
            ArgumentConverter<?> converter = ArgumentConverterRegistry.getConverter(parameterType);
            if (converter == null) return Exceptional
                    .of(new IllegalArgumentException("Parameter of type " + parameterType.getCanonicalName() + " has no registered converter"));

            Exceptional<?> value = converter.convert(source, argument);
            if (value.isAbsent()) return Exceptional.of(value.getError());

            parameters[i] = value.get();
        }

        return Exceptional.of(() -> constructor.newInstance(parameters));
    }
}
