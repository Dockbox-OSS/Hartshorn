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

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.events.annotations.Listener;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.api.i18n.text.actions.HoverAction;
import org.dockbox.selene.api.i18n.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.api.task.TaskRunner;
import org.dockbox.selene.commands.RunCommandAction;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.commands.source.CommandSource;
import org.dockbox.selene.commands.source.DiscordCommandSource;
import org.dockbox.selene.dave.models.DaveTriggers;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.di.SeleneFactory;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.discord.events.DiscordChatReceivedEvent;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.server.events.ServerReloadEvent;
import org.dockbox.selene.server.minecraft.events.chat.SendChatEvent;
import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.util.SeleneUtils;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Command(aliases = "dave", usage = "dave", permission = "dave")
@Module(id = "dave", name = "Dave", description = "", authors = "GuusLieben")
public class DaveModule implements InjectableType {

    private static final int msTick = 20;
    private DaveTriggers triggers = new DaveTriggers();
    private DaveConfig config = new DaveConfig();

    public static final String DAVE_MUTE = "dave.mute";
    public static final String DAVE_REFRESH = "dave.refresh";
    public static final String DAVE_TRIGGERS = "dave.triggers.list";
    public static final String DAVE_TRIGGER_RUN = "dave.triggers.run";

    @Inject
    private DaveResources resources;

    @Command(aliases = "mute", usage = "mute", permission = DaveModule.DAVE_MUTE)
    public void mute(Player player) {
        DaveUtils.toggleMute(player);
    }

    @Command(aliases = "triggers", usage = "triggers", permission = DaveModule.DAVE_TRIGGERS)
    public void triggers(CommandSource source) {
        Provider.provide(PaginationBuilder.class)
                .title(this.resources.getTriggerHeader().asText())
                .content(this.triggers.getTriggers().stream()
                        .map(trigger -> this.resources.getTriggerSingle(SeleneUtils.shorten(trigger.getResponses().get(0).getMessage(), 75) + " ...")
                        .asText()
                        .onHover(HoverAction.showText(this.resources.getTriggerSingleHover().asText()))
                        .onClick(RunCommandAction.runCommand("/dave run " + trigger.getId())))
                        .collect(Collectors.toList()))
                .build()
                .send(source);
    }

    @Command(aliases = "run", usage = "run <trigger{String}>", permission = DaveModule.DAVE_TRIGGER_RUN)
    public void run(Player source, CommandContext context) {
        String triggerId = context.get("trigger");
        this.triggers.findById(triggerId)
                .present(trigger -> DaveUtils.performTrigger(source, source.getName(), trigger, "", this.config))
                .absent(() -> source.send(this.resources.getTriggerNotfound(triggerId)));
    }

    @Command(aliases = "refresh", usage = "refresh", permission = DaveModule.DAVE_REFRESH)
    public void refresh(CommandSource source) {
        this.stateEnabling();
        source.sendWithPrefix(this.resources.getReload());
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        FileManager fm = Provider.provide(FileManager.class);
        Path triggerFile = fm.getDataFile(DaveModule.class, "triggers");
        if (SeleneUtils.isFileEmpty(triggerFile)) this.restoreTriggerFile(fm, triggerFile);

        fm.read(triggerFile, DaveTriggers.class).present(triggers -> {
            Selene.log().info("Found " + triggers.getTriggers().size() + " triggers");
            this.triggers = triggers;
        }).caught(e -> {
            Selene.log().warn("Could not load triggers for Dave");
            Except.handle(e);
        });

        Path configFile = fm.getConfigFile(DaveModule.class);
        fm.read(configFile, DaveConfig.class)
                .present(config -> this.config = config)
                .caught(e -> {
                    Selene.log().warn("Could not load config for Dave");
                    Except.handle(e);
                });
    }

    private void restoreTriggerFile(FileManager fm, Path triggerFile) {
        fm.copyDefaultFile("dave_trigger.yml", triggerFile);
    }

    @Listener
    public void on(ServerReloadEvent event) {
        this.stateEnabling();
    }

    @Listener
    public void on(SendChatEvent sendChatEvent) {
        Player player = (Player) sendChatEvent.getTarget();
        DaveUtils.findMatching(this.triggers, sendChatEvent.getMessage().toPlain()).present(trigger -> Provider.provide(TaskRunner.class).acceptDelayed(() -> DaveUtils.performTrigger(
                player,
                player.getName(),
                trigger,
                sendChatEvent.getMessage().toPlain(),
                this.config),
                5 * msTick, TimeUnit.MILLISECONDS)
        );
    }

    @Listener
    public void on(DiscordChatReceivedEvent chatEvent) {
        if (chatEvent.getChannel().getId().equals(this.config.getChannel().getId())) {
            DaveUtils.findMatching(this.triggers, chatEvent.getMessage().getContentRaw()).present(trigger -> Provider.provide(TaskRunner.class).acceptDelayed(() -> DaveUtils.performTrigger(
                    Provider.provide(DiscordCommandSource.class, SeleneFactory.use(chatEvent.getChannel())),
                    chatEvent.getAuthor().getName(),
                    trigger,
                    chatEvent.getMessage().getContentRaw(),
                    this.config),
                    5 * msTick, TimeUnit.MILLISECONDS)
            );
        }
    }
}
