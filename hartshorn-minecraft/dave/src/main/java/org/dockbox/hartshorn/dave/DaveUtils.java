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

package org.dockbox.hartshorn.dave;

import net.dv8tion.jda.api.entities.TextChannel;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.PermissionHolder;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.api.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.dave.models.DaveResponse;
import org.dockbox.hartshorn.dave.models.DaveTrigger;
import org.dockbox.hartshorn.dave.models.DaveTriggers;
import org.dockbox.hartshorn.dave.models.ResponseType;
import org.dockbox.hartshorn.discord.DiscordUtils;
import org.dockbox.hartshorn.server.minecraft.Console;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class DaveUtils {

    private static final Map<DaveTrigger, LocalDateTime> timeSinceTrigger = HartshornUtils.emptyConcurrentMap();

    private DaveUtils() {}

    public static void toggleMute(Player player) {
        DaveResources resources = Hartshorn.context().get(DaveResources.class);
        final Boolean muted = Dave.MUTED_DAVE.get(player);
        if (muted) {
            player.remove(Dave.MUTED_DAVE);
            player.sendWithPrefix(resources.getUnmute());
        } else {
            player.set(Dave.MUTED_DAVE, true);
            player.sendWithPrefix(resources.getMute());
        }
    }

    public static Exceptional<DaveTrigger> findMatching(DaveTriggers triggers, String message) {
        for (DaveTrigger trigger : triggers.getTriggers()) {
            for (String rawTrigger : trigger.getTriggers()) {
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
        DaveResources resources = Hartshorn.context().get(DaveResources.class);
        Text message = Text.of(config.getPrefix());

        if (link) {
            Text linkText = Text.of(resources.getSuggestionLink(response));
            linkText.onClick(ClickAction.openUrl(response));
            linkText.onHover(
                    HoverAction.showText(resources.getSuggestionLinkHover(response).asText()));
            message.append(linkText);

        }
        else message.append(response);

        // Regular chat response
        Players pss = Hartshorn.context().get(Players.class);
        pss.getOnlinePlayers().stream()
                .filter(op -> important || !isMuted(op))
                .forEach(op -> op.send(message));

        // Discord response
        TextChannel discordChannel = config.getChannel();
        String discordMessage = response.replaceAll("§", "&");
        for (String regex : new String[]{ "(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r" })
            discordMessage = discordMessage.replaceAll(regex, "");

        Hartshorn.context().get(DiscordUtils.class).sendToTextChannel(resources.getDiscordFormat(discordMessage), discordChannel);
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
        DiscordUtils du = Hartshorn.context().get(DiscordUtils.class);

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
        return Dave.MUTED_DAVE.get(player);
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
        Players pss = Hartshorn.context().get(Players.class);
        int index = new Random().nextInt(pss.getOnlinePlayers().size());
        String randomPlayer = pss.getOnlinePlayers().toArray(new Player[0])[index].getName();

        if (null != randomPlayer) fullResponse = fullResponse.replaceAll("<random>", randomPlayer);
        else fullResponse = fullResponse.replaceAll("<random>", playerName + "*");
        return fullResponse;
    }
}
