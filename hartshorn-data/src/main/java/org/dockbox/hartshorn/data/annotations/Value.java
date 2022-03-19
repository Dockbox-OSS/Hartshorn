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

package org.dockbox.hartshorn.data.annotations;

import org.dockbox.hartshorn.application.ApplicationPropertyHolder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a field should be populated with the value obtained
 * from {@link ApplicationPropertyHolder#property(String)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Value {

    /**
     * The key of the configuration value. Nested values are separated by a single
     * period symbol. For example, in the configuration (JSON) below the deepest
     * value is accessed with <code>config.nested.value</code>, returning the value 'A'
     * <pre><code>
     *     { "config": {     "nested": {         "value": "A"     } }
     *     }
     * </code></pre>
     *
     * @return The configuration key.
     */
    String value();
}
