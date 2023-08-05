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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation stereotype to indicate that the annotated element is handled by another framework than the
 * default injection framework in Hartshorn. This annotation is used to prevent the Hartshorn injection
 * framework from attempting to inject the annotated element.
 *
 * <p>For example, annotations like {@link Context} indicate that the annotated parameter is to be provided
 * as a {@link org.dockbox.hartshorn.context.Context context} instance, obtained from whichever relevant
 * context is available, instead of being injected through a {@link ComponentProvider component provider}.
 *
 * <p>Note that this is a stereotype, and not a decorator. To ensure that the stereotype is recognized, it
 * should be applied to an extension annotation using {@link Extends}. For example:
 * <pre>{@code
 * @Extends(HandledInjection.class)
 * @Retention(RetentionPolicy.RUNTIME)
 * @Target( { ElementType.FIELD, ElementType.PARAMETER } )
 * public @interface ProvidedByOtherFramework {
 *    String value() default "";
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface HandledInjection {
}
