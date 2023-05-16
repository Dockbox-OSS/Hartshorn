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

package org.dockbox.hartshorn.util.introspect.util;

import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;

public class ParameterLoaderContext {

    private final ExecutableElementView<?> executable;
    private final Object instance;

    public ParameterLoaderContext(final ExecutableElementView<?> executable, final Object instance) {
        this.executable = executable;
        this.instance = instance;
    }

    public ExecutableElementView<?> executable() {
        return this.executable;
    }

    public Object instance() {
        return this.instance;
    }
}
