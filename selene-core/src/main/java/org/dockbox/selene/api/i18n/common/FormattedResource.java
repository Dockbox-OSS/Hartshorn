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

package org.dockbox.selene.api.i18n.common;

import org.dockbox.selene.api.util.SeleneUtils;

import java.util.Map;

public class FormattedResource implements ResourceEntry
{

    private final ResourceEntry entry;
    private final Object[] formattingArgs;

    public FormattedResource(ResourceEntry entry, Object[] formattingArgs)
    {
        this.entry = entry;
        this.formattingArgs = formattingArgs;
    }

    @Override
    public String getKey()
    {
        return this.entry.getKey();
    }

    @Override
    public String asString()
    {
        return this.formatCustom(this.entry.asString(), this.formattingArgs);
    }

    @Override
    public String plain()
    {
        return ResourceEntry.plain(this.formatCustom(this.entry.asString(), this.formattingArgs));
    }

    @Override
    public ResourceEntry translate(Language lang)
    {
        return this.entry.translate(lang).format(this.formattingArgs);
    }

    // Format value placeholders and colors
    @SuppressWarnings("DuplicatedCode")
    public String formatCustom(String m, Object... args)
    {
        String temp = m;
        if (0 == args.length) return temp;
        Map<String, String> map = SeleneUtils.emptyMap();

        for (int i = 0; i < args.length; i++)
        {
            String arg = "" + args[i];
            if (arg.isEmpty()) map.put(String.format("{%d}", i), "");
            else map.put(String.format("{%d}", i), arg);
            if (0 == i) map.put("%s", arg);
        }
        temp = this.replaceFromMap(temp, map);
        return this.parseColors(temp);
    }
}
