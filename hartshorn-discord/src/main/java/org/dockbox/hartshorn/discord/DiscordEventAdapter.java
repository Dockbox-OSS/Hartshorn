package org.dockbox.hartshorn.discord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.discord.events.DiscordBotDisconnectedEvent;
import org.dockbox.hartshorn.discord.events.DiscordBotReconnectedEvent;
import org.dockbox.hartshorn.discord.events.DiscordChatDeletedEvent;
import org.dockbox.hartshorn.discord.events.DiscordChatReceivedEvent;
import org.dockbox.hartshorn.discord.events.DiscordChatUpdatedEvent;
import org.dockbox.hartshorn.discord.events.DiscordPrivateChatDeletedEvent;
import org.dockbox.hartshorn.discord.events.DiscordPrivateChatReceivedEvent;
import org.dockbox.hartshorn.discord.events.DiscordPrivateChatUpdatedEvent;
import org.dockbox.hartshorn.discord.events.DiscordReactionAddedEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserBannedEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserJoinedEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserLeftEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserNicknameChangedEvent;
import org.dockbox.hartshorn.discord.events.DiscordUserUnbannedEvent;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Posting(value = {
        // Bot specific events
        DiscordBotReconnectedEvent.class,
        DiscordBotDisconnectedEvent.class,
        // Chat-related events
        DiscordChatReceivedEvent.class,
        DiscordChatUpdatedEvent.class,
        DiscordChatDeletedEvent.class,
        DiscordPrivateChatReceivedEvent.class,
        DiscordPrivateChatUpdatedEvent.class,
        DiscordPrivateChatDeletedEvent.class,
        DiscordReactionAddedEvent.class,
        // User related events
        DiscordUserBannedEvent.class,
        DiscordUserUnbannedEvent.class,
        DiscordUserLeftEvent.class,
        DiscordUserJoinedEvent.class,
        DiscordUserNicknameChangedEvent.class
})
public class DiscordEventAdapter extends ListenerAdapter {

    @Inject
    private ApplicationContext context;
    
    @Override
    public void onReconnected(@NotNull ReconnectedEvent event) {
        new DiscordBotReconnectedEvent().with(this.context).post();
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        new DiscordBotDisconnectedEvent().with(this.context).post();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        new DiscordChatReceivedEvent(
                event.getAuthor(),
                event.getMessage(),
                event.getGuild(),
                event.getChannel()
        ).with(this.context).post();
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        new DiscordChatUpdatedEvent(event.getAuthor(), event.getMessage()).with(this.context).post();
    }

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        new DiscordChatDeletedEvent(event.getMessageId()).with(this.context).post();
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        new DiscordPrivateChatReceivedEvent(event.getAuthor(), event.getMessage()).with(this.context).post();
    }

    @Override
    public void onPrivateMessageUpdate(@NotNull PrivateMessageUpdateEvent event) {
        new DiscordPrivateChatUpdatedEvent(event.getAuthor(), event.getMessage()).with(this.context).post();
    }

    @Override
    public void onPrivateMessageDelete(@NotNull PrivateMessageDeleteEvent event) {
        new DiscordPrivateChatDeletedEvent(event.getMessageId()).with(this.context).post();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        event.getTextChannel()
                .retrieveMessageById(event.getMessageId())
                .queue(message -> {
                    User user = event.getUser();
                    if (null != user) {
                        new DiscordReactionAddedEvent(user, message, event.getReaction()).with(this.context).post();
                    }
                });
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        new DiscordUserBannedEvent(event.getUser(), event.getGuild()).with(this.context).post();
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        new DiscordUserUnbannedEvent(event.getUser(), event.getGuild()).with(this.context).post();
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        new DiscordUserLeftEvent(event.getUser(), event.getGuild()).with(this.context).post();
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        new DiscordUserJoinedEvent(event.getUser(), event.getGuild()).with(this.context).post();
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        new DiscordUserNicknameChangedEvent(
                event.getUser(),
                Exceptional.of(event.getOldNickname()),
                Exceptional.of(event.getNewNickname())
        ).with(this.context).post();
    }
}
