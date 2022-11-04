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

package test.org.dockbox.hartshorn.parameterloaders;

import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.util.parameter.ParameterLoaderRule;
import org.dockbox.hartshorn.util.parameter.RuleBasedParameterLoader;

import java.util.Set;

public class RuleBasedParameterLoaderImpl extends RuleBasedParameterLoader<ParameterLoaderContext> {
    @Override
    public Set<ParameterLoaderRule<ParameterLoaderContext>> rules() {
        return super.rules();
    }
}
