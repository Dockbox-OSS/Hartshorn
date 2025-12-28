/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.inject.populate;

import org.dockbox.hartshorn.inject.ContextKey;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dockbox.hartshorn.test.HartshornAssertions.assertThat;

@HartshornIntegrationTest(
    includeBasePackages = false,
    componentPostProcessors = SimpleContextConfiguringComponentProcessor.class
)
class ContextConfiguringComponentProcessorTests {

    @Test
    @TestComponents(EmptyComponent.class)
    void nonContextComponentIsProcessed(
        @Inject EmptyComponent emptyComponent,
        @Inject ProxyOrchestrator proxyOrchestrator
    ) {
        assertThat(emptyComponent).isNotNull();
        assertThat(proxyOrchestrator.isProxy(emptyComponent)).isTrue();

        Proxy<EmptyComponent> component = (Proxy<EmptyComponent>) emptyComponent;
        Option<SimpleContext> context = component.manager()
                .firstContext(ContextKey.of(SimpleContext.class));
        assertThat(context).value()
                .extracting(SimpleContext::value)
                .isEqualTo("Foo");
    }

    @Test
    @TestComponents(ContextComponent.class)
    void contextComponentIsProcessed(
        @Inject ContextComponent contextComponent,
        @Inject ProxyOrchestrator proxyOrchestrator
    ) {
        assertThat(contextComponent).isNotNull();
        assertThat(proxyOrchestrator.isProxy(contextComponent)).isFalse();

        Option<SimpleContext> context = contextComponent
                .firstContext(ContextKey.of(SimpleContext.class));
        assertThat(context).value()
                .extracting(SimpleContext::value)
                .isEqualTo("Foo");
    }
}
