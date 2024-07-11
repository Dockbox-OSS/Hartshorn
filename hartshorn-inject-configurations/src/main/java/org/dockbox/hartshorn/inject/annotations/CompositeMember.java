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

import org.dockbox.hartshorn.inject.collection.ComponentCollection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a binding declaration as a member of a composite collection. Composite members are not exposed
 * as individual components, but rather as a {@link ComponentCollection} that contains all members.
 *
 * <p>Composite members may be declared in any configuration class, and are typically used to provide
 * multiple implementations of a single interface. A simple declaration may look like the example below.
 * <pre>{@code
 * @Configuration
 * class SampleConfiguration {
 *
 *     @Prototype
 *     @CompositeMember
 *     Converter<X, Y> xToYConverter() { ... }
 *
 *     @Prototype
 *     @CompositeMember
 *     Converter<Y, X> yToXConverter() { ... }
 *
 *     @Prototype
 *     ConverterRegistry converters(ComponentCollection<Converter<?, ?>> converters) { ... }
 * }
 * }</pre>
 *
 * <p>Note that the example consumer above does not consider {@link Strict strictness} of the collection.
 *
 * @since 0.6.0
 *
 * @see ComponentCollection
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface CompositeMember {
}

