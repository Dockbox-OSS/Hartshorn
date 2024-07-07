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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a method will return a provided object. The underlying method should not be
 * called, but the provided object should be returned instead. The provided object is obtained from the active
 * {@link org.dockbox.hartshorn.inject.provider.ComponentProvider}. The return type of the method is used to
 * determine the {@link ComponentKey} of the provided object.
 *
 * <p>Example:
 * <pre>{@code
 * public interface MyService {
 *     @Provided
 *     SampleComponent component();
 * }
 * }</pre>
 *
 * @see org.dockbox.hartshorn.inject.processors.ContextMethodPostProcessor
 * @author Guus Lieben
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Provided {

    /**
     * The name of the provided object, used to modify the {@link ComponentKey} of
     * the provided object.
     *
     * @return The name of the provided object.
     */
    String value() default "";
}
