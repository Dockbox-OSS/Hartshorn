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

package org.dockbox.hartshorn.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;

import org.dockbox.hartshorn.proxy.AbstractApplicationProxier;
import org.dockbox.hartshorn.proxy.StandardProxyLookup;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;

public class CglibApplicationProxier extends AbstractApplicationProxier {

    public CglibApplicationProxier() {
        this.registerProxyLookup((StandardProxyLookup) Enhancer::isEnhanced);
    }

    @Override
    public <T> StateAwareProxyFactory<T, ?> factory(final Class<T> type) {
        return new CglibProxyFactory<>(type, this.applicationManager().applicationContext());
    }
}