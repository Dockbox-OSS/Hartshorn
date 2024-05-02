/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;

import java.io.InputStream;

/**
 * Represents a constant listener which is capable of listening to command inputs. Commands may be entered through
 * any means, like a command line, external event bus, or similar solutions. Should be activated after the engine
 * started. This can usually be done by implementing a {@link LifecycleObserver}.
 *
 * <p>For example
 * <pre>{@code
 * @Service
 * class CommandListenerObserver implements LifecycleObserver {
 *     @Inject
 *     private CommandListener listener;
 *
 *     @Override
 *     public void onStarted(ApplicationContext applicationContext) {
 *         this.listener.open();
 *     }
 * }}</pre>
 */
public interface CommandListener {
    void open();

    CommandListener async(boolean async);
    CommandListener input(InputStream stream);
    CommandListener source(CommandSource source);
}
