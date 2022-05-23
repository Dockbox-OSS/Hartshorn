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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.context.ContextCarrier;

/**
 * The context provided to a {@link org.dockbox.hartshorn.commands.CommandExecutor} during
 * command execution. This context provides access to parsed arguments, flags and other
 * related command context.
 */
public interface CommandContext extends ParserContext, ContextCarrier {

    /**
     * Gets the argument or flag associated with the given <code>key</code>, if it exists. If
     * no argument or flag with the given <code>key</code> exists, <code>null</code> is returned
     * instead. The value of the argument is cast to type <code>T</code>.
     *
     * @param key The key of the argument or flag
     * @param <T> The expected type of the argument or flag
     *
     * @return The argument or flag, or <code>null</code>
     * @throws ClassCastException If the argument or flag is not of type <code>T</code>
     */
    <T> T get(String key);

    /**
     * Checks for the presence of an argument or flag associated with the given <code>key</code>.
     *
     * @param key The key of the argument or flag
     *
     * @return <code>true</code> if a argument or flag exists, else <code>false</code>
     */
    boolean has(String key);

    /**
     * Gets the argument or flag associated with the given <code>key</code>, if it exists. The
     * value of the argument is cast to type <code>T</code>. If the argument or flag is not of type
     * <code>T</code>, or does not exist, {@link Result#empty()} is returned instead.
     *
     * @param key The key of the argument or flag
     * @param <T> The expected type of the argument or flag
     *
     * @return The argument or flag wrapped in a {@link Result}, or {@link Result#empty()}
     */
    <T> Result<T> find(String key);

    /**
     * Gets the first {@link CommandParameter} in the form of an argument associated with the given
     * <code>key</code>, if it exists. If the argument is not of type <code>T</code>, or does not exist,
     * {@link Result#empty()} is returned instead. The {@link CommandParameter} contains both the
     * defined key and value of the argument.
     *
     * @param key The key of the argument
     * @param <T> The expected type of the argument
     *
     * @return The argument wrapped in a {@link Result}, or {@link Result#empty()}
     */
    <T> Result<CommandParameter<T>> argument(String key);

    /**
     * Gets the first {@link CommandParameter} in the form of a flag associated with the given
     * <code>key</code>, if it exists. If the flag is not of type <code>T</code>, or does not exist,
     * {@link Result#empty()} is returned instead. The {@link CommandParameter} contains both the
     * defined key and value of the flag.
     *
     * @param key The key of the flag
     * @param <T> The expected type of the flag
     *
     * @return The flag wrapped in a {@link Result}, or {@link Result#empty()}
     */
    <T> Result<CommandParameter<T>> flag(String key);

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
     * <pre><code>
     *     "command &#60;argument&#62;"
     * </code></pre>
     * The raw command may look like:
     * <pre><code>
     *     "command argumentValue"
     * </code></pre>
     *
     * @return The raw command
     */
    String command();
}
