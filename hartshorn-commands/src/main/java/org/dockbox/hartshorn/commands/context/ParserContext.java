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

import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.core.context.Context;

import java.util.List;

/**
 * Context related to a simple command result. This contains the parsed arguments and flags, and
 * the original command. This should only be used for basic access, more specialized actions should
 * make use of {@link CommandContext} instead.
 */
public interface ParserContext extends Context {

    /**
     * Gets all registered arguments in the form of {@link CommandParameter}. If no arguments are
     * registered, an empty list is returned instead.
     *
     * @return All registered arguments.
     */
    List<CommandParameter<?>> arguments();

    /**
     * Gets all registered flags in the form of {@link CommandParameter}. If no flags are registered,
     * an empty list is returned instead.
     *
     * @return All registered flags.
     */
    List<CommandParameter<?>> flags();

    /**
     * Gets the alias of a command. Typically, a raw command will contain both the alias and additional
     * arguments and flags. The result of this method is only the alias. For example the raw command:
     * <pre><code>
     *     "command argumentA --flagB"
     * </code></pre>
     * Will result in:
     * <pre><code>
     *     "command"
     * </code></pre>
     *
     * @return The command alias.
     */
    String alias();

}
