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

package org.dockbox.selene.dave;

import org.dockbox.selene.api.annotations.command.Command;
import org.dockbox.selene.api.annotations.event.Listener;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.command.context.CommandContext;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.events.chat.SendChatEvent;
import org.dockbox.selene.api.events.server.ServerEvent.ServerReloadEvent;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.properties.InjectableType;
import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.api.tasks.TaskRunner;
import org.dockbox.selene.api.text.actions.ClickAction;
import org.dockbox.selene.api.text.actions.HoverAction;
import org.dockbox.selene.api.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.dave.models.DaveTriggers;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Command(aliases = "dave", usage = "dave")
@Module(id = "dave", name = "Dave", description = "", authors = "GuusLieben")
public class DaveModule implements InjectableType
{

    private DaveTriggers triggers = new DaveTriggers();
    private DaveConfig config = new DaveConfig();

    @Command(aliases = "mute", usage = "mute")
    public void mute(Player player)
    {
        DaveUtils.toggleMute(player);
    }

    @Command(aliases = "triggers", usage = "triggers")
    public void triggers(CommandSource source)
    {
        Selene.provide(PaginationBuilder.class)
                .title(DaveResources.DAVE_TRIGGER_HEADER.asText())
                .content(this.triggers.getTriggers().stream()
                        .map(trigger -> {
                            //noinspection MagicNumber
                            return DaveResources.DAVE_TRIGGER_LIST_ITEM
                                    .format(SeleneUtils.shorten(
                                            trigger.getResponses().get(0).getMessage(),
                                            75) + " ..."
                                    ).asText()
                                    .onHover(HoverAction.showText(DaveResources.DAVE_TRIGGER_HOVER.asText()))
                                    .onClick(ClickAction.runCommand("/dave run " + trigger.getId()));
                        })
                        .collect(Collectors.toList()))
                .build()
                .send(source);
    }

    @Command(aliases = "run", usage = "run <trigger{String}>")
    public void run(Player source, CommandContext context)
    {
        String triggerId = context.get("trigger");
        this.triggers.findById(triggerId).ifPresent(trigger -> {
            DaveUtils.performTrigger(
                    source,
                    source.getName(),
                    trigger,
                    "",
                    this.config
            );
        }).ifAbsent(() -> {
            source.send(DaveResources.NO_MATCHING_TRIGGER.format(triggerId));
        });
    }

    @Command(aliases = "refresh", usage = "refresh")
    public void refresh(CommandSource source)
    {
        this.stateEnabling();
        source.sendWithPrefix(DaveResources.DAVE_RELOADED_USER);
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties)
    {
        FileManager fm = Selene.provide(FileManager.class);
        Path triggerFile = fm.getDataFile(DaveModule.class, "triggers");
        if (SeleneUtils.isFileEmpty(triggerFile)) this.restoreTriggerFile(fm, triggerFile);

        fm.read(triggerFile, DaveTriggers.class).ifPresent(triggers -> {
            this.triggers = triggers;
        }).ifAbsent(() -> {
            Selene.log().warn("Could not load triggers for Dave");
        });


        Path configFile = fm.getConfigFile(DaveModule.class);
        fm.read(configFile, DaveConfig.class).ifPresent(config -> this.config = config);
    }

    private void restoreTriggerFile(FileManager fm, Path triggerFile)
    {
        fm.copyDefaultFile("dave_trigger.yml", triggerFile);
    }

    @Listener
    public void reload(ServerReloadEvent event)
    {
        this.stateEnabling();
    }

    @Listener
    public void onChat(SendChatEvent sendChatEvent)
    {
        Player player = (Player) sendChatEvent.getTarget();
        DaveUtils.findMatching(this.triggers, sendChatEvent.getMessage().toPlain()).ifPresent(trigger -> {
            Selene.provide(TaskRunner.class).acceptDelayed(() -> {
                DaveUtils.performTrigger(
                        player,
                        player.getName(),
                        trigger,
                        sendChatEvent.getMessage().toPlain(),
                        this.config
                );
            }, 10, TimeUnit.MILLISECONDS);
        });
    }
}
