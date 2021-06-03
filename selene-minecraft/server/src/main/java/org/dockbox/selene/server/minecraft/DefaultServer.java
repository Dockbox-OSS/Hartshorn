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
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.i18n.MessageReceiver;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.i18n.text.actions.HoverAction;
import org.dockbox.selene.api.i18n.text.pagination.PaginationBuilder;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.services.ServiceContainer;
import org.dockbox.selene.server.minecraft.events.server.ServerReloadEvent;
import org.dockbox.selene.util.SeleneUtils;

import java.util.List;

@Service(id = SeleneInformation.PROJECT_ID, name = SeleneInformation.PROJECT_NAME, owner = Selene.class)
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
        PaginationBuilder paginationBuilder = this.context.get(PaginationBuilder.class);

        List<Text> content = SeleneUtils.emptyList();
        content.add(this.resources.getInfoHeader(Selene.server().getVersion()).translate(source).asText());
        content.add(this.resources.getServices().translate(source).asText());

        for (ServiceContainer container : this.context.locator().containers()) {
            final Text row = this.resources.getServiceRow(container.getName(), container.getId()).translate(source).asText();
            row.onHover(HoverAction.showText(this.resources.getServiceRowHover(container.getName()).translate(source).asText()));
            content.add(row);
        }

        paginationBuilder.title(this.resources.getPaginationTitle().translate(source).asText());
        paginationBuilder.content(content);

        source.send(paginationBuilder.build());
    }

    @Command(value = "service", arguments = "<id{Service}>", permission = DefaultServer.SELENE_ADMIN)
    public void serviceDetails(MessageReceiver src, CommandContext ctx) {
        ServiceContainer container = ctx.get("id");

        final ResourceEntry block = this.resources.getInfoServiceBlock(
                container.getName(),
                container.getId(),
                0 == container.getDependencies().size() ? "None" : String.join("$3, $1", container.getDependencies())
        );

        src.send(block);
    }

    @Command(value = "reload", confirm = true, permission = DefaultServer.SELENE_ADMIN)
    public void reload(MessageReceiver src, CommandContext ctx) {
        EventBus eb = this.context.get(EventBus.class);
        eb.post(new ServerReloadEvent());
        src.send(this.resources.getReloadAll());
    }
}
