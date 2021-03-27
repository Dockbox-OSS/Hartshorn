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

package org.dockbox.selene.test.objects.living;

import org.dockbox.selene.api.Worlds;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.permissions.Permission;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.Packet;
import org.dockbox.selene.api.objects.inventory.PlayerInventory;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.player.Gamemode;
import org.dockbox.selene.api.objects.player.Hand;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.objects.special.Sounds;
import org.dockbox.selene.api.objects.tuple.Tristate;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.text.pagination.Pagination;
import org.dockbox.selene.test.objects.JUnitInventory;
import org.dockbox.selene.test.objects.JUnitPersistentDataHolder;
import org.dockbox.selene.test.objects.JUnitProfile;
import org.dockbox.selene.test.objects.JUnitWorld;
import org.dockbox.selene.test.util.JUnitPermissionRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class JUnitPlayer extends Player implements JUnitPersistentDataHolder {

    private final PlayerInventory inventory = new JUnitInventory();

    private boolean online = true;
    private Gamemode gamemode = Gamemode.CREATIVE;
    private Language language = Selene.getServer().getGlobalConfig().getDefaultLanguage();
    private boolean sneaking = false;
    private Location lookingAt = null;



    public JUnitPlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
        this.setDisplayName(Text.of(name));
        Worlds worlds = Selene.provide(Worlds.class);
        this.setLocation(new Location(0, 0, 0, worlds.getWorld(worlds.getRootWorldId()).orNull()));
        ((JUnitWorld) this.getWorld()).addEntity(this);
    }

    @Override
    public void execute(String command) {
        // TODO: CommandBus implementation
    }

    @Override
    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public void kick(Text reason) {
        this.online = false;
    }

    @Override
    public Gamemode getGamemode() {
        return this.gamemode;
    }

    @Override
    public void setGamemode(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public Language getLanguage() {
        return this.language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
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
        // TODO: Test implementation, mocking client?
    }

    @Override
    public boolean isSneaking() {
        return this.sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    @Override
    public Profile getProfile() {
        return new JUnitProfile(this.getUniqueId());
    }

    @Override
    public Exceptional<Location> getLookingAtBlockPos() {
        return Exceptional.ofNullable(this.lookingAt);
    }

    @Override
    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public void setLookingAt(Location lookingAt) {
        this.lookingAt = lookingAt;
    }


    @Override
    public void send(ResourceEntry text) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void send(Text text) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void sendWithPrefix(ResourceEntry text) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void sendWithPrefix(Text text) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void sendPagination(Pagination pagination) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public void send(Packet packet) {
        // TODO: Test implementation, mocking client?
    }

    @Override
    public boolean hasPermission(String permission) {
        return JUnitPermissionRegistry.hasPermission(this, permission);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return JUnitPermissionRegistry.hasPermission(this, permission);
    }

    @Override
    public void setPermission(String permission, Tristate state) {
        JUnitPermissionRegistry.setPermission(this, permission, state);
    }

    @Override
    public void setPermission(Permission permission, Tristate state) {
        JUnitPermissionRegistry.setPermission(this, permission, state);
    }
}
