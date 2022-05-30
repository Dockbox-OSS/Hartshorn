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

package org.dockbox.hartshorn.util.reflect;

public class MethodModifier<T, P> implements ElementModifier<MethodContext<T, P>> {

    private final MethodContext<T, P> element;

    private MethodModifier(final MethodContext<T, P> element) {
        this.element = element;
    }

    public static <T, P> MethodModifier<T, P> of(final MethodContext<T, P> element) {
        return new MethodModifier<>(element);
    }

    @Override
    public MethodContext<T, P> element() {
        return this.element;
    }

    public void invoker(final MethodInvoker<T, P> invoker) {
        this.element.invoker(invoker);
    }

    public void access(final boolean expose) {
        this.element().method().setAccessible(expose);
    }

    public static void defaultInvoker(final MethodInvoker<?, ?> invoker) {
        MethodContext.defaultInvoker(invoker);
    }
}
