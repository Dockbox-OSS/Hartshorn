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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.application.context.ApplicationContext;

/**
 * Annotation for injecting a {@link org.dockbox.hartshorn.context.ContextView context} into a class. The
 * context is obtained through the responsible {@link org.dockbox.hartshorn.inject.InjectorContext}. If the
 * context does not exist in the active injector context, the injected value will be {@code null}.
 *
 * @author Guus Lieben
 * @since 0.4.7
 *
 * @deprecated Context injection is now built-in to default injection strategies. Use {@link Inject} instead.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Deprecated(since = "0.6.0", forRemoval = true)
public @interface Context {

    /**
     * The name of the context to inject. If left empty the name will be ignored.
     * @return The name of the context to inject.
     */
    String value() default "";
}
