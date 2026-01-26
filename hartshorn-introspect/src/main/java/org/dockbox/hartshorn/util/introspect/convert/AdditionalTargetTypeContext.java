/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.convert;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class AdditionalTargetTypeContext<T> extends DefaultContext {

    private final Class<T> targetType;
    private final ParameterizableType parameterizableType;

    public AdditionalTargetTypeContext(TypeView<T> typeView) {
        this.targetType = typeView.type();
        this.parameterizableType = ParameterizableType.create(typeView);
    }

    public AdditionalTargetTypeContext(Class<T> targetType, ParameterizableType parameterizableType) {
        this.targetType = targetType;
        this.parameterizableType = parameterizableType;
    }

    public Class<T> targetType() {
        return this.targetType;
    }

    public ParameterizableType parameterizableType() {
        return this.parameterizableType;
    }
}
