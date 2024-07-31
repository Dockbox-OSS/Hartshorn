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

package org.dockbox.hartshorn.launchpad;

import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;

/**
 * An exception that is thrown when an attempt is made to close a context that is already closed.
 *
 * @see ApplicationContext#close()
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ContextClosedException extends ApplicationRuntimeException {

    public ContextClosedException(Class<? extends ContextView> type) {
        super("Context (" + type.getSimpleName() + ") is already closed.");
    }
}
