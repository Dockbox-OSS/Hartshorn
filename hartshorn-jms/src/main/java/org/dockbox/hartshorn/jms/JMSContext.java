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

import javax.jms.Message;
import javax.jms.Session;

public class JMSContext extends DefaultContext {

    private final Message message;
    private final Session session;

    public JMSContext(final Message message, final Session session) {
        this.message = message;
        this.session = session;
    }

    public Message message() {
        return this.message;
    }

    public Session session() {
        return this.session;
    }
}
