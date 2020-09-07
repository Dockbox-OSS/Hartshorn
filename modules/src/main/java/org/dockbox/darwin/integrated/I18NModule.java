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
import org.dockbox.darwin.core.util.extension.Extension;
import org.dockbox.darwin.core.command.context.CommandContext;
import org.dockbox.darwin.core.command.context.CommandValue;
import org.dockbox.darwin.core.command.parse.AbstractTypeArgumentParser;
import org.dockbox.darwin.core.i18n.I18N;
import org.dockbox.darwin.core.i18n.Languages;
import org.dockbox.darwin.core.objects.targets.CommandSource;
import org.dockbox.darwin.core.objects.user.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Extension(id = "i18n_commands", name = "I18N Commands", description = "Provided I18N commands, implementation of i18n extension", authors = {"GuusLieben"})
public class I18NModule {

    @Command(aliases = {"lang", "language"}, usage = "language <language>", context = "language <language{String}")
    public void switchLang(CommandSource src, CommandContext ctx) {
        if (src instanceof Player) {
            Optional<Languages> ol = ctx.getArgumentAndParse("language", new LanguageArgumentParser());
            // While the parser will always return a language, it is possible the argument is not present in which case we want to use en_US
            Languages lang = ol.orElse(Languages.EN_US);
            ((Player) src).setLanguage(lang);
            // Messages sent after language switch will be in the preferred language
            src.sendWithPrefix(I18N.LANG_SWITCHED.format(lang.getDescription()));
        }
    }

    public static class LanguageArgumentParser extends AbstractTypeArgumentParser<Languages> {

        @NotNull
        @Override
        public Optional<Languages> parse(@NotNull CommandValue<String> commandValue) {
            String val = commandValue.getValue();
            Languages lang;
            try {
                lang = Languages.valueOf(val);
            } catch (NullPointerException | IllegalArgumentException e) {
                lang = Languages.EN_US;
            }
            return Optional.of(lang);
        }

    }

}
