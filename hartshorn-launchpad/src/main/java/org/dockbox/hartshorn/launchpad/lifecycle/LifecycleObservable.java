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

/**
 * A lifecycle observable is an object that can be observed for lifecycle events. The lifecycle events are defined by the
 * {@link LifecycleObserver} interface.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface LifecycleObservable {

    /**
     * Adds a lifecycle observer to this observable.
     *
     * @param observer the observer to add
     */
    void register(Observer observer);

    /**
     * Adds a lazily instantiated lifecycle observer to this observable. The observer is instantiated when it's first
     * needed.
     *
     * @param observer the observer to add
     */
    void register(Class<? extends Observer> observer);
}
