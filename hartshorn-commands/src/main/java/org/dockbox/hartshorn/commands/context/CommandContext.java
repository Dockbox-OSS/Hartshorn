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

package org.dockbox.hartshorn.commands.context;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.util.option.Option;

/**
 * The context provided to a {@link org.dockbox.hartshorn.commands.CommandExecutor} during
 * command execution. This context provides access to parsed arguments, flags and other
 * related command context.
 *
 * @since 0.4.0
 *
 * @author Guus Lieben
 */
public interface CommandContext extends ParserContext, ContextCarrier {

    /**
     * Gets the argument or flag associated with the given {@code key}, if it exists. If
     * no argument or flag with the given {@code key} exists, {@code null} is returned
     * instead. The value of the argument is cast to type {@code T}.
     *
     * @param key The key of the argument or flag
     * @param type The expected type of the argument or flag
     * @param <T> The expected type of the argument or flag
     *
     * @return The argument or flag, or {@code null}
     * @throws ClassCastException If the argument or flag is not of type {@code T}
     */
    @Nullable
    <T> T get(String key, Class<T> type);

    /**
     * Checks for the presence of an argument or flag associated with the given {@code key}.
     *
     * @param key The key of the argument or flag
     *
     * @return {@code true} if a argument or flag exists, else {@code false}
     */
    boolean has(String key);

    /**
     * Gets the argument or flag associated with the given {@code key}, if it exists. The
     * value of the argument is cast to type {@code T}. If the argument or flag is not of type
     * {@code T}, or does not exist, {@link Option#empty()} is returned instead.
     *
     * @param key The key of the argument or flag
     * @param type The expected type of the argument or flag
     * @param <T> The expected type of the argument or flag
     *
     * @return The argument or flag wrapped in a {@link Option}, or {@link Option#empty()}
     */
    <T> Option<T> find(String key, Class<T> type);

    /**
     * Gets the first {@link CommandParameter} in the form of an argument associated with the given
     * {@code key}, if it exists. If the argument is not of type {@code T}, or does not exist,
     * {@link Option#empty()} is returned instead. The {@link CommandParameter} contains both the
     * defined key and value of the argument.
     *
     * @param key The key of the argument
     * @param <T> The expected type of the argument
     *
     * @return The argument wrapped in a {@link Option}, or {@link Option#empty()}
     */
    <T> Option<CommandParameter<T>> argument(String key);

    /**
     * Gets the first {@link CommandParameter} in the form of a flag associated with the given
     * {@code key}, if it exists. If the flag is not of type {@code T}, or does not exist,
     * {@link Option#empty()} is returned instead. The {@link CommandParameter} contains both the
     * defined key and value of the flag.
     *
     * @param key The key of the flag
     * @param <T> The expected type of the flag
     *
     * @return The flag wrapped in a {@link Option}, or {@link Option#empty()}
     */
    <T> Option<CommandParameter<T>> flag(String key);

    /**
     * Gets the {@link CommandSource} responsible for executing the command. The source is capable
     * if sending and receiving messages and should be used as output for error messages. Exceptions
     * should not be returned to this source.
     *
     * @return The {@link CommandSource} responsible for executing the command.
     */
    CommandSource source();

    /**
     * Gets the raw command as it was provided by the {@link #source()}. For example, if the command has
     * the following definition:
     * <pre>{@code
     *     "command &#60;argument&#62;"
     * }</pre>
     * The raw command may look like:
     * <pre>{@code
     *     "command argumentValue"
     * }</pre>
     *
     * @return The raw command
     */
    String command();
}
