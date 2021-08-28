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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.dave.models.DaveResponse;
import org.dockbox.hartshorn.dave.models.DaveTrigger;
import org.dockbox.hartshorn.dave.models.DaveTriggers;
import org.dockbox.hartshorn.dave.models.ResponseType;
import org.dockbox.hartshorn.di.annotations.component.Component;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.discord.DiscordUtils;
import org.dockbox.hartshorn.i18n.PermissionHolder;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.inject.Inject;

@Component(singleton = true)
public class DaveUtils {

    @Inject
    private ApplicationContext context;
    private final Map<DaveTrigger, LocalDateTime> timeSinceTrigger = HartshornUtils.emptyConcurrentMap();

    private DaveUtils() {}

    public void toggleMute(final Player player) {
        final DaveResources resources = this.context.get(DaveResources.class);
        final Boolean muted = Dave.MUTED_DAVE.get(player);
        if (muted) {
            player.remove(Dave.MUTED_DAVE);
            player.sendWithPrefix(resources.unmute());
        }
        else {
            player.set(Dave.MUTED_DAVE, true);
            player.sendWithPrefix(resources.mute());
        }
    }

    public static Exceptional<DaveTrigger> findMatching(final DaveTriggers triggers, final String message) {
        for (final DaveTrigger trigger : triggers.triggers()) {
            for (final String rawTrigger : trigger.triggers()) {
                boolean containsAll = true;

                for (final String keyword : rawTrigger.split(","))
                    if (!message.toLowerCase().replaceAll(",", "").contains(keyword.toLowerCase())) {
                        containsAll = (false);
                        break;
                    }

                if (containsAll) return Exceptional.of(trigger);
            }
        }
        return Exceptional.empty();
    }

    public void performTrigger(
            final CommandSource source,
            final String displayName,
            final DaveTrigger trigger,
            final String originalMessage,
            final DaveConfig config
    ) {
        final LocalDateTime timeOfLastTriggered = this.timeSinceTrigger.get(trigger);
        long secondsSinceLastResponse = 10;
        if (null != timeOfLastTriggered)
            secondsSinceLastResponse = timeOfLastTriggered.until(LocalDateTime.now(), ChronoUnit.SECONDS);

        if (null != trigger && 10 <= secondsSinceLastResponse)
            this.handleTrigger(source, displayName, trigger, originalMessage, config);
    }

    public void handleTrigger(
            final CommandSource source,
            final String displayName,
            final DaveTrigger trigger,
            final String originalMessage,
            final DaveConfig config
    ) {
        final String perm = trigger.permission();

        if (null != perm) {
            if (source instanceof PermissionHolder) {
                if (!((PermissionHolder) source).hasPermission(perm)) return;
            }
            else return;
        }

        final List<DaveResponse> responses = trigger.responses();

        final boolean important = trigger.important();

        responses.forEach(response -> {
            if (ResponseType.COMMAND == response.type()) {
                this.executeCommand(source, response.message());
            }
            else if (ResponseType.URL == response.type()) {
                this.printResponse(this.parseWebsiteLink(response.message()), true, important, config);
            }
            else {
                this.printResponse(this.parsePlaceHolders(originalMessage, response.message(), displayName),
                        false,
                        important,
                        config);
            }
        });

        this.timeSinceTrigger.put(trigger, LocalDateTime.now());
    }

    private void executeCommand(final CommandSource source, final String command) {
        if (source instanceof Player && this.muted((Player) source)) return;

        if (command.startsWith("*")) source.applicationContext().get(SystemSubject.class).execute(command);
        else source.execute(command);
    }

    private void printResponse(final String response, final boolean link, final boolean important, final DaveConfig config) {
        final DaveResources resources = this.context.get(DaveResources.class);
        final Text message = Text.of(config.prefix());

        if (link) {
            final Text linkText = Text.of(resources.suggestionLink(response));
            linkText.onClick(ClickAction.openUrl(response));
            linkText.onHover(
                    HoverAction.showText(resources.suggestionLinkHover(response).asText()));
            message.append(linkText);

        }
        else message.append(response);

        // Regular chat response
        final Players pss = this.context.get(Players.class);
        pss.onlinePlayers().stream()
                .filter(op -> important || !this.muted(op))
                .forEach(op -> op.send(message));

        // Discord response
        final TextChannel discordChannel = config.channel(this.context);
        String discordMessage = response.replaceAll("§", "&");
        for (final String regex : new String[]{ "(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r" })
            discordMessage = discordMessage.replaceAll(regex, "");

        this.context.get(DiscordUtils.class).sendToTextChannel(resources.discordFormat(discordMessage), discordChannel);
    }

    public String parseWebsiteLink(final String unparsedLink) {
        String parsedLink = unparsedLink.replaceAll("\\[", "").replaceAll("]", "");
        if (!parsedLink.startsWith("http://") && !parsedLink.startsWith("https://"))
            parsedLink = "http://" + parsedLink;
        return parsedLink;
    }

    public String parsePlaceHolders(
            final String message, final String unparsedResponse, final String playerName) {
        String parsedResponse = unparsedResponse.replaceAll("<player>", playerName);
        final DiscordUtils du = this.context.get(DiscordUtils.class);

        if (parsedResponse.contains("<mention>")) {
            for (final String partial : message.split(" "))
                parsedResponse = this.parseMention(partial, parsedResponse, du);

            parsedResponse = parsedResponse.replaceAll("<mention>", playerName);
        }

        if (parsedResponse.contains("<random>"))
            parsedResponse = this.parseRandomPlayer(parsedResponse, playerName);

        return parsedResponse;
    }

    public boolean muted(final Player player) {
        return Dave.MUTED_DAVE.get(player);
    }

    private String parseMention(final String partial, String fullResponse, final DiscordUtils du) {
        if (partial.startsWith("<@") && 2 < partial.length()) {
            final String mention = du.jda().map(jda ->
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

    private String parseRandomPlayer(final String fullResponse, final String playerName) {
        final Players pss = this.context.get(Players.class);
        final int index = new Random().nextInt(pss.onlinePlayers().size());
        final String randomPlayer = pss.onlinePlayers().toArray(new Player[0])[index].name();

        return fullResponse.replaceAll("<random>", Objects.requireNonNullElseGet(randomPlayer, () -> playerName + "*"));
    }
}
