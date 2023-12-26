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

/**
 * A context object that is passed to {@link ParameterLoader}s to provide them with the necessary
 * information to load a parameter. This information includes the {@link ExecutableElementView} that
 * is being invoked, and the instance on which the method is being invoked. If the executable is
 * static, the instance will be {@code null}.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public class ParameterLoaderContext {

    private final ExecutableElementView<?> executable;
    private final Object instance;

    public ParameterLoaderContext(ExecutableElementView<?> executable, Object instance) {
        this.executable = executable;
        this.instance = instance;
    }

    /**
     * Returns the executable element for which the parameters are being loaded.
     *
     * @return the executable element for which the parameters are being loaded
     */
    public ExecutableElementView<?> executable() {
        return this.executable;
    }

    /**
     * Returns the instance on which the executable is being invoked. If the executable is static,
     * this method will return {@code null}.
     *
     * @return the instance on which the executable is being invoked, or {@code null}
     */
    public Object instance() {
        return this.instance;
    }
}
