package com.darwinreforged.server.modules.extensions.chat.dave;

import com.darwinreforged.server.core.entities.chat.ClickEvent;
import com.darwinreforged.server.core.entities.chat.ClickEvent.ClickAction;
import com.darwinreforged.server.core.entities.chat.HoverEvent;
import com.darwinreforged.server.core.entities.chat.HoverEvent.HoverAction;
import com.darwinreforged.server.core.entities.chat.Text;
import com.darwinreforged.server.core.entities.chat.TextBuilder;
import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.events.internal.chat.DiscordChatEvent;
import com.darwinreforged.server.core.events.internal.chat.SendChatMessageEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.util.DiscordUtils;
import com.darwinreforged.server.core.util.TimeUtils;
import com.darwinreforged.server.core.util.PlayerUtils;
import com.darwinreforged.server.modules.extensions.chat.dave.DaveTrigger.Response;

import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;

public class DaveChatListeners {

    private Text botPrefix;
    private String color;

    @Listener
    public void onDiscordChat(DiscordChatEvent event) {
        beforeEach();
        String playername = event.getMember().getEffectiveName();
        String message = event.getMessage().getContentRaw();
        String guild = event.getGuild().getId();

        if (guild.equals("341249512586608640") && event
                .getChannel()
                .getName()
                .equals("global")) {
            DarwinPlayer console = DarwinServer.getUtilChecked(PlayerUtils.class).getConsole();
            Executor.beforeExecution(playername, message, botPrefix, color, console);
            DarwinServer.getUtilChecked(TimeUtils.class).schedule()
                    .execute(new Executor())
                    .delayTicks(5)
                    .submit();
        }
    }

    private void beforeEach() {
        Optional<DaveChatModule> chatModuleOptional = DarwinServer.getModule(DaveChatModule.class);
        if (chatModuleOptional.isPresent()) {
            botPrefix = chatModuleOptional.get().getConfigurationUtil().getPrefix();
            color = chatModuleOptional.get().getConfigurationUtil().getMessageDefaultColor().replaceAll("&", "\u00A7");
        }
    }

    @Listener
    public void onGameChat(SendChatMessageEvent event) {
        beforeEach();
        String playername = event.getTarget().getName();
        UUID consoleId = DarwinServer.getUtilChecked(PlayerUtils.class).getConsoleId();

        if (event.isGlobalChat())
            if (!event.getTarget().getUniqueId().equals(consoleId)) {
                Executor.beforeExecution(playername, event.getMessage(), botPrefix, color, (DarwinPlayer) event.getTarget());
                DarwinServer.getUtilChecked(TimeUtils.class).schedule()
                        .execute(new Executor())
                        .delayTicks(5)
                        .submit();
            }
    }

    private static class Executor
            implements Runnable {

        static String playername;
        static String message;
        static Text botPrefix;
        static String color;
        static DarwinPlayer player;
        static HashMap<DaveTrigger, LocalDateTime> timeSinceTrigger = new HashMap<>();

        static void beforeExecution(String playername1, String message1, Text botPrefix1, String color1, DarwinPlayer player1) {
            playername = playername1;
            message = message1;
            botPrefix = botPrefix1;
            color = color1;
            player = player1;
        }

        @Override
        public void run() {
            DaveTrigger trigger = DaveRawUtils.getAssociatedTrigger(message);

            LocalDateTime timeOfLastTriggered = timeSinceTrigger.get(trigger);
            long secondsSinceLastResponse = 10;
            if (timeOfLastTriggered != null)
                secondsSinceLastResponse = timeOfLastTriggered.until(LocalDateTime.now(), SECONDS);

            if (trigger != null && secondsSinceLastResponse >= 10) {
                List<Response> responses = trigger.getResponses();

                boolean important = trigger.isImportant();

                responses.forEach(response -> {
                    if (response.getType().equals("cmd")) {
                        executeCommand(response.getMessage());
                    } else if (response.getType().equals("url")) {
                        printResponse(DaveRawUtils.parseWebsiteLink(response.getMessage()), true, important);
                    } else {
                        printResponse(DaveRawUtils.parsePlaceHolders(message, response.getMessage(), playername), false, important);
                    }
                });

                timeSinceTrigger.put(trigger, LocalDateTime.now());
            }

        }

        private void executeCommand(String command) {
            player.execute(command.replaceFirst("/", ""));
        }

        private void printResponse(String response, boolean link, boolean important) {
            TextBuilder builder = TextBuilder.empty();
            builder.append(botPrefix).append(color);

            if (link) {
                Text linkSuggestion = new Text(Translations.DAVE_LINK_SUGGESTION.f(response))
                        .setClickEvent(new ClickEvent(ClickAction.OPEN_URL, response))
                        .setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT, Translations.DAVE_LINK_SUGGESTION_HOVER.f(response)));
                builder.append(linkSuggestion);
            }

            PlayerUtils pu = DarwinServer.getUtilChecked(PlayerUtils.class);

            DarwinServer.getModule(DaveChatModule.class).ifPresent(dave -> {
                pu.getOnlinePlayers().stream()
                        .filter(op -> !dave.getPlayerWhoMutedDave().contains(op.getUniqueId()) || important)
                        .forEach(op -> op.tell(builder.build()));
                TextChannel discordChannel = dave.getConfigurationUtil().getChannel();

                String discordMessage = response.replaceAll("§", "&");
                for (String regex : new String[]{"(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r"})
                    discordMessage = discordMessage.replaceAll(regex, "");

                DarwinServer.getUtilChecked(DiscordUtils.class).sendToChannel(Translations.DAVE_DISCORD_FORMAT.f(discordMessage), discordChannel.getId());
            });
        }
    }
}
