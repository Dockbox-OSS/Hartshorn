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

package org.dockbox.hartshorn.launchpad.lifecycle;

import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;

import java.util.Set;

/**
 * An {@link ApplicationEnvironment} which can be observed by {@link Observer}s. Observers are notified of events
 * within the environment, and can be used to react to changes in the environment. Observable events may be triggered
 * by the environment, or by components within the environment.
 *
 * @since 0.4.9
 * @author Guus Lieben
 */
public interface ObservableApplicationEnvironment extends ApplicationEnvironment, LifecycleObservable {

    /**
     * Gets all observers of the given type. If no observers are registered for the given type, an empty set is
     * returned.
     *
     * @param type The type of observers to get
     * @return All observers of the given type
     * @param <T> The type of observers to get
     */
    <T extends Observer> Set<T> observers(Class<T> type);
}
