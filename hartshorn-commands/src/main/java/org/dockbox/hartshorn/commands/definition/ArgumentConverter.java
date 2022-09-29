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

package org.dockbox.hartshorn.commands.definition;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.util.Result;

import java.util.Collection;
import java.util.List;

/**
 * Responsible for converting a raw string input to the specified type <code>T</code>.
 *
 * @param <T> The type this converter can convert into
 */
public interface ArgumentConverter<T> {

    /**
     * The keys/names of the type this converter can apply. This will always contain
     * at least one value.
     *
     * @return The keys/names of the type
     */
    List<String> keys();

    /**
     * The {@link Class} reference of the type this converter can convert into.
     *
     * @return The type reference
     */
    Class<T> type();

    /**
     * Converts a given raw argument into type <code>T</code>. The provided
     * {@link CommandSource} may provide additional context to the implementation.
     *
     * @param source The command source executing a command containing the argument
     * @param argument The raw argument without the associated key
     *
     * @return The converted object of type <code>T</code>, or {@link Result#empty()}
     */
    Result<T> convert(CommandSource source, String argument);

    /**
     * Converts a given argument into type <code>T</code>. The {@link CommandParameter}
     * provides both the raw argument of the value, and the associated <code>key</code>
     * used to obtain it. The provided {@link CommandSource} may provide additional
     * context to the implementation.
     *
     * @param source The command source executing a command containing the argument
     * @param value The argument with the associated key
     *
     * @return The converted object of type <code>T</code>, or {@link Result#empty()}
     */
    Result<T> convert(CommandSource source, CommandParameter<String> value);

    /**
     * Gets the suggestions to complete a currently incomplete argument value. Suggestions
     * are complete values.
     * <p>For example, when possible values are: <code>one, two, three</code>, with the input
     * being <code>t</code>, the returned collection will be <code>two, three</code>.
     *
     * @param source The command source executing a command containing the argument
     * @param argument The incomplete raw argument
     *
     * @return All suggested values, or empty {@link List}
     */
    Collection<String> suggestions(CommandSource source, String argument);

    /**
     * Gets the amount of tokens the converter expects. A token is a single value separated by
     * a delimiter, typically a single space. For example, if the converter expects a first and
     * last name, the size will be 2 (two). If the size is -1 (negative one) the converter is
     * expected to be able to accept all remaining tokens.
     *
     * @return The amount of expected tokens
     */
    int size();
}
