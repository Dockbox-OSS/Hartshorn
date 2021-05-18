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

package org.dockbox.selene.server.minecraft;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.entity.annotations.Metadata;
import org.dockbox.selene.api.i18n.MessageReceiver;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.server.DefaultServer;
import org.dockbox.selene.server.DefaultServerResources;
import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.util.SeleneUtils;

import javax.inject.Inject;

@Metadata(alias = "minecraft", serializable = false)
@Command(value = SeleneInformation.PROJECT_ID, permission = DefaultServer.SELENE_ADMIN, extend = true)
public class DefaultMinecraftServer {

    @Inject
    private DefaultServerResources resources;

    @Command(value = { "lang", "language" }, arguments = "<language{Language}> [player{Player}]", inherit = false, permission = SeleneInformation.GLOBAL_PERMITTED)
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

    @Command(value = "platform", permission = DefaultServer.SELENE_ADMIN)
    public void platform(MessageReceiver src) {
        MinecraftServerType st = MinecraftServerBootstrap.instance().getServerType();
        String platformVersion = Selene.getServer().getPlatformVersion();

        String mcVersion = MinecraftServerBootstrap.instance().getMinecraftVersion().getReadableVersionString();

        Object[] system = SeleneUtils.getAll(System::getProperty,
                "java.version", "java.vendor", "java.vm.version", "java.vm.name", "java.vm.vendor", "java.runtime.version", "java.class.version");

        src.send(this.resources.getPlatformInformation(st.getDisplayName(), platformVersion, mcVersion,
                system[0], system[1], system[2], system[2], system[3], system[4], system[5])
        );
    }
}
