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

package test.org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;

public class StringParameterRule implements ParameterLoaderRule<ParameterLoaderContext> {

    @Override
    public boolean accepts(final ParameterView<?> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        return parameter.type().is(String.class);
    }

    @Override
    public <T> Option<T> load(final ParameterView<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        return Option.of(parameter.type().cast("JUnit"));
    }
}