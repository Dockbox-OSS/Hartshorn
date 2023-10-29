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

package org.dockbox.hartshorn.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the values of fields and executables should be automatically populated
 * by {@link org.dockbox.hartshorn.component.ComponentPopulator}s. Note that this annotation
 * is typically not required, as populating is enabled only if fields or executables are
 * explicitly marked as injectable.
 *
 * <p>If the value of {@link #fields()} is {@code true}, all fields will be populated.
 *
 * @author Guus Lieben
 * @since 0.4.9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Populate {

    /**
     * The types of values that should be populated.
     *
     * @return The types of values that should be populated.
     */
    Type[] value();

    /**
     * @deprecated since 0.5.0, for removal in 0.6.0. Use {@link #value()} instead.
     * @return {@code true} if fields should be populated, {@code false} otherwise.
     */
    @Deprecated(since = "0.5.0", forRemoval = true)
    boolean fields() default false;

    /**
     * @deprecated since 0.5.0, for removal in 0.6.0. Use {@link #value()} instead.
     * @return {@code true} if executables (e.g. methods) should be populated, {@code false} otherwise.
     */
    @Deprecated(since = "0.5.0", forRemoval = true)
    boolean executables() default false;

    /**
     * Types of elements that can be populated. Used as a value for {@link Populate#value()}.
     *
     * @author Guus Lieben
     * @since 0.5.0
     */
    enum Type {
        /**
         * <pre>{@code
         * @Inject
         * private MyComponent component;
         * }</pre>
         */
        FIELDS,
        /**
         * <pre>{@code
         * @Inject
         * public void setComponent(MyComponent component) {
         *    this.component = component;
         *    // ...
         * }
         * }</pre>
         */
        EXECUTABLES,
    }
}
