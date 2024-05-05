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

package org.dockbox.hartshorn.util;

/**
 * A functional interface for customizing objects. This interface is similar to {@link java.util.function.Consumer} but
 * allows for composition of customizers, and is the common interface for all customizers in Hartshorn.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface Customizer<T> {

    /**
     * Configures the given target object. Implementations of this method may access the target object directly, and
     * configure it as necessary.
     *
     * @param target The object to configure.
     */
    void configure(T target);

    /**
     * Returns a customizer that composes this customizer with the given customizer. When the returned customizer is
     * invoked, the given customizer is invoked first, and then this customizer is invoked.
     *
     * @param before The customizer to invoke first.
     * @return A customizer that composes this customizer with the given customizer.
     */
    default Customizer<T> compose(Customizer<T> before) {
        return (T target) -> {
            before.configure(target);
            this.configure(target);
        };
    }

    /**
     * Returns a customizer that does nothing. This can be used to accept the default configuration without
     * further modification.
     *
     * @param <T> The type of object to customize.
     *
     * @return A customizer that does nothing.
     */
    static <T> Customizer<T> useDefaults() {
        return target -> {};
    }
}
