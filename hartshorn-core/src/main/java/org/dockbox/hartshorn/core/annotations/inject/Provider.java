/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.annotations.inject;

import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that a method will act as a binding provider. The return type of the
 * method, combined with the {@link #value()} form the {@link org.dockbox.hartshorn.core.Key} of
 * the binding.
 *
 * <p>The use of provider methods require the presence of {@link UseServiceProvision} on the activator
 * class.
 *
 * <p>Provider methods can have parameters, which will be injected through the active
 * {@link ApplicationContext}. This includes support for {@link javax.inject.Named} parameters.
 *
 * <p>If {@link javax.inject.Singleton} is used on the provider method, the result of the provider
 * method will be cached immediately, and the same instance will be returned on subsequent calls.
 *
 * @author Guus Lieben
 * @since 21.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Provider {
    String value() default "";
    int priority() default -1;
}
