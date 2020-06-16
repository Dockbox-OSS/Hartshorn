package org.dockbox.darwin.sponge.objects.location;

import com.boydti.fawe.object.FawePlayer;

import org.dockbox.darwin.core.objects.location.Location;
import org.dockbox.darwin.core.objects.user.Gamemode;
import org.dockbox.darwin.core.objects.user.Player;
import org.dockbox.darwin.core.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.Optional;
import java.util.UUID;

public class SpongePlayer extends Player {

    private org.spongepowered.api.entity.living.player.Player referencePlayer;

    public SpongePlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
        referencePlayer = Sponge.getServer().getPlayer(uniqueId).orElse(null);
    }

    private void refreshReference() {
        if (referencePlayer == null) referencePlayer = Sponge.getServer().getPlayer(getUniqueId()).orElse(null);
    }

    @Override
    public boolean isOnline() {
        refreshReference();
        return referencePlayer != null && referencePlayer.isOnline();
    }

    @NotNull
    @Override
    public Optional<FawePlayer<?>> getFawePlayer() {
        return Optional.empty();
    }

    @Override
    public void kick(@NotNull Text message) {
        refreshReference();
        if (referencePlayer != null) referencePlayer.kick();
    }

    @NotNull
    @Override
    public Gamemode getGamemode() {
        refreshReference();
        GameMode mode = referencePlayer.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET);
        try {
            return Enum.valueOf(Gamemode.class, mode.toString());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Gamemode.OTHER;
        }
    }

    @Override
    public void setGamemode(@NotNull Gamemode gamemode) {

    }

    @Override
    public void execute(@NotNull String command) {

    }

    @Override
    public void send(@NotNull Text text) {

    }

    @Override
    public void send(@NotNull CharSequence text) {

    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {

    }

    @Override
    public void sendWithPrefix(@NotNull CharSequence text) {

    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return false;
    }

    @Override
    public boolean hasAnyPermission(@NotNull String... permissions) {
        return false;
    }

    @Override
    public boolean hasAllPermissions(@NotNull String... permissions) {
        return false;
    }

    @Override
    public void setPermission(@NotNull String permission) {

    }

    @Override
    public void setPermissions(@NotNull String... permissions) {

    }

    @NotNull
    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void setLocation(@NotNull Location location) {

    }
}
