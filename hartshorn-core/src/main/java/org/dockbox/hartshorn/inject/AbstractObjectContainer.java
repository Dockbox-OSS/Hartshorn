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

package org.dockbox.hartshorn.inject;

/**
 * TODO: #1060 Add documentation
 *
 * @param <T> ...
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractObjectContainer<T> implements ObjectContainer<T> {

    private final T instance;

    protected AbstractObjectContainer(T instance) {
        this.instance = instance;
    }

    /**
     * Returns the object instance. Note that this instance may or may not have been processed.
     *
     * @return the object instance
     */
    public T instance() {
        return this.instance;
    }

    @Override
    public String toString() {
        return "ObjectContainer{" +
            "instance=" + this.instance +
            '}';
    }
}
