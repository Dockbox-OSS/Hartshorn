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

package org.dockbox.selene.integrated;

import org.dockbox.selene.core.annotations.Command;
import org.dockbox.selene.core.command.CommandBus;
import org.dockbox.selene.core.command.context.CommandContext;
import org.dockbox.selene.core.command.context.CommandValue.Argument;
import org.dockbox.selene.core.command.parse.impl.LanguageArgumentParser;
import org.dockbox.selene.core.command.parse.impl.UUIDArgumentParser;
import org.dockbox.selene.core.events.server.ServerEvent.Reload;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.targets.CommandSource;
import org.dockbox.selene.core.objects.targets.Identifiable;
import org.dockbox.selene.core.objects.targets.MessageReceiver;
import org.dockbox.selene.core.objects.user.Player;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.ServerReference;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.actions.ClickAction.RunCommand;
import org.dockbox.selene.core.text.actions.HoverAction.ShowText;
import org.dockbox.selene.core.text.navigation.PaginationBuilder;
import org.dockbox.selene.core.text.navigation.PaginationService;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.core.util.extension.Extension;
import org.dockbox.selene.core.util.extension.ExtensionContext;
import org.dockbox.selene.core.util.extension.ExtensionManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Extension(
        id = "selene",
        name = "Selene",
        description = "Integrated features of Selene",
        authors = {"GuusLieben"},
        url = "https://github.com/GuusLieben/Selene"
)
@Command(aliases = {"selene", "darwin"}, usage = "selene")
public class IntegratedServerExtension extends ServerReference {

    // Parent command
    @Command(aliases = "", usage = "")
    public void debugExtensions(MessageReceiver source) {
        super.consumeWithInstance(ExtensionManager.class, em -> {
            PaginationBuilder pb = super.getInstance(PaginationService.class).builder();

            List<Text> content = new ArrayList<>();
            content.add(Text.of(IntegratedServerResources.SERVER_HEADER.format(Selene.getServer().getVersion())));
            content.add(Text.of(IntegratedServerResources.SERVER_UPDATE.format(Selene.getServer().getLastUpdate())));
            content.add(Text.of(IntegratedServerResources.SERVER_AUTHORS.format(String.join(",", Selene.getServer().getAuthors()))));
            content.add(Text.of(IntegratedServerResources.SERVER_EXTENSIONS));

            em.getRegisteredExtensionIds()
                    .forEach(id -> em.getHeader(id)
                            .map(this::generateText)
                            .ifPresent(content::add)
                    );

            pb.title(IntegratedServerResources.PAGINATION_TITLE.asText());
            pb.contents(content);

            source.sendPagination(pb.build());
        });
    }

    private Text generateText(Extension e) {
        Text line = Text.of(IntegratedServerResources.EXTENSION_ROW.format(e.name(), e.id()));
        line.onClick(new RunCommand("/dserver extension " + e.id()));
        line.onHover(new ShowText(Text.of(IntegratedServerResources.EXTENSION_ROW_HOVER.format(e.name()))));
        return line;
    }

    @Command(aliases = "extension", usage = "extension <id{Extension}>")
    public void debugExtension(MessageReceiver src, CommandContext ctx) {
        super.consumeWithInstance(ExtensionManager.class, em -> {
            Optional<Argument<Extension>> oarg = ctx.getArgument("id", Extension.class);
            if (!oarg.isPresent()) {
                src.send(IntegratedServerResources.MISSING_ARGUMENT.format("id"));
                return;
            }

            Extension e = oarg.get().getValue();
            Optional<ExtensionContext> oec = em.getContext(e.id());

            if (!oec.isPresent()) {
                src.sendWithPrefix(IntegratedServerResources.EXTENSION_UNKNOWN.format(oarg.get().getValue()));
            } else {
                ExtensionContext ec = oec.get();

                src.send(Text.of(IntegratedServerResources.EXTENSION_INFO_BLOCK.format(
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

    @Command(aliases = "reload", usage = "reload [id{Extension}]", requireConfirm = true)
    public void reload(MessageReceiver src, CommandContext ctx) {
        EventBus eb = super.getInstance(EventBus.class);
        if (ctx.hasArgument("id")) {
            Optional<Argument<Extension>> oarg = ctx.getArgument("id", Extension.class);
            if (!oarg.isPresent()) {
                src.send(IntegratedServerResources.MISSING_ARGUMENT.format("id"));
                return;
            }

            Extension e = oarg.get().getValue();
            Exceptional<?> oi = Exceptional.ofOptional(Selene.getInstance(ExtensionManager.class).getInstance(e.id()));
            
            oi.ifPresent(o -> {
                eb.post(new Reload(), o.getClass());
                src.send(IntegratedServerResources.EXTENSION_RELOAD_SUCCESSFUL.format(e.name()));
            }).ifAbsent(() -> {
                src.send(IntegratedServerResources.EXTENSION_RELOAD_FAILED.format(e.name()));
            });
        } else {
            eb.post(new Reload());
            src.send(IntegratedServerResources.FULL_RELOAD_SUCCESSFUL);
        }
    }

    @Command(aliases = "confirm", usage = "confirm <uuid{String}>")
    public void confirm(CommandSource src, CommandContext ctx) {
        if (!(src instanceof Identifiable)) {
            src.send(IntegratedServerResources.CONFIRM_WRONG_SOURCE);
            return;
        }
        Optional<UUID> ouuid = ctx.getArgumentAndParse("uuid", new UUIDArgumentParser());

        // UUID is stored by the command executor to ensure runnables are not called by other sources. The uuid
        // argument here is just a confirmation that the source is correct.
        Exceptional.ofOptional(ouuid)
                .ifPresent(uuid -> {
                    if (((Identifiable) src).getUniqueId().equals(uuid))
                        super.getInstance(CommandBus.class).confirmLastCommand(uuid);
                    else
                        src.send(IntegratedResource.CONFIRM_EXPIRED);
                })
                .ifAbsent(() -> src.send(IntegratedServerResources.CONFIRM_INVALID_ID));
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
        src.sendWithPrefix(IntegratedServerResources.LANG_SWITCHED.format(lang.getNameLocalized() + " (" + lang.getNameEnglish() + ")"));
    }

}
