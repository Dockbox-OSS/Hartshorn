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

package com.nonregistered;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.services.ComponentLocatorImpl;
import org.mockito.Mockito;
import org.slf4j.Logger;

public class ThrowingComponentLocatorImpl extends ComponentLocatorImpl {

    private final ApplicationContext proxy;
    private boolean mock = false;

    public ThrowingComponentLocatorImpl(final ApplicationContext applicationContext) {
        super(applicationContext);
        this.proxy = Mockito.spy(applicationContext);
    }

    @Override
    public ApplicationContext applicationContext() {
        if (this.mock) return this.proxy;
        return super.applicationContext();
    }

    @Override
    public <T> void validate(final Key<T> key) {
        this.mock = true;
        final Logger logger = Mockito.spy(this.proxy.log());
        Mockito.doAnswer(invocation -> {
            final String message = invocation.getArgument(0, String.class);
            throw new ApplicationException(message);
        }).when(logger).warn(Mockito.anyString());
        Mockito.when(this.proxy.log()).thenReturn(logger);
        super.validate(key);
        this.mock = false;
    }
}
