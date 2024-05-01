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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * A system subject that sends messages to the console. This is the default system subject for
 * applications.
 *
 * <p>Messages are logged at the {@link Level} specified by the {@link #level()} method. By default,
 * this is {@link Level#INFO}.
 *
 * @since 0.4.6
 *
 * @author Guus Lieben
 */
public class ApplicationSystemSubject extends SystemSubject {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationSystemSubject.class);

    private Level level = Level.INFO;

    public ApplicationSystemSubject(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    /**
     * Sets the level at which messages are logged. By default, this is {@link Level#INFO}.
     *
     * @param level the level at which messages are logged
     * @return this subject
     */
    public ApplicationSystemSubject withLevel(Level level) {
        this.level = level;
        return this;
    }

    /**
     * Returns the level at which messages are logged. By default, this is {@link Level#INFO}.
     *
     * @return the level at which messages are logged
     */
    public Level level() {
        return this.level;
    }

    @Override
    public void send(Message text) {
        LOG.atLevel(this.level).log("{}", text.string());
    }
}
