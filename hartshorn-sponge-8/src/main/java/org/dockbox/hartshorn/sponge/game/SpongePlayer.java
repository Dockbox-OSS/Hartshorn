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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.api.keys.PersistentDataKey;
import org.dockbox.hartshorn.api.keys.TransactionResult;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.hartshorn.server.minecraft.packets.Packet;
import org.dockbox.hartshorn.server.minecraft.players.GameSettings;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.server.minecraft.players.Sounds;
import org.dockbox.hartshorn.server.minecraft.players.inventory.PlayerInventory;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Map;
import java.util.UUID;

public class SpongePlayer extends Player {

    @Wired
    public SpongePlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
    }

    @Override
    public void execute(String command) {

    }

    @Override
    public void send(ResourceEntry text) {

    }

    @Override
    public void send(Text text) {

    }

    @Override
    public void sendWithPrefix(ResourceEntry text) {

    }

    @Override
    public void sendWithPrefix(Text text) {

    }

    @Override
    public void send(Pagination pagination) {

    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;
    }

    @Override
    public void setPermission(String permission, Tristate state) {

    }

    @Override
    public void setPermission(Permission permission, Tristate state) {

    }

    @Override
    public <T> Exceptional<T> get(PersistentDataKey<T> dataKey) {
        return Exceptional.empty();
    }

    @Override
    public <T> TransactionResult set(PersistentDataKey<T> dataKey, T value) {
        return TransactionResult.success();
    }

    @Override
    public <T> void remove(PersistentDataKey<T> dataKey) {

    }

    @Override
    public Map<PersistentDataKey<?>, Object> getPersistentData() {
        return HartshornUtils.emptyMap();
    }

    @Override
    public Location getLocation() {
        return Location.empty();
    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public Text getDisplayName() {
        return Text.of();
    }

    @Override
    public void setDisplayName(Text displayName) {

    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public void setHealth(double health) {

    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void setInvisible(boolean visible) {

    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {

    }

    @Override
    public boolean hasGravity() {
        return false;
    }

    @Override
    public void setGravity(boolean gravity) {

    }

    @Override
    public void send(Packet packet) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public void kick(Text reason) {

    }

    @Override
    public Gamemode getGamemode() {
        return Gamemode.OTHER;
    }

    @Override
    public void setGamemode(Gamemode gamemode) {

    }

    @Override
    public Item getItemInHand(Hand hand) {
        return MinecraftItems.getInstance().getAir();
    }

    @Override
    public void setItemInHand(Hand hand, Item item) {

    }

    @Override
    public void play(Sounds sound) {

    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public Profile getProfile() {
        return new SpongeProfile(this.getUniqueId());
    }

    @Override
    public Exceptional<Location> getLookingAtBlockPos() {
        return Exceptional.empty();
    }

    @Override
    public PlayerInventory getInventory() {
        return null;
    }

    @Override
    public GameSettings getGameSettings() {
        return null;
    }

    public Exceptional<User> user() {
        return Exceptional.of(Sponge.server().userManager().find(this.getUniqueId()));
    }

    public Exceptional<ServerPlayer> player() {
        return this.user().map(user -> user.player().orElse(null));
    }
}
