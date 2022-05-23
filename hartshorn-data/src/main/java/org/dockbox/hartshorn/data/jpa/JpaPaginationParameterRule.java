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

package org.dockbox.hartshorn.data.jpa;

import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.parameter.ParameterLoaderRule;
import org.dockbox.hartshorn.data.context.JpaParameterLoaderContext;

import javax.persistence.Query;

public class JpaPaginationParameterRule implements ParameterLoaderRule<JpaParameterLoaderContext> {

    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final JpaParameterLoaderContext context, final Object... args) {
        return parameter.type().childOf(Pagination.class);
    }

    @Override
    public <T> Result<T> load(final ParameterContext<T> parameter, final int index, final JpaParameterLoaderContext context, final Object... args) {
        final Query query = context.query();
        final Pagination pagination = (Pagination) args[index];
        if (pagination.max() != null) query.setMaxResults(pagination.max());
        if (pagination.start() != null) query.setFirstResult(pagination.start());
        return Result.of((T) args[index]);
    }
}
