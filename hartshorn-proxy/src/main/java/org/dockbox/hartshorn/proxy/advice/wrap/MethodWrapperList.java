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

package org.dockbox.hartshorn.proxy.advice.wrap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MethodWrapperList<T> implements MethodWrapper<T> {

    private final Collection<MethodWrapper<T>> wrappers;

    @SafeVarargs
    public MethodWrapperList(final MethodWrapper<T>... wrappers) {
        this.wrappers = Arrays.asList(wrappers);
    }

    public MethodWrapperList(final Collection<MethodWrapper<T>> wrappers) {
        this.wrappers = Collections.unmodifiableCollection(wrappers);
    }

    @Override
    public void acceptBefore(final ProxyCallbackContext<T> context) {
        for (final MethodWrapper<T> wrapper : this.wrappers) {
            wrapper.acceptBefore(context);
        }
    }

    @Override
    public void acceptAfter(final ProxyCallbackContext<T> context) {
        for (final MethodWrapper<T> wrapper : this.wrappers) {
            wrapper.acceptAfter(context);
        }
    }

    @Override
    public void acceptError(final ProxyCallbackContext<T> context) {
        for (final MethodWrapper<T> wrapper : this.wrappers) {
            wrapper.acceptError(context);
        }
    }
}
