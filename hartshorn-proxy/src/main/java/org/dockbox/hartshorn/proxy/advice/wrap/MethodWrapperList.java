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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * A {@link MethodWrapper} that wraps multiple other {@link MethodWrapper}s. This wrapper will invoke all of the
 * wrapped method wrappers. If the provided collection is ordered, the wrappers are invoked in the order in which they
 * are provided.
 *
 * @param <T> The type of the proxy instance
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public class MethodWrapperList<T> implements MethodWrapper<T> {

    private final Collection<MethodWrapper<T>> wrappers;

    @SafeVarargs
    public MethodWrapperList(MethodWrapper<T>... wrappers) {
        this.wrappers = Arrays.asList(wrappers);
    }

    public MethodWrapperList(Collection<MethodWrapper<T>> wrappers) {
        this.wrappers = Collections.unmodifiableCollection(wrappers);
    }

    @Override
    public void acceptBefore(ProxyCallbackContext<T> context) {
        for (MethodWrapper<T> wrapper : this.wrappers) {
            wrapper.acceptBefore(context);
        }
    }

    @Override
    public void acceptAfter(ProxyCallbackContext<T> context) {
        for (MethodWrapper<T> wrapper : this.wrappers) {
            wrapper.acceptAfter(context);
        }
    }

    @Override
    public void acceptError(ProxyCallbackContext<T> context) {
        for (MethodWrapper<T> wrapper : this.wrappers) {
            wrapper.acceptError(context);
        }
    }
}
