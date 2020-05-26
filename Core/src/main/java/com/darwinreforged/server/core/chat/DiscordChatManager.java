package com.darwinreforged.server.core.chat;

import com.darwinreforged.server.core.internal.Utility;
import com.darwinreforged.server.core.internal.DarwinConfig;
import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.listeners.DiscordJDAListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

import javax.security.auth.login.LoginException;

/**
 The type Discord utils.
 */
@Utility("Discord connection and messaging utilities")
public interface DiscordChatManager {

    /**
     Init.

     @param channelWhitelist
     the channel whitelist
     */
    default void init(List<String> channelWhitelist) {
        if (getJda() != null) getJda().addEventListener(new DiscordJDAListener(channelWhitelist));
    }

    /**
     Send to channel.

     @param message
     the message
     @param channel
     the channel
     */
    default void sendToChannel(String message, String channel) {
        getChannel(channel).sendMessage(message).queue();
    }

    /**
     Send to channel.

     @param message
     the message
     @param channel
     the channel
     */
    default void sendToChannel(String message, long channel) {
        getChannel(channel).sendMessage(message).queue();
    }

    /**
     Send to channel.

     @param embed
     the embed
     @param channel
     the channel
     */
    default void sendToChannel(MessageEmbed embed, String channel) {
        getChannel(channel).sendMessage(embed).queue();
    }

    /**
     Send to channel.

     @param embed
     the embed
     @param channel
     the channel
     */
    default void sendToChannel(MessageEmbed embed, long channel) {
        getChannel(channel).sendMessage(embed).queue();
    }

    /**
     Gets channel.

     @param channel
     the channel

     @return the channel
     */
    default TextChannel getChannel(String channel) {
        return getJdaWithFallback().getTextChannelById(channel);
    }

    /**
     Gets channel.

     @param channel
     the channel

     @return the channel
     */
    default TextChannel getChannel(long channel) {
        return getJdaWithFallback().getTextChannelById(channel);
    }

    /**
     Gets user by id.

     @param id
     the id

     @return the user by id
     */
    default User getUserById(long id) {
        return getJdaWithFallback().getUserById(id);
    }

    /**
     Gets user by id.

     @param id
     the id

     @return the user by id
     */
    default User getUserById(String id) {
        return getJda().getUserById(id);
    }

    /**
     Gets jda with fallback.

     @return the jda with fallback
     */
    default JDA getJdaWithFallback() {
        JDA jda = getJda();
        if (jda != null) return jda;
        else {
            try {
                DarwinServer.getLog().warn("Failed to obtain internal JDA instance, creating temporary instance..");
                return (new JDABuilder(DarwinConfig.BOT_TOKEN.get())).build().awaitReady();
            } catch (InterruptedException | LoginException e) {
                DarwinServer.error("Failed to obtain JDA instance", e);
            }
        }
        // We tried everything we could..
        DarwinServer.getLog().warn("Failed to obtain JDA instance");
        return null;
    }

    /**
     Gets jda.

     @return the jda
     */
    JDA getJda();
}
