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

import org.dockbox.selene.core.DiscordUtils;
import org.dockbox.selene.core.PlayerStorageService;
import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.objects.Console;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.keys.data.IntegerPersistentDataKey;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.targets.PermissionHolder;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.actions.ClickAction;
import org.dockbox.selene.core.text.actions.HoverAction;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.dave.models.DaveResponse;
import org.dockbox.selene.dave.models.DaveTrigger;
import org.dockbox.selene.dave.models.DaveTriggers;
import org.dockbox.selene.dave.models.ResponseType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class DaveUtils
{

    public static final IntegerPersistentDataKey mutedKey = IntegerPersistentDataKey.of("dave_muting", DaveModule.class);
    private static final Map<DaveTrigger, LocalDateTime> timeSinceTrigger = SeleneUtils.emptyConcurrentMap();

    private DaveUtils() {}

    public static void toggleMute(Player player)
    {
        player.get(mutedKey).ifPresent(state -> {
            if (1 == state)
            {
                player.remove(mutedKey);
                player.sendWithPrefix(DaveResources.DAVE_UNMUTED);

            }
            else
            {
                throw new IllegalStateException("Unexpected muted state value '" + state + "', was I modified externally?");
            }

        }).ifAbsent(() -> {
            player.set(mutedKey, 1);
            player.sendWithPrefix(DaveResources.DAVE_MUTED);
        });
    }

    public static Exceptional<DaveTrigger> findMatching(DaveTriggers triggers, String message)
    {
        for (DaveTrigger trigger : triggers.getTriggers())
        {
            for (String rawTrigger : trigger.getRawTriggers())
            {
                boolean containsAll = true;

                for (String keyword : rawTrigger.split(","))
                    if (!message.toLowerCase().replaceAll(",", "").contains(keyword.toLowerCase()))
                    {
                        containsAll = (false);
                        break;
                    }

                if (containsAll) return Exceptional.of(trigger);
            }
        }
        return Exceptional.empty();
    }

    public static void performTrigger(CommandSource source, String displayName, DaveTrigger trigger, String originalMessage, DaveConfig config)
    {
        LocalDateTime timeOfLastTriggered = timeSinceTrigger.get(trigger);
        long secondsSinceLastResponse = 10;
        if (null != timeOfLastTriggered)
            secondsSinceLastResponse = timeOfLastTriggered.until(LocalDateTime.now(), ChronoUnit.SECONDS);

        if (null != trigger && 10 <= secondsSinceLastResponse) handleTrigger(
                source,
                displayName,
                trigger,
                originalMessage,
                config
        );
    }

    public static void handleTrigger(CommandSource source, String displayName, DaveTrigger trigger, String originalMessage, DaveConfig config)
    {
        String perm = trigger.getPermission();

        if (null != perm)
        {
            if (source instanceof PermissionHolder)
            {
                if (!((PermissionHolder) source).hasPermission(perm)) return;
            }
            else return;
        }

        List<DaveResponse> responses = trigger.getResponses();

        boolean important = trigger.isImportant();

        responses.forEach(response -> {
            if (ResponseType.COMMAND == response.getType())
            {
                executeCommand(source, response.getMessage());
            }
            else if (ResponseType.URL == response.getType())
            {
                printResponse(DaveUtils.parseWebsiteLink(response.getMessage()), true, important, config);
            }
            else
            {
                printResponse(DaveUtils.parsePlaceHolders(
                        originalMessage,
                        response.getMessage(),
                        displayName),
                        false,
                        important,
                        config
                );
            }
        });

        timeSinceTrigger.put(trigger, LocalDateTime.now());
    }

    private static void executeCommand(CommandSource source, String command)
    {
        if (source instanceof Player && isMuted((Player) source)) return;

        if (command.startsWith("*")) Console.getInstance().execute(command);
        else source.execute(command);
    }

    private static void printResponse(String response, boolean link, boolean important, DaveConfig config)
    {
        Text message = Text.of(config.getPrefix());

        if (link)
        {
            Text linkText = Text.of(DaveResources.DAVE_LINK_SUGGESTION.format(response));
            linkText.onClick(ClickAction.openUrl(response));
            linkText.onHover(HoverAction.showText(DaveResources.DAVE_LINK_SUGGESTION_HOVER.format(response).asText()));
            message.append(linkText);

        }
        else message.append(response);

        // Regular chat response
        PlayerStorageService pss = Selene.provide(PlayerStorageService.class);
        pss.getOnlinePlayers().stream()
                .filter(op -> important || !isMuted(op))
                .forEach(op -> {
                    op.send(message);
                });

        // Discord response
        TextChannel discordChannel = config.getChannel();
        String discordMessage = response.replaceAll("§", "&");
        for (String regex : new String[]{ "(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r" })
            discordMessage = discordMessage.replaceAll(regex, "");

        Selene.provide(DiscordUtils.class).sendToTextChannel(DaveResources.DAVE_DISCORD_FORMAT.format(discordMessage).asString(), discordChannel);
    }

    public static String parseWebsiteLink(String unparsedLink)
    {
        String parsedLink = unparsedLink.replaceAll("\\[", "").replaceAll("]", "");
        if (!parsedLink.startsWith("http://") && !parsedLink.startsWith("https://"))
            parsedLink = "http://" + parsedLink;
        return parsedLink;
    }

    public static String parsePlaceHolders(String message, String unparsedResponse, String playername)
    {
        String parsedResponse = unparsedResponse.replaceAll("<player>", playername);
        DiscordUtils du = Selene.provide(DiscordUtils.class);


        if (parsedResponse.contains("<mention>"))
        {
            boolean replaced = false;
            for (String partial : message.split(" "))
            {
                if (partial.startsWith("<@") && partial.length() > 2)
                {
                    String mention = du.getJDA().map(jda -> jda.getUserById(
                            partial
                                    .replaceFirst("<@", "")
                                    .replaceFirst(">", "")
                            ).getName()
                    ).orElse("player");

                    if (null != mention)
                    {
                        parsedResponse = parsedResponse.replaceAll("<mention>", partial.replaceFirst("@", ""));
                        replaced = true;
                    }
                }

                if (partial.replaceAll("&.", "").startsWith("@") && partial.length() > 2)
                {
                    parsedResponse = parsedResponse.replaceAll("<mention>", partial.replaceFirst("@", ""));
                    replaced = true;
                }
            }

            if (!replaced)
            {
                parsedResponse = parsedResponse.replaceAll("<mention>", playername);
            }
        }

        if (parsedResponse.contains("<random>"))
        {
            PlayerStorageService pss = Selene.provide(PlayerStorageService.class);
            int index = new Random().nextInt(pss.getOnlinePlayers().size());
            String randomPlayer = pss.getOnlinePlayers().toArray(new Player[0])[index].getName();

            if (null != randomPlayer) parsedResponse = parsedResponse.replaceAll("<random>", randomPlayer);
            else parsedResponse = parsedResponse.replaceAll("<random>", playername + "*");
        }

        return parsedResponse;
    }

    public static boolean isMuted(Player player)
    {
        return player.get(mutedKey).map(state -> 1 == state).orElse(false);
    }

}
