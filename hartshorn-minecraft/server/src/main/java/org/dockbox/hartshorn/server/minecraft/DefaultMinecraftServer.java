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

package org.dockbox.hartshorn.server.minecraft;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.entity.annotations.Entity;
import org.dockbox.hartshorn.api.i18n.MessageReceiver;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.players.Player;

@Entity(value = "minecraft", serializable = false)
@Command(value = Hartshorn.PROJECT_ID, permission = DefaultServer.ADMIN, extend = true)
public class DefaultMinecraftServer {

    @Wired
    private DefaultServerResources resources;

    @Command(value = { "lang", "language" }, arguments = "<language{Language}> [player{Player}]", inherit = false, permission = Hartshorn.GLOBAL_PERMITTED)
    public void switchLang(MessageReceiver src, CommandContext ctx, Language language, Player player) {
        if (null == player) {
            if (src instanceof Player) {
                player = (Player) src;
            }
            else {
                src.send(this.resources.getWrongSource());
                return;
            }
        }

        player.setLanguage(language);

        String languageLocalized = language.getNameLocalized() + " (" + language.getNameEnglish() + ")";
        if (player != src)
            src.sendWithPrefix(this.resources.getOtherLanguageUpdated(player.getName(), languageLocalized));
        player.sendWithPrefix(this.resources.getLanguageUpdated(languageLocalized));
    }
}
