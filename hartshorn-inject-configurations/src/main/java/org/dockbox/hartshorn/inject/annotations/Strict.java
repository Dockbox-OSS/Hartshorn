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

package org.dockbox.hartshorn.inject.annotations;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an injection lookup should be strict, meaning that it should only return a value if it is
 * explicitly bound to the key, and not if it is bound to a sub-type of the key.
 *
 * <p>This is the default behavior of a {@link ComponentKey} lookup, but can be overridden by configuring
 * {@link ComponentKey#strict()}. This annotation can be used by {@link ParameterLoader}s to override the
 * default behavior dynamically.
 *
 * <p>For {@link ComponentCollection} lookups, this annotation can be used to indicate that the collection
 * should only contain elements that are explicitly bound to the key, and not if they are bound to a sub-type
 * of the key. If {@link #value() strict mode} is set to {@code false}, the collection will contain all
 * elements that are compatible with the key, including sub-types. This will thus not only match the first
 * compatible element, but all compatible elements.
 *
 * @see ComponentKey#strict()
 * @see ComponentKey.Builder#strict(boolean)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Strict {

    /**
     * Whether strict mode should be enabled. Defaults to {@code true}.
     *
     * @return {@code true} if strict mode should be enabled, {@code false} otherwise.
     */
    boolean value() default true;
}
