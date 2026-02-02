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

package org.dockbox.hartshorn.web.route.rules;

import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.rest.PathParameter;

/**
 * A {@link ParameterLoaderRule} that loads parameter values from path parameters using the
 * {@link PathParameter} annotation.
 *
 * @param <C> the type of the parameter loader context
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class PathParameterValueParameterLoaderRule<C extends ParameterLoaderContext>
        extends AbstractWebRequestParameterLoaderRule<PathParameter, C> {

    public PathParameterValueParameterLoaderRule(
            WebRequest request,
            ConversionService conversionService
    ) {
        super(PathParameter.class, request, conversionService);
    }

    @Override
    protected Option<String> lookupValue(WebRequest request, PathParameter annotation) {
        return request.pathParameters().pathParameter(annotation.value());
    }

    @Override
    protected String getDefaultValue(PathParameter annotation) {
        throw new UnsupportedOperationException("Path parameters do not support default values.");
    }
}
