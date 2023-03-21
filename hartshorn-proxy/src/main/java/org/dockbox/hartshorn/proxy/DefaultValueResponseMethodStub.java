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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class DefaultValueResponseMethodStub<T> implements MethodStub<T> {

    @Override
    public Object invoke(final MethodStubContext<T> stubContext) {
        final Class<?> returnType = stubContext.target().returnType();
        final TypeView<?> introspectedType = stubContext.applicationContext()
                .environment()
                .introspect(returnType);
        return introspectedType.defaultOrNull();
    }
}