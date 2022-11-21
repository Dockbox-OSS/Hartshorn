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

package org.dockbox.hartshorn.jpa.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the annotated method will perform a query targeting the entity target of
 * the repository this method is declared on. The method may return a result, depending
 * on the query.
 *
 * <p>Queries may be executed in a transaction, if the repository is transactional or the
 * method is annotated with {@link Transactional}.
 *
 * <p>By default, queries will be parsed according to the definition of the named query
 * in the entity class.
 *
 * <p>By default the entity type is derived from the named query, and is checked against
 * the method return type.
 *
 * @author Guus Lieben
 * @since 21.9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NamedQuery {
    String value();
    boolean automaticClear() default false;
    boolean automaticFlush() default true;
    Class<?> entityType() default Void.class;

    enum QueryType {
        JPQL,
        NATIVE,
    }
}
