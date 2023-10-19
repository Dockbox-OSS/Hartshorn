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

package org.dockbox.hartshorn.component.processing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;

/**
 * Annotation used to indicate that a method will act as a binding provider. The return type of the
 * method, combined with the {@link #value()} form the {@link ComponentKey} of
 * the binding.
 *
 * <p>Provider methods can have parameters, which will be injected through the active
 * {@link ApplicationContext}. This includes support for {@link jakarta.inject.Named} parameters, and
 * optionally {@link org.dockbox.hartshorn.context.Context} types if
 * {@link org.dockbox.hartshorn.inject.processing.UseContextInjection} is used.
 *
 * <p>If {@link jakarta.inject.Singleton} is used on the provider method, the result of the provider
 * method will be cached immediately, and the same instance will be returned on subsequent calls.
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Binds {
    String value() default "";

    int priority() default -1;

    boolean lazy() default false;

    int phase() default 0;

    boolean processAfterInitialization() default true;

    BindingType type() default BindingType.COMPONENT;

    enum BindingType {
        COMPONENT,
        COLLECTION,
    }
}
