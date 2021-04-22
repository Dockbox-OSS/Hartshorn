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

package org.dockbox.selene.test;

import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.DefaultResource;
import org.dockbox.selene.minecraft.players.Player;
import org.dockbox.selene.api.objects.targets.MessageReceiver;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.Map;

public enum TestResources implements ResourceEntry {
    SERVER$CONFIRMED("IntegratedModule::confirm::true", "server.confirm.true"),
    SERVER$NOT_CONFIRMED("IntegratedModule::confirm::false", "server.confirm.false"),
    ;

    private final String key;
    private final Map<Language, String> translations = SeleneUtils.emptyConcurrentMap();
    private String value;

    TestResources(String value, String key) {
        this.value = value;
        this.key = key;
    }

    public static String parse(CharSequence input) {
        return DefaultResource.NONE.parseColors(input.toString());
    }

    public String getValue(Player player) {
        return this.translate(player.getLanguage()).asString();
    }

    @Override
    public String getKey() {
        return "TestResource." + this.key;
    }

    @Override
    public String asString() {
        return this.parseColors(this.value);
    }

    @Override
    public String plain() {
        return ResourceEntry.plain(this.value);
    }

    @Override
    public ResourceEntry translate(MessageReceiver receiver) {
        return this;
    }


    @Override
    public ResourceEntry translate(Language lang) {
        return this;
    }

    @Override
    public ResourceEntry translate() {
        return this;
    }

    public void setLanguageValue(Language lang, String value) {
        this.translations.put(lang, value);
        if (lang == Selene.getServer().getGlobalConfig().getDefaultLanguage()) this.value = value;
    }
}
