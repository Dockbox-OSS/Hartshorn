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

package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.jpa.query.context.JpaQueryContext;

import jakarta.persistence.Query;

public class EntityQueryExecutor implements QueryExecutor {

    @Override
    public Object execute(final JpaQueryContext context) {
        final Query query = context.query();

        final Object result;
        // TODO: Also update other usages of .isVoid() to use queryType() instead
        final QueryExecuteType queryExecuteType = context.queryType();
        if (queryExecuteType == QueryExecuteType.SELECT) result = query.getResultList();
        else  result = query.executeUpdate();

        if (context.automaticClear()) context.entityManager().clear();
        return result;
    }
}
