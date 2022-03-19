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

package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.parameter.ParameterLoaderRule;
import org.dockbox.hartshorn.web.mvc.ViewModel;

public class ViewModelParameterRule implements ParameterLoaderRule<MvcParameterLoaderContext> {

    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final MvcParameterLoaderContext context, final Object... args) {
        return parameter.type().childOf(ViewModel.class);
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final int index, final MvcParameterLoaderContext context, final Object... args) {
        return Exceptional.of((T) context.viewModel());
    }
}
