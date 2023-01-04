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

package org.dockbox.hartshorn.jpa.annotations;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the key of a data source for a {@link JpaRepository}. The key is used to identify the
 * data source in the {@link DataSourceList}, and is <b>not</b> a representation of a {@link ComponentKey}.
 *
 * <p>A sample usage may look like the following snippet:
 * <pre>{@code
 * @Service
 * @DataSource("my-data-source")
 * public class EntityJpaRepository implements JpaRepository<Entity, Long> {
 * }
 * }</pre>
 *
 * @author Guus Lieben
 * @since 22.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataSource {
    String value();
}
