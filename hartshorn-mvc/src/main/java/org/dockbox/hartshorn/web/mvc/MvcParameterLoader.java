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

package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.parameter.RuleBasedParameterLoader;
import org.dockbox.hartshorn.web.processing.rules.BodyRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.HeaderRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.RequestQueryParameterRule;
import org.dockbox.hartshorn.web.processing.rules.ServletRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.ServletResponseParameterRule;

public class MvcParameterLoader extends RuleBasedParameterLoader<MvcParameterLoaderContext> {

    public MvcParameterLoader() {
        this.add(new ViewModelParameterRule());
        this.add(new BodyRequestParameterRule());
        this.add(new HeaderRequestParameterRule());
        this.add(new RequestQueryParameterRule());
        this.add(new ServletRequestParameterRule());
        this.add(new ServletResponseParameterRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterView<T> parameter, final int index, final MvcParameterLoaderContext context, final Object... args) {
        return context.provider().get(parameter.type().type());
    }
}