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

package org.dockbox.hartshorn.jms.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.data.annotations.Value;
import org.dockbox.hartshorn.jms.annotations.UseActiveMQ;

import javax.jms.ConnectionFactory;

@Service(activators = UseActiveMQ.class)
public class ActiveMQProviders {

    @Value("hartshorn.jms.url")
    private String url = "tcp://localhost:61616";

    @Provider
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(this.url);
    }
}
