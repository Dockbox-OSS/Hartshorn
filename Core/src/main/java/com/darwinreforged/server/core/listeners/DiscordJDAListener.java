package com.darwinreforged.server.core.listeners;

import com.darwinreforged.server.core.events.internal.chat.DiscordChatEvent;
import com.darwinreforged.server.core.DarwinServer;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 The type Discord listener.
 */
public class DiscordJDAListener extends ListenerAdapter {

    private final List<String> channelWhitelist = new ArrayList<>();

    /**
     Instantiates a new Discord listener.

     @param channelWhitelist
     the channel whitelist
     */
    public DiscordJDAListener(List<String> channelWhitelist) {
        this.channelWhitelist.addAll(channelWhitelist);
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (channelWhitelist.contains(event.getChannel().getId()) && !event.getAuthor().isBot()) {
            DarwinServer.getEventBus().post(new DiscordChatEvent(
                    event.getMember(),
                    event.getMessage(),
                    event.getGuild(),
                    event.getTextChannel())
            );
        }
    }
}
