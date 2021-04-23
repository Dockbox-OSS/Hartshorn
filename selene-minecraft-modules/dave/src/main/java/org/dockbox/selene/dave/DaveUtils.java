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

import net.dv8tion.jda.api.entities.TextChannel;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.i18n.PermissionHolder;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.i18n.text.actions.ClickAction;
import org.dockbox.selene.api.i18n.text.actions.HoverAction;
import org.dockbox.selene.api.keys.Keys;
import org.dockbox.selene.api.keys.PersistentDataKey;
import org.dockbox.selene.commands.source.CommandSource;
import org.dockbox.selene.dave.models.DaveResponse;
import org.dockbox.selene.dave.models.DaveTrigger;
import org.dockbox.selene.dave.models.DaveTriggers;
import org.dockbox.selene.dave.models.ResponseType;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.discord.DiscordUtils;
import org.dockbox.selene.server.minecraft.Console;
import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.server.minecraft.players.Players;
import org.dockbox.selene.util.SeleneUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class DaveUtils {

    public static final PersistentDataKey<Integer> mutedKey = Keys.persistent(Integer.class, "dave_muting", DaveModule.class);
    private static final Map<DaveTrigger, LocalDateTime> timeSinceTrigger = SeleneUtils.emptyConcurrentMap();

    private DaveUtils() {}

    public static void toggleMute(Player player) {
        player.get(mutedKey).present(state -> {
            if (1 == state) {
                player.remove(mutedKey);
                player.sendWithPrefix(DaveResources.DAVE_UNMUTED);

            }
            else {
                throw new IllegalStateException(
                        "Unexpected muted state value '" + state + "', was I modified externally?");
            }
        }).absent(() -> {
            player.set(mutedKey, 1);
            player.sendWithPrefix(DaveResources.DAVE_MUTED);
        });
    }

    public static Exceptional<DaveTrigger> findMatching(DaveTriggers triggers, String message) {
        for (DaveTrigger trigger : triggers.getTriggers()) {
            for (String rawTrigger : trigger.getRawTriggers()) {
                boolean containsAll = true;

                for (String keyword : rawTrigger.split(","))
                    if (!message.toLowerCase().replaceAll(",", "").contains(keyword.toLowerCase())) {
                        containsAll = (false);
                        break;
                    }

                if (containsAll) return Exceptional.of(trigger);
            }
        }
        return Exceptional.none();
    }

    public static void performTrigger(
            CommandSource source,
            String displayName,
            DaveTrigger trigger,
            String originalMessage,
            DaveConfig config
    ) {
        LocalDateTime timeOfLastTriggered = timeSinceTrigger.get(trigger);
        long secondsSinceLastResponse = 10;
        if (null != timeOfLastTriggered)
            secondsSinceLastResponse = timeOfLastTriggered.until(LocalDateTime.now(), ChronoUnit.SECONDS);

        if (null != trigger && 10 <= secondsSinceLastResponse)
            handleTrigger(source, displayName, trigger, originalMessage, config);
    }

    public static void handleTrigger(
            CommandSource source,
            String displayName,
            DaveTrigger trigger,
            String originalMessage,
            DaveConfig config
    ) {
        String perm = trigger.getPermission();

        if (null != perm) {
            if (source instanceof PermissionHolder) {
                if (!((PermissionHolder) source).hasPermission(perm)) return;
            }
            else return;
        }

        List<DaveResponse> responses = trigger.getResponses();

        boolean important = trigger.isImportant();

        responses.forEach(response -> {
            if (ResponseType.COMMAND == response.getType()) {
                executeCommand(source, response.getMessage());
            }
            else if (ResponseType.URL == response.getType()) {
                printResponse(DaveUtils.parseWebsiteLink(response.getMessage()), true, important, config);
            }
            else {
                printResponse(DaveUtils.parsePlaceHolders(originalMessage, response.getMessage(), displayName),
                        false,
                        important,
                        config);
            }
        });

        timeSinceTrigger.put(trigger, LocalDateTime.now());
    }

    private static void executeCommand(CommandSource source, String command) {
        if (source instanceof Player && isMuted((Player) source)) return;

        if (command.startsWith("*")) Console.getInstance().execute(command);
        else source.execute(command);
    }

    private static void printResponse(
            String response, boolean link, boolean important, DaveConfig config) {
        Text message = Text.of(config.getPrefix());

        if (link) {
            Text linkText = Text.of(DaveResources.DAVE_LINK_SUGGESTION.format(response));
            linkText.onClick(ClickAction.openUrl(response));
            linkText.onHover(
                    HoverAction.showText(DaveResources.DAVE_LINK_SUGGESTION_HOVER.format(response).asText()));
            message.append(linkText);

        }
        else message.append(response);

        // Regular chat response
        Players pss = Provider.provide(Players.class);
        pss.getOnlinePlayers().stream()
                .filter(op -> important || !isMuted(op))
                .forEach(op -> op.send(message));

        // Discord response
        TextChannel discordChannel = config.getChannel();
        String discordMessage = response.replaceAll("§", "&");
        for (String regex : new String[]{ "(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r" })
            discordMessage = discordMessage.replaceAll(regex, "");

        Provider.provide(DiscordUtils.class)
                .sendToTextChannel(
                        DaveResources.DAVE_DISCORD_FORMAT.format(discordMessage), discordChannel);
    }

    public static String parseWebsiteLink(String unparsedLink) {
        String parsedLink = unparsedLink.replaceAll("\\[", "").replaceAll("]", "");
        if (!parsedLink.startsWith("http://") && !parsedLink.startsWith("https://"))
            parsedLink = "http://" + parsedLink;
        return parsedLink;
    }

    public static String parsePlaceHolders(
            String message, String unparsedResponse, String playerName) {
        String parsedResponse = unparsedResponse.replaceAll("<player>", playerName);
        DiscordUtils du = Provider.provide(DiscordUtils.class);

        if (parsedResponse.contains("<mention>")) {
            boolean replaced = false;
            for (String partial : message.split(" "))
                parsedResponse = parseMention(partial, parsedResponse, du);

            if (!replaced) parsedResponse = parsedResponse.replaceAll("<mention>", playerName);
        }

        if (parsedResponse.contains("<random>"))
            parsedResponse = parseRandomPlayer(parsedResponse, playerName);

        return parsedResponse;
    }

    public static boolean isMuted(Player player) {
        return player.get(mutedKey).map(state -> 1 == state).or(false);
    }

    private static String parseMention(String partial, String fullResponse, DiscordUtils du) {
        if (partial.startsWith("<@") && 2 < partial.length()) {
            String mention = du.getJDA().map(jda ->
                    jda.getUserById(partial.replaceFirst("<@", "").replaceFirst(">", "")).getName()
            ).or("player");

            if (null != mention) {
                fullResponse = fullResponse.replaceAll("<mention>", partial.replaceFirst("<@", "").replaceFirst(">", ""));
            }
        }
        else if (Text.of(partial).toPlain().startsWith("@") && 2 < partial.length()) {
            fullResponse = fullResponse.replaceAll("<mention>", partial.replaceFirst("@", ""));
        }
        return fullResponse;
    }

    private static String parseRandomPlayer(String fullResponse, String playerName) {
        Players pss = Provider.provide(Players.class);
        int index = new Random().nextInt(pss.getOnlinePlayers().size());
        String randomPlayer = pss.getOnlinePlayers().toArray(new Player[0])[index].getName();

        if (null != randomPlayer) fullResponse = fullResponse.replaceAll("<random>", randomPlayer);
        else fullResponse = fullResponse.replaceAll("<random>", playerName + "*");
        return fullResponse;
    }
}
