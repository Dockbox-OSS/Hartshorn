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

package org.dockbox.selene.common.discord;

import com.github.ygimenez.exception.InvalidHandlerException;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.Page;
import com.github.ygimenez.model.Paginator;
import com.github.ygimenez.model.PaginatorBuilder;
import com.github.ygimenez.type.PageType;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.selene.api.annotations.command.DiscordCommand;
import org.dockbox.selene.api.annotations.command.DiscordCommand.ListeningLevel;
import org.dockbox.selene.api.discord.DiscordPagination;
import org.dockbox.selene.api.discord.DiscordUtils;
import org.dockbox.selene.api.discord.templates.MessageTemplate;
import org.dockbox.selene.api.discord.templates.Template;
import org.dockbox.selene.api.events.discord.DiscordCommandContext;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.DefaultResource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.tuple.Triad;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class DefaultDiscordUtils implements DiscordUtils {

    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final String WILDCARD = "*";

    private static final Map<String, Triad<DiscordCommand, Method, Object>> commandMethods = SeleneUtils.emptyConcurrentMap();

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
            return Exceptional.ofNullable(this.getJDA().get().getCategoryById(Selene.getServer().getGlobalConfig().getDiscordLoggingCategoryId()));
        return Exceptional.empty();
    }

    @NotNull
    @Override
    public Exceptional<Guild> getGuild() {
        return this.getGlobalTextChannel().map(GuildChannel::getGuild);
    }

    @Override
    public void sendToTextChannel(@NotNull Text text, @NotNull MessageChannel channel) {
        DefaultDiscordUtils.sendToTextChannel(text.toPlain(), channel);
    }

    @Override
    public void sendToTextChannel(@NotNull ResourceEntry text, @NotNull MessageChannel channel) {
        DefaultDiscordUtils.sendToTextChannel(text.plain(), channel);
    }

    @Override
    public void sendToTextChannel(DiscordPagination pagination, MessageChannel channel) {
        this.getJDA().ifPresent(jda -> {
            try {
                if (pagination.getPages().isEmpty()) return;

                Paginator paginator = PaginatorBuilder.createPaginator()
                        .setHandler(jda)
                        .shouldRemoveOnReact(false)
                        .build();
                Pages.activate(paginator);

                List<Page> pages = pagination.getPages().stream()
                        .map(page -> {
                            if (page instanceof Message) {
                                return new Page(PageType.TEXT, page);
                            }
                            else if (page instanceof MessageEmbed) {
                                return new Page(PageType.EMBED, page);
                            }
                            else throw new IllegalArgumentException("Pages of type '" + page.getClass().getName() + "' are not supported");
                        }).collect(Collectors.toList());

                channel.sendMessage((Message) pages.get(0).getContent()).queue(success -> Pages.paginate(success, pages));
            }
            catch (InvalidHandlerException e) {
                Selene.handle(e);
            }
        });
    }

    @Override
    public void sendToTextChannel(Template<?> template, MessageChannel channel) {
        if (template instanceof MessageTemplate)
            channel.sendMessage(((MessageTemplate) template).getJDAMessage()).queue();
    }

    @Override
    public void sendToUser(@NotNull Text text, @NotNull User user) {
        DefaultDiscordUtils.sendToUser(text.toPlain(), user);
    }

    @Override
    public void sendToUser(@NotNull ResourceEntry text, @NotNull User user) {
        DefaultDiscordUtils.sendToUser(text.plain(), user);
    }

    @Override
    public void sendToUser(DiscordPagination pagination, User user) {
        user.openPrivateChannel().queue(privateChannel -> sendToTextChannel(pagination, privateChannel));
    }

    @Override
    public void sendToUser(Template<?> template, User user) {
        user.openPrivateChannel().queue(channel -> {
            if (template instanceof MessageTemplate)
                channel.sendMessage(((MessageTemplate) template).getJDAMessage());
        });
    }

    @Override
    public void registerCommandListener(@NotNull Object instance) {
        Object obj = instance;
        if (instance instanceof Class) obj = Reflect.getInstance((Class<?>) instance);

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
            boolean correctChannel = WILDCARD.equals(annotation.channelId()) || context.getChannel().getId().equals(annotation.channelId());
            if (!correctChannel) return;

            // If all roles are allowed, we move on directly, otherwise we compare the ranks of the author
            // first
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
                context.sendToChannel(DefaultResource.DISCORD_COMMAND_NOT_PERMITTED);
                return;
            }

            Method method = information.getSecond();
            Object instance = information.getThird();

            try {
                method.invoke(instance, context);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                context.sendToChannel(DefaultResource.DISCORD_COMMAND_ERRORED);
                Selene.handle("Failed to invoke previously checked method [" + method.getName() + "] in [" + instance.getClass()
                        .getCanonicalName() + "]");
            }
        }
        else context.sendToChannel(DefaultResource.DISCORD_COMMAND_UNKNOWN);
    }

    private static boolean isValidChannel(@NotNull DiscordCommandContext context, ListeningLevel level) {
        boolean listensForBoth = ListeningLevel.BOTH == level;
        if (listensForBoth) return true;

        boolean isPrivateAndValid = ListeningLevel.PRIVATE_ONLY == level && ChannelType.PRIVATE == context.getChannel().getType();
        boolean isTextAndValid = ListeningLevel.CHANNEL_ONLY == level && ChannelType.TEXT == context.getChannel().getType();

        return isPrivateAndValid || isTextAndValid;
    }

    public static void sendToUser(@NotNull CharSequence text, @NotNull User user) {
        user.openPrivateChannel().queue(channel -> channel.sendMessage(text));
    }

    private static void sendToTextChannel(@NotNull CharSequence text, @NotNull MessageChannel channel) {
        channel.sendMessage(text).queue();
    }
}
