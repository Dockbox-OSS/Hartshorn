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

package org.dockbox.hartshorn.util.introspect.util;

import org.dockbox.hartshorn.util.introspect.view.ParameterView;

import java.lang.annotation.Annotation;

/**
 * A {@link ParameterLoaderRule} that accepts parameters that are annotated with a specific annotation.
 *
 * @param <A> the annotation type
 * @param <C> the context type
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public abstract class AnnotatedParameterLoaderRule<A extends Annotation, C extends ParameterLoaderContext> implements ParameterLoaderRule<C>{

    protected abstract Class<A> annotation();

    @Override
    public boolean accepts(ParameterView<?> parameter, int index, C context, Object... args) {
        return parameter.annotations().has(this.annotation());
    }
}
