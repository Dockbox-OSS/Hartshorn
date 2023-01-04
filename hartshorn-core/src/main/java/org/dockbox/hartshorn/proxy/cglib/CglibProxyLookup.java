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

import net.sf.cglib.proxy.Enhancer;

import org.dockbox.hartshorn.proxy.StandardProxyLookup;
import org.dockbox.hartshorn.proxy.javassist.JavassistProxyLookup;

/**
 * @deprecated CGLib is not actively maintained, and commonly causes issues with Java 9+.
 *             It is recommended to use Javassist instead, through the
 *             {@link JavassistProxyLookup}.
 */
@Deprecated(since = "22.5")
public class CglibProxyLookup implements StandardProxyLookup {

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return Enhancer.isEnhanced(candidate);
    }
}
