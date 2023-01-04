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

package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.jpa.query.QueryResultTransformer;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;

import jakarta.persistence.Query;

public class HibernateQueryResultTransformer implements QueryResultTransformer {

    @Override
    public Query transform(final Query query, final TypeView<?> resultType) {
        // SqmQuery implementations are correctly typed, so those don't need to be transformed. Native queries
        // are not pre-configured with a result transformer, so we need to add one.
        if (query instanceof NativeQuery<?> hibernateQuery) {
            final TypeView<?> targetType = QueryResultTransformer.createSafeTargetType(resultType);
            return hibernateQuery.setTupleTransformer(Transformers.aliasToBean(targetType.type()));
        }
        return query;
    }
}
