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

package org.dockbox.darwin.integrated;

import org.dockbox.darwin.core.annotations.Command;
import org.dockbox.darwin.core.annotations.Listener;
import org.dockbox.darwin.core.command.context.CommandContext;
import org.dockbox.darwin.core.command.context.CommandValue.Argument;
import org.dockbox.darwin.core.command.parse.impl.LanguageArgumentParser;
import org.dockbox.darwin.core.events.discord.DiscordEvent;
import org.dockbox.darwin.core.i18n.common.Language;
import org.dockbox.darwin.core.i18n.entry.IntegratedResource;
import org.dockbox.darwin.core.objects.targets.CommandSource;
import org.dockbox.darwin.core.objects.user.Player;
import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.text.Text;
import org.dockbox.darwin.core.util.extension.Extension;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Extension(id = "i18n_commands", name = "I18N Commands", description = "Provided I18N commands, implementation of i18n extension", authors = {"GuusLieben"})
public class I18NExtension {

    @Command(aliases = {"lang", "language"}, usage = "language <language{String}> [player{Player}] -s --f flag{String}", cooldownDuration = 10)
    public void switchLang(CommandSource src, CommandContext ctx) {
        Optional<Language> ol = ctx.getArgumentAndParse("language", new LanguageArgumentParser());
        @Nullable Player target;

        Optional<Argument<Player>> op = ctx.getArgument("player", Player.class);
        if (op.isPresent()) target = op.get().getValue();
        else if (src instanceof Player) target = (Player) src;
        else {
            src.send(Text.of("What are you??"));
            return;
        }

        // While the parser will always return a language, it is possible the argument is not present in which case we want to use en_US
        Language lang = ol.orElse(Language.EN_US);
        target.setLanguage(lang);
        // Messages sent after language switch will be in the preferred language
        src.sendWithPrefix(IntegratedResource.LANG_SWITCHED.format(lang.getNameLocalized() + " (" + lang.getNameEnglish() + ")"));
    }

}
