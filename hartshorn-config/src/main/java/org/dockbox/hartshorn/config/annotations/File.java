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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used by {@link Deserialize} and {@link Serialize} to indicate a potential
 * file target.
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface File {
    /**
     * The owner of the file, to act as synthetic hierarchical category.
     *
     * @return The owner of the file.
     *
     * @deprecated To be removed in a future release.
     */
    @Deprecated(since = "0.4.12", forRemoval = true)
    Class<?> owner() default Void.class;

    /**
     * The name of the file.
     *
     * @return The name of the file.
     */
    String value();
}
