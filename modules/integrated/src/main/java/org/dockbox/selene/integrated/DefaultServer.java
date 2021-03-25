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

package org.dockbox.selene.integrated;

import com.google.inject.Singleton;

import org.dockbox.selene.api.annotations.command.Command;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.command.CommandBus;
import org.dockbox.selene.api.command.context.CommandContext;
import org.dockbox.selene.api.command.context.CommandArgument;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.events.server.ServerReloadEvent;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.entry.DefaultResource;
import org.dockbox.selene.api.module.ModuleContext;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.objects.targets.AbstractIdentifiable;
import org.dockbox.selene.api.objects.targets.MessageReceiver;
import org.dockbox.selene.api.server.Server;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.ServerType;
import org.dockbox.selene.api.server.bootstrap.SeleneBootstrap;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.text.actions.ClickAction;
import org.dockbox.selene.api.text.actions.HoverAction;
import org.dockbox.selene.api.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.List;

@Module(
        id = "selene",
        name = "Selene",
        description = "Integrated features of Selene",
        authors = "GuusLieben"
)
@Command(aliases = { "selene", "darwin" }, usage = "selene")
@Singleton
public class DefaultServer implements Server {

    // Parent command
    @Command(aliases = "", usage = "")
    public static void debugModules(MessageReceiver source) {
        Reflect.runWithInstance(ModuleManager.class, em -> {
            PaginationBuilder pb = Selene.provide(PaginationBuilder.class);

            List<Text> content = SeleneUtils.emptyList();
            content.add(DefaultServerResources.SERVER_HEADER
                    .format(Selene.getServer().getVersion())
                    .translate(source).asText()
            );
            content.add(DefaultServerResources.SERVER_UPDATE
                    .format(Selene.getServer().getLastUpdate())
                    .translate(source).asText()
            );
            content.add(DefaultServerResources.SERVER_AUTHORS
                    .format(String.join(",", SeleneBootstrap.getAuthors()))
                    .translate(source).asText());
            content.add(DefaultServerResources.SERVER_MODULES.translate(source).asText());

            em.getRegisteredModuleIds().forEach(id -> em.getHeader(id)
                    .map(e -> DefaultServer.generateText(e, source))
                    .ifPresent(content::add)
            );

            pb.title(DefaultServerResources.PAGINATION_TITLE.translate(source).asText());
            pb.content(content);

            source.sendPagination(pb.build());
        });
    }

    private static Text generateText(Module e, MessageReceiver source) {
        Text line = DefaultServerResources.MODULE_ROW
                .format(e.name(), e.id())
                .translate(source)
                .asText();
        line.onClick(ClickAction.runCommand("/dserver module " + e.id()));
        line.onHover(HoverAction.showText(DefaultServerResources.MODULE_ROW_HOVER
                .format(e.name())
                .translate(source)
                .asText()
        ));
        return line;
    }

    @Command(aliases = "module", usage = "module <id{Module}>")
    public static void debugModule(MessageReceiver src, CommandContext ctx) {
        Reflect.runWithInstance(ModuleManager.class, em -> {
            Exceptional<CommandArgument<Module>> oarg = ctx.argument("id");
            if (!oarg.isPresent()) {
                src.send(DefaultServerResources.MISSING_ARGUMENT.format("id"));
                return;
            }

            Module e = oarg.get().getValue();
            Exceptional<ModuleContext> oec = em.getContext(e.id());

            if (!oec.isPresent()) {
                src.sendWithPrefix(DefaultServerResources.UNKNOWN_MODULE.format(oarg.get().getValue()));
            }
            else {
                ModuleContext ec = oec.get();

                src.send(DefaultServerResources.MODULE_INFO_BLOCK.format(
                        e.name(), e.id(), e.description(),
                        0 == e.dependencies().length ? "None" : String.join("$3, $1", e.dependencies()),
                        String.join("$3, $1", e.authors()),
                        ec.getSource()
                ));
            }
        });
    }

    @Command(aliases = "reload", usage = "reload [id{Module}]", confirm = true)
    public static void reload(MessageReceiver src, CommandContext ctx) {
        EventBus eb = Selene.provide(EventBus.class);
        if (ctx.has("id")) {
            Exceptional<CommandArgument<Module>> oarg = ctx.argument("id");
            if (!oarg.isPresent()) {
                src.send(DefaultServerResources.MISSING_ARGUMENT.format("id"));
                return;
            }

            Module e = oarg.get().getValue();
            Exceptional<?> oi = Selene.provide(ModuleManager.class).getInstance(e.id());

            oi.ifPresent(o -> {
                eb.post(new ServerReloadEvent(), o.getClass());
                src.send(DefaultServerResources.MODULE_RELOAD_SUCCESSFUL.format(e.name()));
            }).ifAbsent(() ->
                    src.send(DefaultServerResources.NODULE_RELOAD_FAILED.format(e.name())));
        }
        else {
            eb.post(new ServerReloadEvent());
            src.send(DefaultServerResources.FULL_RELOAD_SUCCESSFUL);
        }
    }

    @Command(aliases = { "lang", "language" }, usage = "language <language{Language}> [player{Player}]", inherit = false)
    public static void switchLang(MessageReceiver src, CommandContext ctx, Language language, Player player) {
        if (null == player) {
            if (src instanceof Player) {
                player = (Player) src;
            }
            else {
                src.send(DefaultResource.CONFIRM_WRONG_SOURCE);
                return;
            }
        }

        player.setLanguage(language);

        String languageLocalized = language.getNameLocalized() + " (" + language.getNameEnglish() + ")";
        if (player != src)
            src.sendWithPrefix(DefaultServerResources.LANG_SWITCHED_OTHER.format(player.getName(), languageLocalized));
        player.sendWithPrefix(DefaultServerResources.LANG_SWITCHED.format(languageLocalized));
    }

    @Command(aliases = "platform", usage = "platform")
    public static void platform(MessageReceiver src) {
        ServerType st = Selene.getServer().getServerType();
        String platformVersion = Selene.getServer().getPlatformVersion();

        String mcVersion = Selene.getServer().getMinecraftVersion().getReadableVersionString();

        String[] system = SeleneUtils.getAll(System::getProperty,
                "java.version", "java.vendor", "java.vm.version", "java.vm.name", "java.vm.vendor", "java.runtime.version", "java.class.version");

        src.send(DefaultServerResources.PLATFORM_INFORMATION.format(
                st.getDisplayName(), platformVersion, mcVersion, system[0], system[1], system[2], system[2], system[3], system[4], system[5])
        );
    }

    @Override
    @Command(aliases = "confirm", usage = "confirm <cooldownId{String}>")
    public void confirm(MessageReceiver src, CommandContext ctx) {
        if (!(src instanceof AbstractIdentifiable)) {
            src.send(DefaultServerResources.CONFIRM_WRONG_SOURCE);
            return;
        }
        Exceptional<CommandArgument<String>> optionalCooldownId = ctx.argument("cooldownId");

        // UUID is stored by the command executor to ensure runnables are not called by other sources. The uuid
        // argument here is just a confirmation that the source is correct.
        optionalCooldownId
                .ifPresent(cooldownId -> {
                    String cid = cooldownId.getValue();
                    Selene.provide(CommandBus.class).confirmCommand(cid).ifAbsent(() ->
                            src.send(DefaultServerResources.CONFIRM_FAILED));
                })
                .ifAbsent(() -> src.send(DefaultServerResources.CONFIRM_INVALID_ID));
    }

}
