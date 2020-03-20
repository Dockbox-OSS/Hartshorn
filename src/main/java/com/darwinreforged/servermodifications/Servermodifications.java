package com.darwinreforged.servermodifications;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "servermodifications",
        name = "Servermodifications",
        description = "Custom plugins and modifications combined into a single source",
        url = "https://darwinreforged.com",
        authors = {
                "GuusLieben",
                "TheCrunchy",
                "simbolduc",
                "pumbas600"
        }
)
public class Servermodifications {

    @Inject
    private Logger logger;

    public Servermodifications() {

    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }
}
