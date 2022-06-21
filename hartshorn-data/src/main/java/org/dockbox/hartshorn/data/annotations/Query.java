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
 * <p>By default, queries will be parsed as JPA (JPQL) queries and executed against the
 * database. This can be overridden by specifying a different {@link QueryType} in
 * {@link #type()} to use the native query language of the database.
 *
 * <p>By default the entity type is derived from the {@link #entityType()} attribute. If
 * this is not specified, the entity type is derived from the method return type.
 *
 * @author Guus Lieben
 * @since 21.9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {
    String value();
    QueryType type() default QueryType.JPQL;
    boolean automaticClear() default false;
    boolean automaticFlush() default true;
    Class<?> entityType() default Void.class;

    enum QueryType {
        JPQL,
        NATIVE,
    }
}
