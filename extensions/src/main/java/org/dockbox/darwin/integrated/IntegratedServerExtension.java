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

package org.dockbox.darwin.integrated;

import org.dockbox.darwin.core.annotations.Command;
import org.dockbox.darwin.core.command.context.CommandContext;
import org.dockbox.darwin.core.command.context.CommandValue.Argument;
import org.dockbox.darwin.core.command.parse.impl.LanguageArgumentParser;
import org.dockbox.darwin.core.events.server.ServerEvent.Reload;
import org.dockbox.darwin.core.i18n.common.Language;
import org.dockbox.darwin.core.i18n.common.ResourceEntry;
import org.dockbox.darwin.core.i18n.entry.ExternalResourceEntry;
import org.dockbox.darwin.core.i18n.entry.IntegratedResource;
import org.dockbox.darwin.core.objects.targets.CommandSource;
import org.dockbox.darwin.core.objects.targets.MessageReceiver;
import org.dockbox.darwin.core.objects.user.Player;
import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.server.ServerReference;
import org.dockbox.darwin.core.text.Text;
import org.dockbox.darwin.core.text.actions.ClickAction.RunCommand;
import org.dockbox.darwin.core.text.actions.HoverAction.ShowText;
import org.dockbox.darwin.core.text.navigation.PaginationBuilder;
import org.dockbox.darwin.core.text.navigation.PaginationService;
import org.dockbox.darwin.core.util.Utils;
import org.dockbox.darwin.core.util.events.EventBus;
import org.dockbox.darwin.core.util.extension.Extension;
import org.dockbox.darwin.core.util.extension.ExtensionContext;
import org.dockbox.darwin.core.util.extension.ExtensionManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Extension(
        id = "darwinserver",
        name = "Darwin Server",
        description = "Integrated features of DarwinServer",
        authors = {"GuusLieben"},
        url = "https://github.com/GuusLieben/DarwinServer"
)
@Command(aliases = {"dserver", "darwin"}, usage = "dserver")
public class IntegratedServerExtension extends ServerReference {

    private static final ResourceEntry PAGINATION_TITLE = new ExternalResourceEntry("$1Darwin Server Info", "darwinserver.pagination.title");
    private static final ResourceEntry SERVER_HEADER = new ExternalResourceEntry("$2DarwinServer $3($1Version$3: $1{0}$3)", "darwinserver.info.header");
    private static final ResourceEntry SERVER_UPDATE = new ExternalResourceEntry("$2Last updated$3: $1{0}", "darwinserver.info.updated");
    private static final ResourceEntry SERVER_AUTHORS = new ExternalResourceEntry("$2Authors$3: $1{0}", "darwinserver.info.authors");
    private static final ResourceEntry SERVER_EXTENSIONS = new ExternalResourceEntry("$2Extensions$3:", "darwinserver.info.extensions");
    private static final ResourceEntry EXTENSION_ROW = new ExternalResourceEntry("$3 - $1{0} $3- $2{1}", "darwinserver.info.extension.row");
    private static final ResourceEntry EXTENSION_ROW_HOVER = new ExternalResourceEntry("$2Details for '$1{0}$2'", "darwinserver.info.extension.hover");
    private static final ResourceEntry EXTENSION_INFO_BLOCK = new ExternalResourceEntry(
            String.join("",
                    Utils.repeat(IntegratedResource.DEFAULT_PAGINATION_PADDING.asString(), 20), "\n",
                    "$2Name : $1{0}", "\n",
                    "$2ID : $1{1}", "\n",
                    "$2Description : $1{2}", "\n",
                    "$2Version : $1{3}", "\n",
                    "$2URL : $1{4}", "\n",
                    "$2Dependencies : $1{5}", "\n",
                    "$2Requires NMS : $1{6}", "\n",
                    "$2Author(s) : $1{7}", "\n",
                    "$2Type : $1{8}", "\n",
                    "$2Source : $1{9}", "\n",
                    Utils.repeat(IntegratedResource.DEFAULT_PAGINATION_PADDING.asString(), 20)
            ), "darwinserver.info.extension.block");
    private static final ResourceEntry EXTENSION_UNKNOWN = new ExternalResourceEntry("$4Could not find extension with ID '{0}'", "darwinserver.info.extension.unknown");
    private static final ResourceEntry LANG_SWITCHED = new ExternalResourceEntry("$1Your preferred language has been switched to: $2{0}", "i18n.lang.updated");


    // Parent command
    @Command(aliases = "", usage = "")
    public void debugExtensions(MessageReceiver source) {
        super.consumeWithInstance(ExtensionManager.class, em -> {
            PaginationBuilder pb = super.getInstance(PaginationService.class).builder();

            List<Text> content = new ArrayList<>();
            content.add(Text.of(SERVER_HEADER.format(Server.getServer().getVersion())));
            content.add(Text.of(SERVER_UPDATE.format(Server.getServer().getLastUpdate())));
            content.add(Text.of(SERVER_AUTHORS.format(String.join(",", Server.getServer().getAuthors()))));
            content.add(Text.of(SERVER_EXTENSIONS));

            em.getRegisteredExtensionIds()
                    .forEach(id -> em.getHeader(id)
                            .map(this::generateText)
                            .ifPresent(content::add)
                    );

            pb.title(PAGINATION_TITLE.asText());
            pb.contents(content);

            source.sendPagination(pb.build());
        });
    }

    private Text generateText(Extension e) {
        Text line = Text.of(EXTENSION_ROW.format(e.name(), e.id()));
        line.onClick(new RunCommand("/dserver extension " + e.id()));
        line.onHover(new ShowText(Text.of(EXTENSION_ROW_HOVER.format(e.name()))));
        return line;
    }

    @Command(aliases = "extension", usage = "extension <id{Extension}>")
    public void debugExtension(MessageReceiver source, CommandContext ctx) {
        super.consumeWithInstance(ExtensionManager.class, em -> {
            Optional<Argument<Extension>> oarg = ctx.getArgument("id", Extension.class);
            if (!oarg.isPresent()) return; // TODO, message

            Extension e = oarg.get().getValue();
            Optional<ExtensionContext> oec = em.getContext(e.id());

            if (!oec.isPresent()) {
                source.sendWithPrefix(EXTENSION_UNKNOWN.format(oarg.get().getValue()));
            } else {
                ExtensionContext ec = oec.get();

                source.send(Text.of(EXTENSION_INFO_BLOCK.format(
                        e.name(), e.id(), e.description(), e.version(), e.url(),
                        0 == e.dependencies().length ? "None" : String.join("$3, $1", e.dependencies()),
                        e.requiresNMS(),
                        String.join("$3, $1", e.authors()),
                        ec.getType().getString(),
                        ec.getSource()
                )));
            }
        });
    }

    @Command(aliases = {"lang", "language"}, usage = "language <language{String}> [player{Player}] -s --f flag{String}", single = true)
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
        src.sendWithPrefix(LANG_SWITCHED.format(lang.getNameLocalized() + " (" + lang.getNameEnglish() + ")"));
    }

}
