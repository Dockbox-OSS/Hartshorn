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

package test.types;

import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.component.Service;

import javax.inject.Named;
import javax.inject.Singleton;

@Service
public class SampleProviderService {

    @Provider
    public ProvidedInterface get() {
        return () -> "Provision";
    }

    @Provider("named")
    public ProvidedInterface named() {
        return () -> "NamedProvision";
    }

    @Provider("parameter")
    public ProvidedInterface withParameter(final SampleField field) {
        return () -> "ParameterProvision";
    }

    @Provider("namedParameter")
    public ProvidedInterface withNamedField(@Named("named") final SampleField field) {
        return () -> "NamedParameterProvision";
    }

    @Singleton
    @Provider("singleton")
    public ProvidedInterface singleton() {
        return () -> "SingletonProvision";
    }
}
