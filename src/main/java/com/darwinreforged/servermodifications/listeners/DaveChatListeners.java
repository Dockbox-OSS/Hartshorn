package com.darwinreforged.servermodifications.listeners;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.modules.DaveChatModule;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.plugins.DaveRawUtils;
import com.magitechserver.magibridge.DiscordHandler;
import com.magitechserver.magibridge.api.DiscordEvent;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;

import static java.time.temporal.ChronoUnit.SECONDS;

public class DaveChatListeners {

    private String botPrefix;
    private String color;

    @Listener
    public void onDiscordChat(DiscordEvent.MessageEvent event) {
        beforeEach();
        String playername = event.getMember().getEffectiveName();
        String message = event.getRawMessage();
        String guild = event.getGuild().getId();

        if (guild.equals("341249512586608640") && !playername.equals("DR") && !playername.equals("DR ≫ Dave") && event
                .getChannel()
                .getName()
                .equals("global")) {
            CommandSource source = Sponge.getServer().getConsole();
            Executor.beforeExecution(playername, message, botPrefix, color, source);
            Sponge.getScheduler().createTaskBuilder().execute(new Executor()).delayTicks(5)
                    .submit(DarwinServer.getServer());
        }
    }

    private void beforeEach() {
        Optional<DaveChatModule> chatModuleOptional = DarwinServer.getModule(DaveChatModule.class);
        if (chatModuleOptional.isPresent()) {
            botPrefix = chatModuleOptional.get().getSettingsProperties().getProperty("prefix").replaceAll("&", "§");
            color = chatModuleOptional.get().getSettingsProperties().getProperty("messageColor").replaceAll("&", "§");
        }
    }

    @Listener
    public void onGameChat(SendChannelMessageEvent event, @First Player player) {
        beforeEach();
        String playername = player.getName();

        if (event.getChannel().getName().equalsIgnoreCase("global"))
            if (player instanceof CommandSource) {
                Executor.beforeExecution(playername, event.getMessage().toPlain(), botPrefix, color, player);
                Sponge.getScheduler().createTaskBuilder().execute(new Executor()).delayTicks(5)
                        .submit(DarwinServer.getServer());
            }
    }

    private static class Executor
            implements Runnable {

        static String playername;
        static String message;
        static String botPrefix;
        static String color;
        static CommandSource player;
        static HashMap<Map.Entry, LocalDateTime> timeSinceTrigger = new HashMap<>();

        static void beforeExecution(String playername1, String message1, String botPrefix1, String color1, CommandSource player1) {
            playername = playername1;
            message = message1;
            botPrefix = botPrefix1;
            color = color1;
            player = player1;
        }

        @Override
        public void run() {
            Map.Entry trigger = DaveRawUtils.getAssociatedTrigger(message);

            LocalDateTime timeOfLastTriggered = timeSinceTrigger.get(trigger);
            long secondsSinceLastResponse = 10;
            if (timeOfLastTriggered != null)
                secondsSinceLastResponse = timeOfLastTriggered.until(LocalDateTime.now(), SECONDS);

            if (trigger != null && secondsSinceLastResponse >= 10) {
                String unparsedResponse = trigger.getValue().toString().replaceAll("&", "§");
                String[] potentialResponses = DaveRawUtils.parsePlaceHolders(message, unparsedResponse, playername)
                        .split("<:>");

                int index = new Random().nextInt(potentialResponses.length);
                String response = potentialResponses.length > 1 ? potentialResponses[index] : potentialResponses[0];

                String[] responseLayers = response.split("<>");

                boolean important = trigger.getKey().toString().startsWith("<!important>");

                Arrays.stream(responseLayers).forEach(partial -> {
                    if (partial.startsWith("/") || partial.startsWith("*/"))
                        executeCommand(partial);
                    else if (partial.startsWith("[") && partial.endsWith("]"))
                        printResponse(DaveRawUtils.parseWebsiteLink(partial), true, important);
                    else
                        printResponse(partial, false, important);
                });

                timeSinceTrigger.put(trigger, LocalDateTime.now());
            }

        }

        private void executeCommand(String command) {
            CommandSource executor = command.startsWith("*/") ? Sponge.getServer().getConsole() : player;
            if (command.startsWith("*/")) command = command.replaceFirst("\\*", "");
            Sponge.getCommandManager().process(executor, command.replaceFirst("/", ""));
        }

        private void printResponse(String response, boolean link, boolean important) {
            Text.Builder message = Text.builder().append(Text.of(botPrefix, color));
            if (link) try {
                message.append(Translations.DAVE_LINK_SUGGESTION.ft(response))
                        .onClick(TextActions.openUrl(new URL(response)))
                        .onHover(TextActions.showText(Translations.DAVE_LINK_SUGGESTION_HOVER.ft(response)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            else message.append(Text.of(response));

            DarwinServer.getModule(DaveChatModule.class).ifPresent(dave -> Sponge.getServer().getOnlinePlayers()
                    .stream().filter(player1 -> !dave
                            .getPlayerWhoMutedDave()
                            .contains(player1.getName()) || important)
                    .forEach(player1 -> PlayerUtils
                            .tell(player1, message.build())));

            Optional<DaveChatModule> chatModuleOptional = DarwinServer.getModule(DaveChatModule.class);
            if (chatModuleOptional.isPresent()) {
                String discordChannel = chatModuleOptional.get().getSettingsProperties().getProperty("discordChannel");

                String discordMessage = response.replaceAll("§", "&");
                for (String regex : new String[]{"(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r"})
                    discordMessage = discordMessage.replaceAll(regex, "");

                DiscordHandler.sendMessageToChannel(discordChannel, Translations.DAVE_DISCORD_FORMAT.f(discordMessage));
            }
        }
    }
}
