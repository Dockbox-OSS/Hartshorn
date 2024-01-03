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

package org.dockbox.hartshorn.component.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.Named;
import org.dockbox.hartshorn.inject.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that a method will act as a binding provider. The return type of the
 * method, combined with optional {@link Qualifier} annotations form the {@link ComponentKey} of
 * the binding.
 *
 * <p>Provider methods can have parameters, which will be injected through the active
 * {@link ApplicationContext}. This includes support for complex {@link ComponentKey}s, and
 * {@link org.dockbox.hartshorn.context.Context} types.
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

    /**
     * The name of the binding. If not specified, no name will be bound.
     *
     * @return the name of the binding
     *
     * @deprecated use {@link Named} annotations instead or a custom {@link Qualifier} annotation instead
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
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
