/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.util.discord;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.selene.core.annotations.DiscordCommand;
import org.dockbox.selene.core.annotations.DiscordCommand.ListeningLevel;
import org.dockbox.selene.core.events.discord.DiscordCommandContext;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.tuple.Triad;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.discord.DiscordUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DefaultDiscordUtils implements DiscordUtils {

    private static final Map<String, Triad<DiscordCommand, Method, Object>> commandMethods = new ConcurrentHashMap<>();
    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final String WILDCARD = "*";

    @Override
    public boolean checkMessageExists(String messageId, String channelId) {
        return this.getJDA().map(jda -> {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (null == channel) return false;
            Message message = channel.retrieveMessageById(messageId).complete();
            return null != message;
        }).orElse(false);
    }

    @NotNull
    @Override
    public Exceptional<Category> getLoggingCategory() {
        if (this.getJDA().isPresent())
            return Exceptional.ofNullable(this.getJDA().get().getCategoryById("638683800167251998"));
        return Exceptional.empty();
    }

    @NotNull
    @Override
    public Exceptional<Guild> getGuild() {
        return this.getGlobalTextChannel().map(GuildChannel::getGuild);
    }

    @Override
    public void sendToTextChannel(@NotNull Text text, @NotNull MessageChannel channel) {
        this.sendToTextChannel(text.toPlain(), channel);
    }

    @Override
    public void sendToTextChannel(@NotNull CharSequence text, @NotNull MessageChannel channel) {
        channel.sendMessage(text).queue();
    }

    @Override
    public void sendToTextChannel(@NotNull ResourceEntry text, @NotNull MessageChannel channel) {
        this.sendToTextChannel(text.plain(), channel);
    }

    @Override
    public void sendToUser(@NotNull Text text, @NotNull User user) {
        this.sendToUser(text.toPlain(), user);
    }

    @Override
    public void sendToUser(@NotNull CharSequence text, @NotNull User user) {
        user.openPrivateChannel().queue(channel -> channel.sendMessage(text));
    }

    @Override
    public void sendToUser(@NotNull ResourceEntry text, @NotNull User user) {
        this.sendToUser(text.plain(), user);
    }

    @Override
    public void registerCommandListener(@NotNull Object instance) {
        Object obj = instance;
        if (instance instanceof Class) {
            try {
                Constructor<?> ctor = ((Class<?>) instance).getConstructor();
                obj = ctor.newInstance();
            } catch (NoSuchMethodException e) {
                Selene.getServer().except("Could not find constructor for Discord listener [" + ((Class<?>) instance).getCanonicalName() + "]");
                return;
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                Selene.getServer().except("Could not instantiate Discord listener [" + ((Class<?>) instance).getCanonicalName() + "]");
                return;
            }
        }

        Arrays.stream(obj.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(DiscordCommand.class))
                .filter(m -> {
                    boolean correctParameterCount = 1 == m.getParameterCount();
                    if (!correctParameterCount) return false;
                    return m.getParameters()[0].getType().equals(DiscordCommandContext.class);
                })
                .forEach(method -> {
                    DiscordCommand annotation = method.getAnnotation(DiscordCommand.class);
                    String command = annotation.command();
                    Triad<DiscordCommand, Method, Object> information = new Triad<>(annotation, method, instance);

                    if (commandMethods.containsKey(command))
                        Selene.log().warn("More than one listener registered for Discord command: " + command);
                    commandMethods.put(command, information);
                });
    }

    @Override
    @SuppressWarnings("CallToSuspiciousStringMethod")
    public void post(@NotNull String command, @NotNull DiscordCommandContext context) {
        if (commandMethods.containsKey(command)) {
            Triad<DiscordCommand, Method, Object> information = commandMethods.get(command);
            DiscordCommand annotation = information.getFirst();

            ListeningLevel level = annotation.listeningLevel();

            if (!isValidChannel(context, level)) return;

            // Ensure the command is either globally available, or it was sent in the correct channel
            boolean correctChannel = WILDCARD.equals(annotation.channelId())
                    || context.getChannel().getId().equals(annotation.channelId());
            if (!correctChannel) return;

            // If all roles are allowed, we move on directly, otherwise we compare the ranks of the author first
            boolean userPermitted = WILDCARD.equals(annotation.minimumRankId());
            if (!userPermitted) {

                // Ensure the role exists at all
                Exceptional<Role> or = this.getGuild().map(guild -> guild.getRoleById(annotation.minimumRankId()));
                if (!or.isPresent()) return;

                // Ensure the player has the required role
                userPermitted = this.getGuild()
                        .map(guild -> guild.getMember(context.getAuthor()))
                        .map(member -> member.getRoles().contains(or.get()))
                        .orElse(false);
            }

            if (!userPermitted) {
                context.sendToChannel(IntegratedResource.DISCORD_COMMAND_NOT_PERMITTED);
                return;
            }

            Method method = information.getSecond();
            Object instance = information.getThird();

            try {
                method.invoke(instance, context);
            } catch (IllegalAccessException | InvocationTargetException e) {
                context.sendToChannel(IntegratedResource.DISCORD_COMMAND_ERRORED);
                Selene.getServer().except("Failed to invoke previously checked method [" + method.getName() + "] in [" + instance.getClass().getCanonicalName() + "]");
            }
        } else context.sendToChannel(IntegratedResource.DISCORD_COMMAND_UNKNOWN);
    }

    private static boolean isValidChannel(@NotNull DiscordCommandContext context, ListeningLevel level) {
        boolean listensForBoth = ListeningLevel.BOTH == level;
        if (listensForBoth) return true;

        boolean isPrivateAndValid = ListeningLevel.PRIVATE_ONLY == level && ChannelType.PRIVATE == context.getChannel().getType();
        boolean isTextAndValid = ListeningLevel.CHANNEL_ONLY == level && ChannelType.TEXT == context.getChannel().getType();

        return isPrivateAndValid || isTextAndValid;
    }

}
