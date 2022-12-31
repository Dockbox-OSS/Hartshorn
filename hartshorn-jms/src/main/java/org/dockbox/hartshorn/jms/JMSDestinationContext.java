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

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.jms.annotations.DestinationType;
import org.dockbox.hartshorn.jms.annotations.JMSConfiguration;

public class JMSDestinationContext extends DefaultContext {

    private final String session;
    private final String id;
    private final DestinationType destinationType;

    public JMSDestinationContext(final String session, final String id, final DestinationType destinationType) {
        this.session = session;
        this.id = id;
        this.destinationType = destinationType;
    }

    public static JMSDestinationContext of(final JMSConfiguration configuration) {
        return new JMSDestinationContext(
                configuration.session(),
                configuration.id(),
                configuration.destination()
        );
    }

    public String session() {
        return this.session;
    }

    public String id() {
        return this.id;
    }

    public DestinationType destinationType() {
        return this.destinationType;
    }
}
