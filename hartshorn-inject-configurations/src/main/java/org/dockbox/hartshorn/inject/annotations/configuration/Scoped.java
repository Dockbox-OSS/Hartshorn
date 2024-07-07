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

package org.dockbox.hartshorn.inject.annotations.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.inject.scope.Scope;

/**
 * Defines in which scope a component should be created. When defined, all scopes that are an instance
 * of the provided type will contain the component declaration. This is useful for components that
 * should be created in a specific scope, such as a request or session scope.
 *
 * <p>Scope implementations remain up to the container to define. The container will typically provide
 * a default application scope, which does not need to be defined explicitly.
 *
 * @since 0.5.0
 *
 * @see Scope
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Scoped {
    Class<? extends Scope> value();
}
