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
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.targets.MessageReceiver;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;

public interface ResourceEntry extends Formattable {

    ResourceEntry translate(Language lang);

    String asString();

    String getKey();

    default Text asText() {
        return Text.of(this.asString());
    }

    String plain();

    default ResourceEntry translate(MessageReceiver receiver) {
        if (receiver instanceof Player)
            return this.translate(((Player) receiver).getLanguage());
        else return this.translate(Selene.getServer().getGlobalConfig().getDefaultLanguage());
    }

    static String plain(String value) {
        return value.replaceAll("[$|&][0-9a-fklmnor]", "");
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


    @SuppressWarnings("ClassReferencesSubclass")
    default FormattedResource format(Object... args) {
        return new FormattedResource(this, args);
    }
}
