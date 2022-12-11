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

package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.binding.Bound;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a service method can be used as a factory method, capable of
 * creating instances of bound types through constructors annotated with {@link Bound}. This
 * allows you to manually inject values into a dependency, without knowing the exact type of the
 * dependency.
 *
 * <p>Factory methods are validated on startup, ensuring the parameters of the method match
 * the parameters of the bound type's constructor.
 *
 * <pre>{@code
 * @Service
 * interface CustomFactory {
 *     @Factory
 *     CustomType customType(String name);
 * }
 * }</pre>
 *
 * <p>The above example will create a factory method for the {@code CustomType} type. The
 * implementation of this type can be configured through the active {@link ApplicationContext}.
 *
 * <pre>{@code
 * @Binds(CustomType.class)
 * class CustomTypeImpl implements CustomType {
 *     @Bound
 *     public CustomTypeImpl(String name) {
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * <p>Factory methods do not natively support {@link org.dockbox.hartshorn.component.Scope scopes},
 * as the hierarchy of the component to be provided is determined during initialization, and scopes
 * are only defined as context at that point. If you wish to use scopes, you should use post-construct
 * actions to configure a scope-provided component.
 *
 * @author Guus Lieben
 * @since 21.9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface Factory {

    /**
     * If the binding of a factory method is based on a named dependency, this method returns the
     * name of the {@link ComponentKey} to use.
     * @return The name of the {@link ComponentKey} to use.
     */
    String value() default "";

    /**
     * Indicate whether the factory method should fail if there is no binding for the bound type. If
     * this is set to {@code true}, the factory method will throw an exception if there is no binding
     * for the bound type. If this is set to {@code false}, the factory method will return {@code null}.
     */
    boolean required() default true;
}
