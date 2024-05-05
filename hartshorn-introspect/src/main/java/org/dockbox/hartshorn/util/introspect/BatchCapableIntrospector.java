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

package org.dockbox.hartshorn.util.introspect;

/**
 * An {@link Introspector} that can be optimized for batch introspection. Batch mode should only be
 * enabled when working in a multi-application environment, where the same types are introspected
 * across multiple applications. In this case, implementations can share caches, or otherwise optimize
 * for batch introspection.
 *
 * <p>Batch optimization typically expects that the same implementation is used across all applications.
 * If this is not the case, there is no guarantee that the optimization of one application is compatible
 * with the optimization of another application.
 *
 * <p>Batch mode is disabled by default, and should only be enabled when required. Note that batch mode
 * is not guaranteed to improve performance, and may even decrease performance in some cases.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface BatchCapableIntrospector extends Introspector {

    /**
     * Enables or disables batch mode. When enabled, the introspector is optimized for batch introspection.
     *
     * @param enabled whether batch mode should be enabled
     */
    void enableBatchMode(boolean enabled);

    /**
     * Returns whether batch mode is enabled.
     *
     * @return whether batch mode is enabled
     */
    boolean batchModeEnabled();

}
