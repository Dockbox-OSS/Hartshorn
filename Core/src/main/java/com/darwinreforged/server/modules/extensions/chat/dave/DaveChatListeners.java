package com.darwinreforged.server.modules.extensions.chat.dave;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.chat.ClickEvent;
import com.darwinreforged.server.core.chat.ClickEvent.ClickAction;
import com.darwinreforged.server.core.chat.DiscordChatManager;
import com.darwinreforged.server.core.chat.HoverEvent;
import com.darwinreforged.server.core.chat.HoverEvent.HoverAction;
import com.darwinreforged.server.core.chat.Text;
import com.darwinreforged.server.core.events.internal.chat.DiscordChatEvent;
import com.darwinreforged.server.core.events.internal.chat.SendChatMessageEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.player.DarwinPlayer;
import com.darwinreforged.server.core.player.PlayerManager;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.types.living.Console;
import com.darwinreforged.server.core.util.TimeUtils;
import com.darwinreforged.server.modules.extensions.chat.dave.DaveTrigger.Response;

import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 The type Dave chat listeners.
 */
public class DaveChatListeners {

    private Text botPrefix;
    private String botDefaultColor;

    /**
     On discord chat.

     @param event
     the event
     */
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
            Console console = Console.instance;
            Executor.beforeExecution(playername, message, botPrefix, botDefaultColor, console);
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
            botDefaultColor = chatModuleOptional.get().getConfigurationUtil().getMessageDefaultColor().replaceAll("&", "\u00A7");
        }
    }

    /**
     On game chat.

     @param event
     the event
     */
    @Listener
    public void onGameChat(SendChatMessageEvent event) {
        beforeEach();
        String playername = event.getTarget().getName();

        if (event.isGlobalChat())
            if (!(event.getTarget() instanceof Console)) {
                Executor.beforeExecution(playername, event.getMessage(), botPrefix, botDefaultColor, (DarwinPlayer) event.getTarget());
                DarwinServer.getUtilChecked(TimeUtils.class).schedule()
                        .execute(new Executor())
                        .delayTicks(5)
                        .submit();
            }
    }

    public static class Executor
            implements Runnable {

        /**
         The Playername.
         */
        static String playername;
        /**
         The Message.
         */
        static String message;
        /**
         The Bot prefix.
         */
        static Text botPrefix;
        /**
         The Color.
         */
        static String color;
        /**
         The Sender.
         */
        static CommandSender sender;
        /**
         The Time since trigger.
         */
        static HashMap<DaveTrigger, LocalDateTime> timeSinceTrigger = new HashMap<>();

        /**
         Before execution.

         @param playername
         the playername
         @param message
         the message
         @param botPrefix
         the bot prefix
         @param color
         the color
         @param sender
         the sender
         */
        static void beforeExecution(String playername, String message, Text botPrefix, String color, CommandSender sender) {
            Executor.playername = playername;
            Executor.message = message;
            Executor.botPrefix = botPrefix;
            Executor.color = color;
            Executor.sender = sender;
        }

        @Override
        public void run() {
            DaveTrigger trigger = DaveRawUtils.getAssociatedTrigger(message);

            LocalDateTime timeOfLastTriggered = timeSinceTrigger.get(trigger);
            long secondsSinceLastResponse = 10;
            if (timeOfLastTriggered != null)
                secondsSinceLastResponse = timeOfLastTriggered.until(LocalDateTime.now(), SECONDS);

            if (trigger != null && secondsSinceLastResponse >= 10) handleTrigger(trigger);

        }

        public static void handleTrigger(DaveTrigger trigger) {
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

        private static void executeCommand(String command) {
            sender.execute(command.replaceFirst("/", ""));
        }

        private static void printResponse(String response, boolean link, boolean important) {
            Text message = Text.of(botPrefix.getText());
            message.append(color);

            if (link) {
                Text linkSuggestion = Text.of(Translations.DAVE_LINK_SUGGESTION.f(response))
                        .setClickEvent(new ClickEvent(ClickAction.OPEN_URL, response))
                        .setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT, Translations.DAVE_LINK_SUGGESTION_HOVER.f(response)));
                message.append(linkSuggestion);
            } else message.append(Text.of(response));

            PlayerManager pu = DarwinServer.getUtilChecked(PlayerManager.class);

            DarwinServer.getModule(DaveChatModule.class).ifPresent(dave -> {
                // Regular chat module
                pu.getOnlinePlayers().stream()
                        .filter(op -> !dave.getPlayerWhoMutedDave().contains(op.getUniqueId()) || important)
                        .forEach(op -> op.sendMessage(message, true));

                // Discord module
                TextChannel discordChannel = dave.getConfigurationUtil().getChannel();
                String discordMessage = response.replaceAll("§", "&");
                for (String regex : new String[]{"(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r"})
                    discordMessage = discordMessage.replaceAll(regex, "");

                DarwinServer.getUtilChecked(DiscordChatManager.class).sendToChannel(Translations.DAVE_DISCORD_FORMAT.f(discordMessage), discordChannel.getId());
            });
        }
    }
}
