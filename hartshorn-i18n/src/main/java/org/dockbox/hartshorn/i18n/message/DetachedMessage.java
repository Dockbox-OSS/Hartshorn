/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.i18n.message;

import org.dockbox.hartshorn.i18n.MessageReceiver;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.Message;
import org.dockbox.hartshorn.i18n.text.Text;

public class DetachedMessage implements Message {

    private final String fake;

    public DetachedMessage(String content) {
        this.fake = content;
    }

    @Override
    public String key() {
        return "hartshorn.detached";
    }

    @Override
    public Text asText() {
        return Text.of(this.fake);
    }

    @Override
    public String asString() {
        return this.fake;
    }

    @Override
    public String plain() {
        return this.fake;
    }

    @Override
    public Message translate(MessageReceiver receiver) {
        return this;
    }

    @Override
    public Message translate(Language lang) {
        return this;
    }

    @Override
    public Message translate() {
        return this;
    }

    @Override
    public Message format(Object... args) {
        return this;
    }

    @Override
    public Language language() {
        return Language.EN_US;
    }
}
