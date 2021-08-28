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

package org.dockbox.hartshorn.discord;

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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Triad;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.discord.annotations.DiscordCommand;
import org.dockbox.hartshorn.discord.annotations.DiscordCommand.ListeningLevel;
import org.dockbox.hartshorn.discord.templates.MessageTemplate;
import org.dockbox.hartshorn.discord.templates.Template;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

@Service
@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class DefaultDiscordUtils implements DiscordUtils {

    @SuppressWarnings("ConstantDeclaredInAbstractClass")
    public static final String WILDCARD = "*";
    private final Map<String, Triad<DiscordCommand, MethodContext<?, ?>, TypeContext<?>>> commandMethods = HartshornUtils.emptyConcurrentMap();

    @Inject
    private ApplicationContext context;
    @Value("hartshorn.discord.logging-channel")
    private String loggingCategoryId;

    @Override
    public boolean checkMessageExists(final String messageId, final String channelId) {
        return this.jda().map(jda -> {
            final TextChannel channel = jda.getTextChannelById(channelId);
            if (null == channel) return false;
            final Message message = channel.retrieveMessageById(messageId).complete();
            return null != message;
        }).or(false);
    }

    @NotNull
    @Override
    public Exceptional<Category> loggingCategory() {
        if (this.jda().present())
            return Exceptional.of(this.jda().get().getCategoryById(this.loggingCategoryId));
        return Exceptional.empty();
    }

    @NotNull
    @Override
    public Exceptional<Guild> guild() {
        return this.globalChannel().map(GuildChannel::getGuild);
    }

    @Override
    public void sendToTextChannel(@NotNull final Text text, @NotNull final MessageChannel channel) {
        DefaultDiscordUtils.sendToTextChannel(text.toPlain(), channel);
    }

    @Override
    public void sendToTextChannel(@NotNull final ResourceEntry text, @NotNull final MessageChannel channel) {
        DefaultDiscordUtils.sendToTextChannel(text.plain(), channel);
    }

    @Override
    public void sendToTextChannel(final DiscordPagination pagination, final MessageChannel channel) {
        this.jda().present(jda -> {
            try {
                if (pagination.pages().isEmpty()) return;

                final Paginator paginator = PaginatorBuilder.createPaginator()
                        .setHandler(jda)
                        .shouldRemoveOnReact(false)
                        .build();
                Pages.activate(paginator);

                final List<Page> pages = pagination.pages().stream()
                        .map(page -> {
                            if (page instanceof Message) {
                                return new Page(PageType.TEXT, page);
                            }
                            else if (page instanceof MessageEmbed) {
                                return new Page(PageType.EMBED, page);
                            }
                            else throw new IllegalArgumentException("Pages of type '" + page.getClass().getName() + "' are not supported");
                        }).toList();

                channel.sendMessage((Message) pages.get(0).getContent()).queue(success -> Pages.paginate(success, pages));
            }
            catch (final InvalidHandlerException e) {
                Except.handle(e);
            }
        });
    }

    @Override
    public void sendToTextChannel(final Template<?> template, final MessageChannel channel) {
        if (template instanceof MessageTemplate)
            channel.sendMessage(((MessageTemplate) template).message()).queue();
    }

    @Override
    public void sendToUser(@NotNull final Text text, @NotNull final User user) {
        DefaultDiscordUtils.sendToUser(text.toPlain(), user);
    }

    @Override
    public void sendToUser(@NotNull final ResourceEntry text, @NotNull final User user) {
        DefaultDiscordUtils.sendToUser(text.plain(), user);
    }

    @Override
    public void sendToUser(final DiscordPagination pagination, final User user) {
        user.openPrivateChannel().queue(privateChannel -> this.sendToTextChannel(pagination, privateChannel));
    }

    @Override
    public void sendToUser(final Template<?> template, final User user) {
        user.openPrivateChannel().queue(channel -> {
            if (template instanceof MessageTemplate)
                channel.sendMessage(((MessageTemplate) template).message());
        });
    }

    @Override
    public void registerCommandListener(@NotNull final TypeContext<?> type) {
        type.flatMethods(DiscordCommand.class).stream()
                .filter(m -> {
                    final boolean correctParameterCount = 1 == m.parameterCount();
                    if (!correctParameterCount) return false;
                    return m.parameterTypes().get(0).childOf(DiscordCommandContext.class);
                })
                .forEach(method -> {
                    final DiscordCommand annotation = method.annotation(DiscordCommand.class).get();
                    final String command = annotation.command();
                    final Triad<DiscordCommand, MethodContext<?, ?>, TypeContext<?>> information = new Triad<>(annotation, method, type);

                    if (this.commandMethods.containsKey(command))
                        Hartshorn.log().warn("More than one listener registered for Discord command: " + command);
                    this.commandMethods.put(command, information);
                });
    }

    @Override
    @SuppressWarnings("CallToSuspiciousStringMethod")
    public void post(@NotNull final String command, @NotNull final DiscordCommandContext context) {
        final DiscordResources resources = this.context.get(DiscordResources.class);
        if (this.commandMethods.containsKey(command)) {
            final Triad<DiscordCommand, MethodContext<?, ?>, TypeContext<?>> information = this.commandMethods.get(command);
            final DiscordCommand annotation = information.first();

            final ListeningLevel level = annotation.listeningLevel();

            if (!validChannel(context, level)) return;

            // Ensure the command is either globally available, or it was sent in the correct channel
            final boolean correctChannel = WILDCARD.equals(annotation.channelId()) || context.channel().getId().equals(annotation.channelId());
            if (!correctChannel) return;

            // If all roles are allowed, we move on directly, otherwise we compare the ranks of the author
            // first
            boolean userPermitted = WILDCARD.equals(annotation.minimumRankId());
            if (!userPermitted) {

                // Ensure the role exists at all
                final Exceptional<Role> or = this.guild().map(guild -> guild.getRoleById(annotation.minimumRankId()));
                if (!or.present()) return;

                // Ensure the player has the required role
                userPermitted = this.guild()
                        .map(guild -> guild.getMember(context.author()))
                        .map(member -> member.getRoles().contains(or.get()))
                        .or(false);
            }

            if (!userPermitted) {
                context.sendToChannel(resources.commandNotPermitted());
                return;
            }

            final MethodContext<?, ?> method = information.second();
            final TypeContext<?> type = information.third();

            final Object o = this.context.get(type);
            //noinspection unchecked
            ((MethodContext<?, Object>) method).invoke(o, context);
        }
        else context.sendToChannel(resources.commandUnknown());
    }

    private static boolean validChannel(@NotNull final DiscordCommandContext context, final ListeningLevel level) {
        final boolean listensForBoth = ListeningLevel.BOTH == level;
        if (listensForBoth) return true;

        final boolean privateAndValid = ListeningLevel.PRIVATE_ONLY == level && ChannelType.PRIVATE == context.channel().getType();
        final boolean textAndValid = ListeningLevel.CHANNEL_ONLY == level && ChannelType.TEXT == context.channel().getType();

        return privateAndValid || textAndValid;
    }

    public static void sendToUser(@NotNull final CharSequence text, @NotNull final User user) {
        user.openPrivateChannel().queue(channel -> channel.sendMessage(text));
    }

    private static void sendToTextChannel(@NotNull final CharSequence text, @NotNull final MessageChannel channel) {
        channel.sendMessage(text).queue();
    }
}
