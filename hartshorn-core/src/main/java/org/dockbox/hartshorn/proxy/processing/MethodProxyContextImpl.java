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

package org.dockbox.hartshorn.proxy.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;

public class MethodProxyContextImpl<T> extends DefaultApplicationAwareContext implements MethodProxyContext<T> {

    private final TypeView<T> type;
    private final MethodView<T, ?> method;

    public MethodProxyContextImpl(final ApplicationContext context, final TypeView<T> type, final MethodView<T, ?> method) {
        super(context);
        this.type = type;
        this.method = method;
    }

    @Override
    public <A extends Annotation> A annotation(final Class<A> annotation) {
        return this.method.annotations().get(annotation).orNull();
    }

    @Override
    public TypeView<T> type() {
        return this.type;
    }

    @Override
    public MethodView<T, ?> method() {
        return this.method;
    }
}
