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

package org.dockbox.hartshorn.context;

/**
 * A context that has a name, which can be used for identification purposes. The name is not
 * necessarily unique, but is encouraged to be.
 *
 * @since 0.4.3
 *
 * @author Guus Lieben
 */
public interface NamedContext extends Context {

    /**
     * Returns the name of this context. The name is not necessarily unique, but is encouraged to be.
     *
     * @return The name of this context.
     */
    String name();
}
