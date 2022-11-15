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

package org.dockbox.hartshorn.jpa.annotations;

import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.jpa.transaction.TransactionalProxyCallbackPostProcessor;
import org.dockbox.hartshorn.proxy.UseProxying;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@UseProxying
@Retention(RetentionPolicy.RUNTIME)
@ServiceActivator(processors = TransactionalProxyCallbackPostProcessor.class)
public @interface UseTransactionManagement {
}
