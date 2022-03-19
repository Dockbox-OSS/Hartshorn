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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.application.lifecycle.ApplicationState;
import org.dockbox.hartshorn.events.EngineChangedState;

import java.io.InputStream;

/**
 * Represents a constant listener which is capable of listening to command inputs. Commands may be entered through
 * any mean, like a command line, external event bus, or similar solutions. Should be activated after the engine
 * started, typically this can be done by listening for {@link EngineChangedState} with
 * {@link ApplicationState.Started} as its parameter.
 *
 * <p>For example
 * <pre>{@code
 * @Listener
 * public void on(EngineChangedState<Started> event) {
 *      event.applicationContext().get(CommandCLI.class).open();
 * }
 * }</pre>
 */
public interface CommandListener {
    void open();

    CommandListener async(boolean async);
    CommandListener input(InputStream stream);
    CommandListener source(CommandSource source);
}
