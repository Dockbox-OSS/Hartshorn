package com.darwinreforged.server.modules.extensions.plotsquared.whatworld;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.commands.annotations.Permission;
import com.darwinreforged.server.core.commands.context.CommandArgument;
import com.darwinreforged.server.core.commands.context.CommandContext;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.player.DarwinPlayer;
import com.darwinreforged.server.core.player.PlayerManager;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.types.location.DarwinWorld;

import java.util.Optional;

@Module(id = "whatworld", name = "WhatWorld", description = "Shows what world a player is in", authors = {"GuusLieben", "TheCrunchy"})
public class WhatWorldModule {

    @Command(aliases = "ww", usage = "ww [player]", desc = "Shows what world a player is in", context = "ww [player{Player}]")
    @Permission(Permissions.WW_USE)
    public void whatWorld(CommandSender src, CommandContext ctx) {
        Optional<CommandArgument<DarwinPlayer>> playerCandidate = ctx.getArgument("player", DarwinPlayer.class);
        if (playerCandidate.isPresent()) {
            DarwinPlayer p = playerCandidate.get().getValue();
            worldForPlayer(p, src);
        } else DarwinServer.get(PlayerManager.class).getOnlinePlayers().forEach(p -> worldForPlayer(p, src));
    }

    private void worldForPlayer(DarwinPlayer p, CommandSender src) {
        String wn = p.getWorld().map(DarwinWorld::getName).orElse("Unknown");
        src.sendMessage(Translations.WHATWORLD_PLAYER_IN.f(p.getName(), wn), false);
    }

}
