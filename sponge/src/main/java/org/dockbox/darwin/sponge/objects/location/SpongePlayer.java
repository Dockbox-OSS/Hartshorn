package org.dockbox.darwin.sponge.objects.location;

import com.boydti.fawe.object.FawePlayer;

import org.dockbox.darwin.core.i18n.I18N;
import org.dockbox.darwin.core.objects.location.Location;
import org.dockbox.darwin.core.objects.location.World;
import org.dockbox.darwin.core.objects.user.Gamemode;
import org.dockbox.darwin.core.objects.user.Player;
import org.dockbox.darwin.core.text.Text;
import org.dockbox.darwin.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.UUID;

public class SpongePlayer extends Player {

    private final ThreadLocal<Optional<org.spongepowered.api.entity.living.player.Player>> referencePlayer = new ThreadLocal<>();

    public SpongePlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
        referencePlayer.set(Sponge.getServer().getPlayer(uniqueId));
    }

    private void refreshReference() {
        if (!referencePlayer.get().isPresent()) referencePlayer.set(Sponge.getServer().getPlayer(getUniqueId()));
    }

    private org.spongepowered.api.entity.living.player.Player getReference() {
        refreshReference();
        return referencePlayer.get().orElse(null);
    }

    private boolean referenceExists() {
        refreshReference();
        return referencePlayer.get().isPresent();
    }

    @Override
    public boolean isOnline() {
        return referenceExists() && getReference().isOnline();
    }

    @NotNull
    @Override
    public Optional<FawePlayer<?>> getFawePlayer() {
        return Optional.empty();
    }

    @Override
    public void kick(@NotNull Text message) {
        if (referenceExists()) getReference().kick();
    }

    @NotNull
    @Override
    public Gamemode getGamemode() {
        if (referenceExists()) {
            GameMode mode = getReference().get(Keys.GAME_MODE).orElse(GameModes.NOT_SET);
            return SpongeConversionUtil.fromSponge(mode);
        } else return Gamemode.OTHER;
    }

    @Override
    public void setGamemode(@NotNull Gamemode gamemode) {
        if (referenceExists())
            getReference().offer(Keys.GAME_MODE, SpongeConversionUtil.toSponge(gamemode));
    }

    @Override
    public void execute(@NotNull String command) {
        refreshReference();
        if (referenceExists()) Sponge.getCommandManager().process(getReference(), command);
    }

    @Override
    public void send(@NotNull Text text) {
        refreshReference();
        if (referenceExists()) getReference().sendMessage(SpongeConversionUtil.toSponge(text));
    }

    @Override
    public void send(@NotNull CharSequence text) {
        if (referenceExists()) getReference().sendMessage(org.spongepowered.api.text.Text.of(text));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        if (referenceExists()) getReference().sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(I18N.PREFIX.asText()),
                SpongeConversionUtil.toSponge(text)
                )
        );
    }

    @Override
    public void sendWithPrefix(@NotNull CharSequence text) {
        if (referenceExists()) getReference().sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(I18N.PREFIX.asText()),
                org.spongepowered.api.text.Text.of(text)
                )
        );
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        if (referenceExists()) return getReference().hasPermission(permission);
        else return Sponge.getServiceManager().provide(UserStorageService.class)
                .map(uss -> uss
                        .get(this.getUniqueId())
                        .map(u -> u.hasPermission(permission))
                        .orElse(false))
                .orElse(false);
    }

    @Override
    public boolean hasAnyPermission(@NotNull String @NotNull ... permissions) {
        for (String permission : permissions) if (hasPermission(permission)) return true;
        return false;
    }

    @Override
    public boolean hasAllPermissions(@NotNull String @NotNull ... permissions) {
        for (String permission : permissions) if (!hasPermission(permission)) return false;
        return true;
    }

    @Override
    public void setPermission(@NotNull String permission, boolean value) {
        if (referenceExists())
            getReference().getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, permission, Tristate.fromBoolean(value));
        else Sponge.getServiceManager().
                provide(UserStorageService.class)
                .flatMap(uss -> uss.get(this.getUniqueId()))
                .ifPresent(user -> user
                        .getSubjectData()
                        .setPermission(SubjectData.GLOBAL_CONTEXT, permission, Tristate.fromBoolean(value))
                );
    }

    @Override
    public void setPermissions(boolean value, @NotNull String @NotNull ... permissions) {
        for (String permission : permissions) setPermission(permission, value);
    }

    @NotNull
    @Override
    public Location getLocation() {
        if (referenceExists()) return SpongeConversionUtil.fromSponge(getReference().getLocation());
        else return Location.Companion.getEMPTY();
    }

    @Override
    public void setLocation(@NotNull Location location) {
        if (referenceExists()) getReference().setLocation(SpongeConversionUtil.toSponge(location));
    }

    @NotNull
    @Override
    public World getWorld() {
        // No reference refresh required as this is done by getLocation. Should never throw NPE as Location is either
        // valid or EMPTY (World instance follows this same guideline).
        return getLocation().getWorld();
    }
}
