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

package org.dockbox.hartshorn.commands.service;

import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.core.StringUtilities;

/**
 * Represents a single parameter which can be provided to command executors
 * through {@link CommandContext#flags()} and {@link CommandContext#arguments()}.
 *
 * @param <T> The type of the parameter value.
 */
public class CommandParameter<T> {

    private final T value;
    private final String key;

    public CommandParameter(final T value, final String key) {
        this.value = value;
        this.key = key;
    }

    public T value() {
        return this.value;
    }

    public String key() {
        return this.key;
    }

    public String trimmedKey() {
        return StringUtilities.trimWith('-', this.key());
    }

}
