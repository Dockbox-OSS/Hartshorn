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

package org.dockbox.hartshorn.sponge.game;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.api.exceptions.NotImplementedException;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.entry.DefaultResources;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.packets.Packet;
import org.dockbox.hartshorn.server.minecraft.players.GameSettings;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.server.minecraft.players.SimpleGameSettings;
import org.dockbox.hartshorn.server.minecraft.players.Sounds;
import org.dockbox.hartshorn.server.minecraft.players.inventory.PlayerInventory;
import org.dockbox.hartshorn.sponge.inventory.SpongePlayerInventory;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.data.DataHolder.Mutable;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.world.Locatable;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class SpongePlayer extends Player implements SpongeComposite {

    @Wired
    public SpongePlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
    }

    @Override
    public void execute(String command) {
        this.player().present(player -> {
            try {
                Sponge.server().commandManager().process(player, command);
            }
            catch (CommandException e) {
                Except.handle(e);
            }
        });
    }

    @Override
    public void send(ResourceEntry text) {
        this.send(text.translate(this).asText());
    }

    @Override
    public void send(Text text) {
        this.player().present(player -> {
            player.sendMessage(SpongeConvert.toSponge(text));
        });
    }

    @Override
    public void sendWithPrefix(ResourceEntry text) {
        this.sendWithPrefix(text.translate(this).asText());
    }

    @Override
    public void sendWithPrefix(Text text) {
        this.player().present(player -> {
            final Text message = Text.of(DefaultResources.instance().getPrefix(), text);
            player.sendMessage(SpongeConvert.toSponge(message));
        });
    }

    @Override
    public void send(Pagination pagination) {
        // TODO: Implement once Pagination API is available
        throw new NotImplementedException();
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.hasPermission(permission, SubjectData.GLOBAL_CONTEXT);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        if (permission.getContext().absent()) {
            return this.hasPermission(permission.get());
        }
        else {
            Set<Context> contexts = SpongeConvert.toSponge(permission.getContext().get());
            return this.hasPermission(permission.get(), contexts);
        }
    }

    private boolean hasPermission(String permission, Set<Context> contexts) {
        return this.user().map(user -> user.hasPermission(permission, contexts)).or(false);
    }

    @Override
    public void setPermission(String permission, Tristate state) {
        this.setPermission(permission, SubjectData.GLOBAL_CONTEXT, state);
    }

    @Override
    public void setPermission(Permission permission, Tristate state) {
        if (permission.getContext().absent()) {
            this.setPermission(permission.get(), state);
        } else {
            Set<Context> contexts = SpongeConvert.toSponge(permission.getContext().get());
            this.setPermission(permission.get(), contexts, state);
        }
    }

    public void setPermission(String permission, Set<Context> context, Tristate state) {
        org.spongepowered.api.util.Tristate tristate = SpongeConvert.toSponge(state);
        this.user().present(user -> {
            user.subjectData().setPermission(context, permission, tristate);
        });
    }

    @Override
    public Location getLocation() {
        return this.player().map(Locatable::serverLocation).map(SpongeConvert::fromSponge).or(Location.empty());
    }

    @Override
    public void setLocation(Location location) {
        this.player().present(player -> {
            SpongeConvert.toSponge(location).present(player::setLocation);
        });
    }

    @Override
    public Text getDisplayName() {
        return this.player()
                .map(player -> player.get(Keys.DISPLAY_NAME).orElse(Component.empty()))
                .map(SpongeConvert::fromSponge)
                .or(Text.of());
    }

    @Override
    public void setDisplayName(Text displayName) {
        this.player().present(player -> player.offer(Keys.DISPLAY_NAME, SpongeConvert.toSponge(displayName)));
    }

    @Override
    public double getHealth() {
        return this.player().map(player -> player.get(Keys.HEALTH).orElse(0D)).or(0D);
    }

    @Override
    public void setHealth(double health) {
        this.player().present(player -> player.offer(Keys.HEALTH, health));
    }

    @Override
    public boolean isInvisible() {
        return this.getBoolean(Keys.IS_INVISIBLE);
    }

    @Override
    public void setInvisible(boolean visible) {
        this.setBoolean(Keys.IS_INVISIBLE, visible);
    }

    @Override
    public boolean isInvulnerable() {
        return this.getBoolean(Keys.INVULNERABLE);
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
        this.setBoolean(Keys.INVULNERABLE, invulnerable);
    }

    @Override
    public boolean hasGravity() {
        return this.getBoolean(Keys.IS_GRAVITY_AFFECTED);
    }

    @Override
    public void setGravity(boolean gravity) {
        this.setBoolean(Keys.IS_GRAVITY_AFFECTED, gravity);
    }

    private boolean getBoolean(Key<Value<Boolean>> key) {
        return this.player()
                .map(player -> player.get(key).orElse(false))
                .or(false);
    }

    private void setBoolean(Key<Value<Boolean>> key, boolean value) {
        this.player().present(player -> player.offer(key, value));
    }

    @Override
    public void send(Packet packet) {
        // TODO: Implement once packet API is done
        throw new NotImplementedException();
    }

    @Override
    public boolean isOnline() {
        return this.player().map(ServerPlayer::isOnline).or(false);
    }

    @Override
    public void kick(Text reason) {
        this.player().present(player -> player.kick(SpongeConvert.toSponge(reason)));
    }

    @Override
    public Gamemode getGamemode() {
        return this.player()
                .map(player -> player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET.get()))
                .map(SpongeConvert::fromSponge)
                .or(Gamemode.OTHER);
    }

    @Override
    public void setGamemode(Gamemode gamemode) {
        this.player().present(player -> {
            final GameMode mode = SpongeConvert.toSponge(gamemode);
            player.offer(Keys.GAME_MODE, mode);
        });
    }

    @Override
    public Item getItemInHand(Hand hand) {
        return this.getInventory().getSlot(hand.getSlot());
    }

    @Override
    public void setItemInHand(Hand hand, Item item) {
        this.getInventory().setSlot(item, hand.getSlot());
    }

    @Override
    public void play(Sounds sound) {
        this.player().present(player -> {
            SpongeConvert.toSponge(sound).present(soundType -> {
                final Sound playableSound = Sound.sound(soundType, Source.MASTER, 1, 1);
                player.playSound(playableSound);
            });
        });
    }

    @Override
    public boolean isSneaking() {
        return this.player()
                .map(player -> player.get(Keys.IS_SNEAKING).orElse(false))
                .or(false);
    }

    @Override
    public Profile getProfile() {
        return new SpongeProfile(this.getUniqueId());
    }

    @Override
    public Exceptional<Location> getLookingAtBlockPos() {
        // TODO: Implement once BlockRay is available on API8
        return Exceptional.empty();
    }

    @Override
    public PlayerInventory getInventory() {
        return new SpongePlayerInventory(this);
    }

    @Override
    public GameSettings getGameSettings() {
        return this.player().map(player -> {
            final Locale locale = player.locale();
            final Language language = Language.of(locale);
            return new SimpleGameSettings(language);
        }).orElse(() -> new SimpleGameSettings(Language.EN_US)).get();
    }

    private Exceptional<User> user() {
        return Exceptional.of(Sponge.server().userManager().find(this.getUniqueId()));
    }

    public Exceptional<ServerPlayer> player() {
        return this.user().map(user -> user.player().orElse(null));
    }

    @Override
    public Exceptional<? extends Mutable> getDataHolder() {
        // Use offline user reference to ensure we can (almost) always obtain the information
        return this.user();
    }
}
