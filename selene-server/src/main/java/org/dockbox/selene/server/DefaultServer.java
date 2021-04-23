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

package org.dockbox.selene.server;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneBootstrap;
import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.domain.AbstractIdentifiable;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.i18n.MessageReceiver;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.i18n.text.actions.HoverAction;
import org.dockbox.selene.api.i18n.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.module.ModuleContainer;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.commands.CommandBus;
import org.dockbox.selene.commands.RunCommandAction;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.commands.context.CommandParameter;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.server.events.ServerReloadEvent;
import org.dockbox.selene.util.SeleneUtils;

import java.util.List;

@Module(
        id = SeleneInformation.PROJECT_ID,
        name = SeleneInformation.PROJECT_NAME,
        description = "Integrated features of Selene",
        authors = "GuusLieben"
)
@Command(aliases = SeleneInformation.PROJECT_ID, usage = SeleneInformation.PROJECT_ID, permission = DefaultServerResources.SELENE_ADMIN)
public class DefaultServer implements Server {

    // Parent command
    @Command(aliases = "", usage = "", permission = DefaultServerResources.SELENE_ADMIN)
    public static void debugModules(MessageReceiver source) {
        Provider.with(ModuleManager.class, em -> {
            PaginationBuilder pb = Provider.provide(PaginationBuilder.class);

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

            em.getRegisteredModuleIds().forEach(id -> em.getContainer(id)
                    .map(e -> DefaultServer.generateText(e, source))
                    .present(content::add)
            );

            pb.title(DefaultServerResources.PAGINATION_TITLE.translate(source).asText());
            pb.content(content);

            source.send(pb.build());
        });
    }

    private static Text generateText(ModuleContainer e, MessageReceiver source) {
        Text line = DefaultServerResources.MODULE_ROW
                .format(e.name(), e.id())
                .translate(source)
                .asText();
        line.onClick(RunCommandAction.runCommand("/" + SeleneInformation.PROJECT_ID + " module " + e.id()));
        line.onHover(HoverAction.showText(DefaultServerResources.MODULE_ROW_HOVER
                .format(e.name())
                .translate(source)
                .asText()
        ));
        return line;
    }

    @Command(aliases = "module", usage = "module <id{Module}>", permission = DefaultServerResources.SELENE_ADMIN)
    public static void debugModule(MessageReceiver src, CommandContext ctx) {
        ModuleContainer container = ctx.get("id");

        src.send(DefaultServerResources.MODULE_INFO_BLOCK.format(
                container.name(), container.id(), container.description(),
                0 == container.dependencies().length ? "None" : String.join("$3, $1", container.dependencies()),
                String.join("$3, $1", container.authors()), container.source()
        ));
    }

    @Command(aliases = "reload", usage = "reload [id{Module}]", confirm = true, permission = DefaultServerResources.SELENE_ADMIN)
    public static void reload(MessageReceiver src, CommandContext ctx) {
        EventBus eb = Provider.provide(EventBus.class);
        if (ctx.has("id")) {
            ModuleContainer container = ctx.get("id");
            Exceptional<?> oi = Provider.provide(ModuleManager.class).getInstance(container.id());

            oi.present(o -> {
                eb.post(new ServerReloadEvent(), o.getClass());
                src.send(DefaultServerResources.MODULE_RELOAD_SUCCESSFUL.format(container.name()));
            }).absent(() ->
                    src.send(DefaultServerResources.NODULE_RELOAD_FAILED.format(container.name())));
        }
        else {
            eb.post(new ServerReloadEvent());
            src.send(DefaultServerResources.FULL_RELOAD_SUCCESSFUL);
        }
    }

    @Override
    @Command(aliases = "confirm", usage = "confirm <cooldownId{String}>", permission = SeleneInformation.GLOBAL_PERMITTED)
    public void confirm(MessageReceiver src, CommandContext ctx) {
        if (!(src instanceof AbstractIdentifiable)) {
            src.send(DefaultServerResources.CONFIRM_WRONG_SOURCE);
            return;
        }
        Exceptional<CommandParameter<String>> optionalCooldownId = ctx.argument("cooldownId");

        // UUID is stored by the command executor to ensure runnables are not called by other sources. The uuid
        // argument here is just a confirmation that the source is correct.
        optionalCooldownId
                .present(cooldownId -> {
                    String cid = cooldownId.getValue();
                    Provider.provide(CommandBus.class).confirmCommand(cid).absent(() ->
                            src.send(DefaultServerResources.CONFIRM_FAILED));
                })
                .absent(() -> src.send(DefaultServerResources.CONFIRM_INVALID_ID));
    }

}
