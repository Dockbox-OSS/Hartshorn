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
import org.dockbox.hartshorn.events.EventBus;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.i18n.MessageReceiver;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.i18n.text.pagination.PaginationBuilder;
import org.dockbox.hartshorn.commands.RunCommandAction;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.annotations.WithConfirmation;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.server.minecraft.events.server.EngineChangedState;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerState.Reload;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;

@Posting(EngineChangedState.class)
@Command(value = Hartshorn.PROJECT_ID, permission = DefaultServer.ADMIN)
public class DefaultServer {

    public static final String ADMIN = Hartshorn.PROJECT_ID + ".admin";

    @Wired
    private DefaultServerResources resources;
    @Wired
    private ApplicationContext context;

    // Parent command
    @Command(permission = DefaultServer.ADMIN)
    public void allServices(MessageReceiver source) {
        PaginationBuilder paginationBuilder = this.context.get(PaginationBuilder.class);

        List<Text> content = HartshornUtils.emptyList();
        content.add(this.resources.infoHeader(Hartshorn.server().version()).translate(source).asText());
        content.add(this.resources.services().translate(source).asText());

        for (ComponentContainer container : this.context.locator().containers()) {
            final Text row = this.resources.serviceRow(container.name(), container.id()).translate(source).asText();
            row.onHover(HoverAction.showText(this.resources.serviceRowHover(container.name()).translate(source).asText()));
            row.onClick(RunCommandAction.runCommand('/' + Hartshorn.PROJECT_ID + " service " + container.id()));
            content.add(row);
        }

        paginationBuilder.title(this.resources.paginationTitle().translate(source).asText());
        paginationBuilder.content(content);

        source.send(paginationBuilder.build());
    }

    @Command(value = "service", arguments = "<id{Service}>", permission = DefaultServer.ADMIN)
    public void serviceDetails(MessageReceiver src, CommandContext ctx) {
        ComponentContainer container = ctx.get("id");
        final ResourceEntry block = this.resources.infoServiceBlock(container.name(), container.id());
        src.send(block);
    }

    @Command(value = "reload", permission = DefaultServer.ADMIN)
    @WithConfirmation
    public void reload(MessageReceiver src, CommandContext ctx) {
        EventBus eb = this.context.get(EventBus.class);
        eb.post(new EngineChangedState<Reload>() {
        });
        src.send(this.resources.reloadAll());
    }
}
