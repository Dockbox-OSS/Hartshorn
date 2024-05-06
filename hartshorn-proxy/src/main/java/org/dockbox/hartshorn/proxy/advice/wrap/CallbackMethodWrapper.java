/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.proxy.advice.wrap;

/**
 * A {@link MethodWrapper} which is backed by individual {@link ProxyCallback}s for each of the three method types.
 *
 * @param <T> The type of the proxy instance
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CallbackMethodWrapper<T> implements MethodWrapper<T> {

    private final ProxyCallback<T> before;
    private final ProxyCallback<T> after;
    private final ProxyCallback<T> afterThrowing;

    public CallbackMethodWrapper(ProxyCallback<T> before, ProxyCallback<T> after, ProxyCallback<T> afterThrowing) {
        this.before = before;
        this.after = after;
        this.afterThrowing = afterThrowing;
    }

    @Override
    public void acceptBefore(ProxyCallbackContext<T> context) {
        if (this.before != null) {
            this.before.accept(context);
        }
    }

    @Override
    public void acceptAfter(ProxyCallbackContext<T> context) {
        if (this.after != null) {
            this.after.accept(context);
        }
    }

    @Override
    public void acceptError(ProxyCallbackContext<T> context) {
        if (this.afterThrowing != null) {
            this.afterThrowing.accept(context);
        }
    }
}
