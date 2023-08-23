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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.component.factory.Factory;
import org.dockbox.hartshorn.component.factory.FactoryServicePostProcessor;
import org.dockbox.hartshorn.component.factory.FactoryServicePreProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a constructor as bound. This means that the constructor arguments will
 * not be injected through regular injection, and instead requires manual provision of
 * arguments. This is implemented through {@link Factory}
 * services.
 *
 * @deprecated Implicit binding is discouraged, see {@link Factory} for more information.
 *
 * @see Factory
 * @see FactoryServicePreProcessor
 * @see FactoryServicePostProcessor
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
@Deprecated(since = "0.5.0", forRemoval = true)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface Bound {
}
