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

package org.dockbox.hartshorn.jpa;

import org.dockbox.hartshorn.jpa.query.Pagination;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.parameter.ParameterLoaderRule;

import jakarta.persistence.Query;

public class JpaPaginationParameterRule implements ParameterLoaderRule<JpaParameterLoaderContext> {

    @Override
    public boolean accepts(final ParameterView<?> parameter, final int index, final JpaParameterLoaderContext context, final Object... args) {
        return parameter.type().is(Pagination.class);
    }

    @Override
    public <T> Option<T> load(final ParameterView<T> parameter, final int index, final JpaParameterLoaderContext context, final Object... args) {
        final Query query = context.query();
        if (query.getMaxResults() != Integer.MAX_VALUE) {
            throw new IllegalStateException("Pagination already set, did you define multiple Pagination parameters?");
        }
        final Pagination pagination = (Pagination) args[index];
        if (pagination.max() != null) query.setMaxResults(pagination.max());
        if (pagination.start() != null) query.setFirstResult(pagination.start());
        return Option.of(() -> TypeUtils.adjustWildcards(args[index], Object.class));
    }
}
