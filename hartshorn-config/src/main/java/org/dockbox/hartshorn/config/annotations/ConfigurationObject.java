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

package org.dockbox.hartshorn.config.annotations;

import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.config.properties.PropertyHolder;
import org.dockbox.hartshorn.util.Property;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Component stereotype for components which are (de)serializable. These components are populated
 * using application properties through the {@link PropertyHolder}, unless a property is explicitly
 * marked with {@link Property#ignore()}, in which case the property is ignored.
 *
 * @author Guus Lieben
 * @since 22.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
public @interface ConfigurationObject {
    String prefix() default "";
    boolean singleton() default true;
}
