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

import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.proxy.ProxyOrchestratorLoader;
import org.dockbox.hartshorn.util.introspect.Introspector;

/**
 * @deprecated CGLib is not actively maintained, and commonly causes issues with Java 9+.
 *             It is recommended to use Javassist instead, through the
 *             {@code org.dockbox.hartshorn.proxy.javassist.JavassistProxyOrchestratorLoader}.
 */
@Deprecated(since = "0.5.0")
public class CglibProxyOrchestratorLoader implements ProxyOrchestratorLoader {
    @Override
    public ProxyOrchestrator create(Introspector introspector) {
        return new CglibProxyOrchestrator(introspector);
    }
}
