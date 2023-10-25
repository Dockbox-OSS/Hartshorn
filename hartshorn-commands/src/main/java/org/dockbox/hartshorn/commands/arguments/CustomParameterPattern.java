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

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

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
    <T> Attempt<T, ConverterException> request(Class<T> type, CommandSource source, String raw);
}
