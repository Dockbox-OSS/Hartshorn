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

package org.dockbox.hartshorn.commands.extension;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.InstallIfAbsent;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;

@InstallIfAbsent
public class CommandExtensionContext extends DefaultApplicationAwareContext {

    private final Set<CommandExecutorExtension> extensions = ConcurrentHashMap.newKeySet();

    @Inject
    public CommandExtensionContext(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public boolean add(final CommandExecutorExtension extension) {
        return this.extensions.add(extension);
    }

    public boolean remove(final CommandExecutorExtension extension) {
        return this.extensions.remove(extension);
    }

    public Set<CommandExecutorExtension> extensions() {
        return Set.copyOf(this.extensions);
    }
}
