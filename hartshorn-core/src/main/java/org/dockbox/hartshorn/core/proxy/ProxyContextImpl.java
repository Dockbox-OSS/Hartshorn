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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import java.util.concurrent.Callable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProxyContextImpl implements ProxyContext {

    private final Callable<?> proceed;
    private final Object self;

    @Override
    public <T> T invoke(final Object... args) throws ApplicationException {
        try {
            if (this.proceed() == null) return null;
            return (T) this.proceed().call();
        }
        catch (final Exception e) {
            throw new ApplicationException(e);
        }
    }
}
