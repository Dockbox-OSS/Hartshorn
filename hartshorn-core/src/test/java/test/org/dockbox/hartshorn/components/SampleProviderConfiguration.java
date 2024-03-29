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

import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.Named;

import jakarta.inject.Singleton;

@Configuration
public class SampleProviderConfiguration {

    @Binds
    public ProvidedInterface get() {
        return () -> "Provision";
    }

    @Binds
    @Named("named")
    public ProvidedInterface named() {
        return () -> "NamedProvision";
    }

    @Binds
    @Named("parameter")
    public ProvidedInterface withParameter(SampleField field) {
        return () -> "ParameterProvision";
    }

    @Binds
    @Named("namedParameter")
    public ProvidedInterface withNamedField(@Named("named") SampleField field) {
        return () -> "NamedParameterProvision";
    }

    @Singleton
    @Binds
    @Named("singleton")
    public ProvidedInterface singleton() {
        return () -> "SingletonProvision";
    }
}
