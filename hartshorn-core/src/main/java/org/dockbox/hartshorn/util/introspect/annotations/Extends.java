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

package org.dockbox.hartshorn.util.introspect.annotations;

import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.Service;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that the annotated annotation extends another annotation, inheriting its attributes.
 * Similar to class inheritance, if annotation X extends annotation Y, when searching Y
 * annotation, X annotation will also be returned.
 *
 * <p>A common example of this inheritance is {@link Service}, which extends {@link Component}.
 *
 * <p>If an attribute in the extended (Component) annotation is also present in the extending
 * (Service) annotation, the extending annotation will override the attribute value of the
 * extended annotation.
 *
 * @author Guus Lieben
 * @since 21.2
 * @see AliasFor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Extends {
    Class<? extends Annotation> value();
}
