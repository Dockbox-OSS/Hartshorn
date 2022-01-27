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

package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;

import java.lang.annotation.Annotation;

public abstract class AnnotatedParameterLoaderRule<A extends Annotation, C extends ParameterLoaderContext> implements ParameterLoaderRule<C>{

    protected abstract Class<A> annotation();

    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final C context, final Object... args) {
        return parameter.annotation(this.annotation()).present();
    }
}
