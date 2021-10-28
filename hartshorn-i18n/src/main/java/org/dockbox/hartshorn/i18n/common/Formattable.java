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

package org.dockbox.hartshorn.i18n.common;

import java.util.Map;
import java.util.Map.Entry;

public interface Formattable {

    default String replaceFromMap(final String string, final Map<String, String> replacements) {
        final StringBuilder sb = new StringBuilder(string);
        int size = string.length();
        for (final Entry<String, String> entry : replacements.entrySet()) {
            if (0 == size) {
                break;
            }
            final String key = entry.getKey();
            final String value = entry.getValue();
            int nextSearchStart;
            int start = sb.indexOf(key, 0);
            while (-1 < start) {
                final int end = start + key.length();
                nextSearchStart = start + value.length();
                sb.replace(start, end, value);
                size -= end - start;
                start = sb.indexOf(key, nextSearchStart);
            }
        }
        return sb.toString();
    }
}
