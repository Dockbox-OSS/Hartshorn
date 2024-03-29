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

import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.i18n.MessageReceiver;
import org.dockbox.hartshorn.util.Subject;

/**
 * Represents a single {@link Subject} capable of executing commands.
 */
public interface CommandSource extends MessageReceiver, ContextCarrier {

    /**
     * Executes the given raw command as the {@link Subject}
     * represented by this source.
     *
     * @param command The raw command
     */
    default void execute(String command) throws ParsingException {
        CommandGateway gateway = this.applicationContext().get(CommandGateway.class);
        gateway.accept(this, command);
    }
}
