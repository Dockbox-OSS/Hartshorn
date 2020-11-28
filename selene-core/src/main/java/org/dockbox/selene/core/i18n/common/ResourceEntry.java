/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.i18n.common;

import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.SeleneUtils;

import java.util.Map;

public interface ResourceEntry extends Formattable {

    @SuppressWarnings("ConstantDeclaredInInterface")
    int MAX_SHORT_LENGTH = 50;

    String getValue();

    String getValue(Language lang);

    void setValue(String value);

    default String plain(){
        return this.getValue().replaceAll("[$|&][0-9a-fklmnor]", "");
    }

    default String asString() {
        return this.parseColors(this.getValue());
    }

    default Text asText() {
        return Text.of(this.asString());
    }

    default String format(Object... args) {
        return this.formatCustom(this.getValue(), args);
    }

    default String shortFormat(Object... args) {
        int diff = this.asString().length() - this.plain().length();
        String formatted = this.format(args);
        if ((MAX_SHORT_LENGTH -1) + diff > formatted.length())
            return formatted;
        else
            return this.format(args).substring(0, MAX_SHORT_LENGTH + diff);
    }

    default String shorten() {
        if ((MAX_SHORT_LENGTH - 1) < this.getValue().length())
            return this.getValue().substring(0, MAX_SHORT_LENGTH);
        else return this.getValue();
    }

    // Format value placeholders and colors
    @SuppressWarnings("DuplicatedCode")
    default String formatCustom(String m, Object... args) {
        String temp = m;
        if (0 == args.length) return temp;
        Map<String, String> map = SeleneUtils.emptyMap();

        for (int i = 0; i < args.length; i++){
            String arg = "" + args[i];
            if (arg.isEmpty()) map.put(String.format("{%d}", i), "");
            else map.put(String.format("{%d}", i), arg);
            if (0 == i) map.put("%s", arg);
        }
        temp = this.replaceFromMap(temp, map);
        return this.parseColors(temp);
    }

    default String parseColors(String m) {
        String temp = m;
        char[] nativeFormats = "abcdef1234567890klmnor".toCharArray();
        for (char c : nativeFormats) temp = temp.replace(String.format("&%s", c), String.format("\u00A7%s", c));
        return "\u00A7r" + temp
                .replace("$1", java.lang.String.format("\u00A7%s", IntegratedResource.COLOR_PRIMARY.plain()))
                .replace("$2", java.lang.String.format("\u00A7%s", IntegratedResource.COLOR_SECONDARY.plain()))
                .replace("$3", java.lang.String.format("\u00A7%s", IntegratedResource.COLOR_MINOR.plain()))
                .replace("$4", java.lang.String.format("\u00A7%s", IntegratedResource.COLOR_ERROR.plain()));
    }

}
