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

package org.dockbox.hartshorn.discord;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.discord.templates.MessageTemplate;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Binds(MessageTemplate.class)
public class SimpleMessageTemplate implements MessageTemplate {

    private final Map<String, String> filledPlaceholders = HartshornUtils.emptyMap();
    @Getter @Setter private Text content = Text.of();

    @Override
    public MessageTemplate copy() {
        MessageTemplate template = new SimpleMessageTemplate();
        template.formatPlaceholders(this.filledPlaceholders);
        template.content(this.content);
        return template;
    }

    @Override
    public MessageTemplate resetPlaceholders() {
        this.filledPlaceholders.clear();
        return this;
    }

    @Override
    public void formatPlaceholder(String key, Object value) {
        this.filledPlaceholders.put('{' + key + '}', String.valueOf(value));
    }

    @Override
    public void formatPlaceholders(Map<String, String> values) {
        values.forEach(this::formatPlaceholder);
    }

    @Override
    public Message message() {
        MessageBuilder builder = new MessageBuilder();
        String message = this.replaceFromMap(this.content.toStringValue(), this.filledPlaceholders);
        builder.setContent(message);
        return builder.build();
    }
}
