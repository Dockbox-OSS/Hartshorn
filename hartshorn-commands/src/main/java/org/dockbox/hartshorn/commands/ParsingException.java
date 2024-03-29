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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.i18n.Message;

/**
 * The exception thrown when a command could not be parsed. This can occur either during
 * the creation of a command definition or {@link org.dockbox.hartshorn.commands.context.CommandContext}.
 */
public class ParsingException extends ApplicationException {

    public ParsingException(Message resource) {
        super(resource.string());
    }

    public ParsingException(Message resource, Throwable cause) {
        super(resource.string(), cause);
    }
}
