package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("spectatortp")
public class SpectatorTeleportTranslations {

    public static final Translation SPECTATOR_TP_DISALLOWED = Translation.create("error_not_allowed", "$3You are not allowed to teleport while in spectator mode");

    private SpectatorTeleportTranslations() {
    }
}
