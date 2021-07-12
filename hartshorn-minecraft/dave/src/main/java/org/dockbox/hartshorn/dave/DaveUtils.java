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
import java.util.Objects;
import java.util.Random;

public final class DaveUtils {

    private static final Map<DaveTrigger, LocalDateTime> timeSinceTrigger = HartshornUtils.emptyConcurrentMap();

    private DaveUtils() {}

    public static void toggleMute(Player player) {
        DaveResources resources = Hartshorn.context().get(DaveResources.class);
        final Boolean muted = Dave.MUTED_DAVE.get(player);
        if (muted) {
            player.remove(Dave.MUTED_DAVE);
            player.sendWithPrefix(resources.unmute());
        } else {
            player.set(Dave.MUTED_DAVE, true);
            player.sendWithPrefix(resources.mute());
        }
    }

    public static Exceptional<DaveTrigger> findMatching(DaveTriggers triggers, String message) {
        for (DaveTrigger trigger : triggers.triggers()) {
            for (String rawTrigger : trigger.triggers()) {
                boolean containsAll = true;

                for (String keyword : rawTrigger.split(","))
                    if (!message.toLowerCase().replaceAll(",", "").contains(keyword.toLowerCase())) {
                        containsAll = (false);
                        break;
                    }

                if (containsAll) return Exceptional.of(trigger);
            }
        }
        return Exceptional.empty();
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
        String perm = trigger.permission();

        if (null != perm) {
            if (source instanceof PermissionHolder) {
                if (!((PermissionHolder) source).hasPermission(perm)) return;
            }
            else return;
        }

        List<DaveResponse> responses = trigger.responses();

        boolean important = trigger.important();

        responses.forEach(response -> {
            if (ResponseType.COMMAND == response.type()) {
                executeCommand(source, response.message());
            }
            else if (ResponseType.URL == response.type()) {
                printResponse(DaveUtils.parseWebsiteLink(response.message()), true, important, config);
            }
            else {
                printResponse(DaveUtils.parsePlaceHolders(originalMessage, response.message(), displayName),
                        false,
                        important,
                        config);
            }
        });

        timeSinceTrigger.put(trigger, LocalDateTime.now());
    }

    private static void executeCommand(CommandSource source, String command) {
        if (source instanceof Player && muted((Player) source)) return;

        if (command.startsWith("*")) Console.instance().execute(command);
        else source.execute(command);
    }

    private static void printResponse(
            String response, boolean link, boolean important, DaveConfig config) {
        DaveResources resources = Hartshorn.context().get(DaveResources.class);
        Text message = Text.of(config.prefix());

        if (link) {
            Text linkText = Text.of(resources.suggestionLink(response));
            linkText.onClick(ClickAction.openUrl(response));
            linkText.onHover(
                    HoverAction.showText(resources.suggestionLinkHover(response).asText()));
            message.append(linkText);

        }
        else message.append(response);

        // Regular chat response
        Players pss = Hartshorn.context().get(Players.class);
        pss.onlinePlayers().stream()
                .filter(op -> important || !muted(op))
                .forEach(op -> op.send(message));

        // Discord response
        TextChannel discordChannel = config.channel();
        String discordMessage = response.replaceAll("§", "&");
        for (String regex : new String[]{ "(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r" })
            discordMessage = discordMessage.replaceAll(regex, "");

        Hartshorn.context().get(DiscordUtils.class).sendToTextChannel(resources.discordFormat(discordMessage), discordChannel);
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
            for (String partial : message.split(" "))
                parsedResponse = parseMention(partial, parsedResponse, du);

            parsedResponse = parsedResponse.replaceAll("<mention>", playerName);
        }

        if (parsedResponse.contains("<random>"))
            parsedResponse = parseRandomPlayer(parsedResponse, playerName);

        return parsedResponse;
    }

    public static boolean muted(Player player) {
        return Dave.MUTED_DAVE.get(player);
    }

    private static String parseMention(String partial, String fullResponse, DiscordUtils du) {
        if (partial.startsWith("<@") && 2 < partial.length()) {
            String mention = du.jda().map(jda ->
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
        int index = new Random().nextInt(pss.onlinePlayers().size());
        String randomPlayer = pss.onlinePlayers().toArray(new Player[0])[index].name();

        return fullResponse.replaceAll("<random>", Objects.requireNonNullElseGet(randomPlayer, () -> playerName + "*"));
    }
}
