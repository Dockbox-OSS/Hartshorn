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

package test.org.dockbox.hartshorn.components;

import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.processing.Prototype;
import org.dockbox.hartshorn.component.processing.Singleton;
import org.dockbox.hartshorn.inject.Named;

@Configuration
public class SampleProviderConfiguration {

    @Prototype
    public ProvidedInterface get() {
        return () -> "Provision";
    }

    @Prototype
    @Named("named")
    public ProvidedInterface named() {
        return () -> "NamedProvision";
    }

    @Prototype
    @Named("parameter")
    public ProvidedInterface withParameter(SampleField field) {
        return () -> "ParameterProvision";
    }

    @Prototype
    @Named("namedParameter")
    public ProvidedInterface withNamedField(@Named("named") SampleField field) {
        return () -> "NamedParameterProvision";
    }

    @Singleton
    @Named("singleton")
    public ProvidedInterface singleton() {
        return () -> "SingletonProvision";
    }
}
