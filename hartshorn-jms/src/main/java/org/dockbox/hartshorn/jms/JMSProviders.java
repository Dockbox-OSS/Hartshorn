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

package org.dockbox.hartshorn.jms;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.data.annotations.Value;
import org.dockbox.hartshorn.jms.annotations.UseJMS;

import javax.inject.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;

@Service(activators = UseJMS.class)
public class JMSProviders {

    @Value("hartshorn.jms.username")
    private String username;

    @Value("hartshorn.jms.password")
    private String password;

    @Value("hartshorn.jms.transacted")
    private boolean transacted = false;

    @Value("hartshorn.jms.acknowledgeMode")
    private int acknowledgeMode = javax.jms.Session.AUTO_ACKNOWLEDGE;

    @Provider
    public Connection connection(final ConnectionFactory connectionFactory, final ExceptionListener exceptionListener) throws JMSException {
        final Connection connection;
        if (this.username == null || this.password == null)
            connection = connectionFactory.createConnection();
        else
            connection = connectionFactory.createConnection(this.username, this.password);

        connection.setExceptionListener(exceptionListener);
        return connection;
    }

    @Provider
    public Session session(final Connection connection) throws JMSException {
        connection.start();
        return connection.createSession(this.transacted, this.acknowledgeMode);
    }

    @Provider("jms.session.default")
    public Session defaultSession(final Connection connection) throws JMSException {
        return this.session(connection);
    }

    @Provider
    @Singleton
    public JMSThreadPoolProvider threadPoolProvider() {
        return new JMSThreadPoolProviderImpl();
    }
}
