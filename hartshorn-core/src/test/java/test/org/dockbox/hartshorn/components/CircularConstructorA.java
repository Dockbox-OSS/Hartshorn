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

package test.org.dockbox.hartshorn.components;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.dockbox.hartshorn.component.Component;

@Singleton
@Component
public class CircularConstructorA {

    private final CircularConstructorB constructorB;

    @Inject
    public CircularConstructorA(CircularConstructorB constructorB) {
        this.constructorB = constructorB;
    }

    public CircularConstructorB constructorB() {
        return this.constructorB;
    }
}
