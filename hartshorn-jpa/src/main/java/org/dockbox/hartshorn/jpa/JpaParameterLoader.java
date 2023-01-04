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

package org.dockbox.hartshorn.jpa;

import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.parameter.RuleBasedParameterLoader;

public class JpaParameterLoader extends RuleBasedParameterLoader<JpaParameterLoaderContext> {

    public JpaParameterLoader() {
        this.add(new JpaPaginationParameterRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterView<T> parameter, final int index, final JpaParameterLoaderContext context, final Object... args) {
        final Object value = args[index];
        if (context.applicationContext().environment().environment().parameterNamesAvailable()) {
            context.query().setParameter(parameter.name(), value);
        }
        else {
            context.query().setParameter(index + 1, value);
        }
        return super.loadDefault(parameter, index, context, args);
    }
}
