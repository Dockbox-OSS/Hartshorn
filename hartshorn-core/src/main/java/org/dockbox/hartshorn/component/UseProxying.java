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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.StandardApplicationBuilder;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.component.processing.proxy.ContextCarrierDelegationPostProcessor;
import org.dockbox.hartshorn.component.processing.proxy.ContextMethodPostProcessor;
import org.dockbox.hartshorn.inject.Provided;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that {@link Provided} methods can be proxied when {@link Component#permitProxying()}
 * is {@code true} for any given component. This acts as a service activator, and is loaded as a default in
 * {@link StandardApplicationBuilder}.
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ServiceActivator(processors = {
        ContextCarrierDelegationPostProcessor.class,
        ContextMethodPostProcessor.class,
})
public @interface UseProxying {
}
