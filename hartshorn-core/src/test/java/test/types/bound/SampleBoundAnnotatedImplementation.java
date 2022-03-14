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

package test.types.bound;

import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;

import test.types.SampleInterface;

@ComponentBinding(value = SampleInterface.class, permitProxying = false)
public class SampleBoundAnnotatedImplementation implements SampleInterface {

    private final String name;

    @Bound
    public SampleBoundAnnotatedImplementation(final String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }
}
