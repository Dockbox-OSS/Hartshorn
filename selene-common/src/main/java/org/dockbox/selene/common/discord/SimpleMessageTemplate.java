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

package org.dockbox.selene.common.discord;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

import org.dockbox.selene.api.discord.templates.MessageTemplate;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.Map;

public class SimpleMessageTemplate implements MessageTemplate
{

    private Text content = Text.of();
    private final Map<String, String> filledPlaceholders = SeleneUtils.emptyMap();

    @Override
    public void setContent(Text content)
    {
        this.content = content;
    }

    @Override
    public Text getContent()
    {
        return this.content;
    }

    @Override
    public MessageTemplate copy()
    {
        SimpleMessageTemplate template = new SimpleMessageTemplate();
        template.formatPlaceholders(filledPlaceholders);
        template.setContent(content);
        return template;
    }

    @Override
    public MessageTemplate resetPlaceholders()
    {
        filledPlaceholders.clear();
        return this;
    }

    @Override
    public void formatPlaceholder(String key, Object value)
    {
        this.filledPlaceholders.put(key, String.valueOf(value));
    }

    @Override
    public void formatPlaceholders(Map<String, String> values)
    {
        this.filledPlaceholders.putAll(values);
    }

    @Override
    public Message getJDAMessage()
    {
        MessageBuilder builder = new MessageBuilder();
        String message = replaceFromMap(content.toStringValue(), filledPlaceholders);
        builder.setContent(message);
        return builder.build();
    }
}
