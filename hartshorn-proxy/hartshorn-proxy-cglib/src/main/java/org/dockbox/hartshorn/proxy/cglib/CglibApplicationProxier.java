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

package org.dockbox.hartshorn.proxy.cglib;

import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.AbstractApplicationProxier;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;

/**
 * @deprecated CGLib is not actively maintained, and commonly causes issues with Java 9+.
 *             It is recommended to use Javassist instead, through the
 *             {@code org.dockbox.hartshorn.proxy.javassist.JavassistApplicationProxier}.
 */
@Deprecated(since = "22.5")
public class CglibApplicationProxier extends AbstractApplicationProxier {

    public CglibApplicationProxier(final ApplicationLogger logger) {
        this.registerProxyLookup(new CglibProxyLookup());
        logger.log().warn("""
                You are using CGLib, which is not actively maintained and may cause issues with Java 9+.
                This may cause unexpected behavior or significant errors during the application runtime.
                It is recommended to use the Javassist proxier instead.""");
    }

    @Override
    public <T> StateAwareProxyFactory<T> factory(final Class<T> type) {
        return new CglibProxyFactory<>(type, this.environment().applicationContext());
    }
}
