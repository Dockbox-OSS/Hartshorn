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

import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.i18n.MessageReceiver;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.server.events.ServerReloadEvent;

@Service(id = SeleneInformation.PROJECT_ID, name = SeleneInformation.PROJECT_NAME)
@Command(value = SeleneInformation.PROJECT_ID, permission = DefaultServer.SELENE_ADMIN)
public class DefaultServer {

    public static final String SELENE_ADMIN = SeleneInformation.PROJECT_ID + ".admin";

    @Wired
    private DefaultServerResources resources;
    @Wired
    private ApplicationContext context;

    // Parent command
    @Command(permission = DefaultServer.SELENE_ADMIN)
    public void allServices(MessageReceiver source) {
        // TODO: Find something for this
//        Selene.context().with(ModuleManager.class, em -> {
//            PaginationBuilder pb = this.context.get(PaginationBuilder.class);
//
//            List<Text> content = SeleneUtils.emptyList();
//            content.add(this.resources.getInfoHeader(Selene.server().getVersion())
//                    .translate(source).asText()
//            );
//            content.add(this.resources.getServices().translate(source).asText());
//
//            em.getRegisteredModuleIds().forEach(id -> em.getContainer(id)
//                    .map(e -> this.generateText(e, source))
//                    .present(content::add)
//            );
//
//            pb.title(this.resources.getPaginationTitle().translate(source).asText());
//            pb.content(content);
//
//            source.send(pb.build());
//        });
    }

    @Command(value = "service", arguments = "<id{Service}>", permission = DefaultServer.SELENE_ADMIN)
    public void serviceDetails(MessageReceiver src, CommandContext ctx) {
        // TODO: Migrate
//        ModuleContainer container = ctx.get("id");
//
//        src.send(this.resources.getInfoServiceBlock(
//                container.name(),
//                container.id(),
//                0 == container.dependencies().length ? "None" : String.join("$3, $1", container.dependencies())
//        ));
    }

    @Command(value = "reload", arguments = "[id{Service}]", confirm = true, permission = DefaultServer.SELENE_ADMIN)
    public void reload(MessageReceiver src, CommandContext ctx) {
        EventBus eb = this.context.get(EventBus.class);
        // TODO: Migrate
        if (ctx.has("id")) {
//            ModuleContainer container = ctx.get("id");
//            Exceptional<?> oi = this.context.get(ModuleManager.class).getInstance(container.id());
//
//            oi.present(o -> {
//                eb.post(new ServerReloadEvent(), o.getClass());
//                src.send(this.resources.getReloadSuccessful(container.name()));
//            }).absent(() ->
//                    src.send(this.resources.getReloadFailed(container.name())));
        }
        else {
            eb.post(new ServerReloadEvent());
            src.send(this.resources.getReloadAll());
        }
    }
}
