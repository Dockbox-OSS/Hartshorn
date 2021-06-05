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

package org.dockbox.hartshorn.test.objects.living;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
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
import org.dockbox.hartshorn.test.objects.JUnitPersistentDataHolder;
import org.dockbox.hartshorn.test.objects.JUnitProfile;
import org.dockbox.hartshorn.test.objects.JUnitWorld;
import org.dockbox.hartshorn.test.objects.inventory.JUnitInventory;
import org.dockbox.hartshorn.test.util.JUnitPermissionRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class JUnitPlayer extends Player implements JUnitPersistentDataHolder {

    @Getter
    private final PlayerInventory inventory = new JUnitInventory();
    @Getter @Setter
    private boolean online = true;

    @Getter @Setter
    private Gamemode gamemode = Gamemode.CREATIVE;
    @Getter @Setter
    private boolean sneaking = false;
    @Setter
    private Location lookingAt = null;
    @Getter @Setter
    private Text displayName;
    @Getter @Setter
    private double health = 20;
    @Getter
    private Location location;
    @Getter @Setter
    private boolean invisible = false;
    @Getter @Setter
    private boolean invulnerable = false;
    @Setter
    private boolean gravity = true;

    public JUnitPlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
        this.setDisplayName(Text.of(name));
        Worlds worlds = Hartshorn.context().get(Worlds.class);
        this.setLocation(new Location(0, 0, 0, worlds.getWorld(worlds.getRootWorldId()).orNull()));
        ((JUnitWorld) this.getWorld()).addEntity(this);
    }

    @Override
    public boolean isAlive() {
        return this.getHealth() > 0;
    }

    @Override
    public boolean hasGravity() {
        return this.gravity;
    }

    @Override
    public void setLocation(Location location) {
        if (this.location != null) ((JUnitWorld) this.getWorld()).destroyEntity(this.getUniqueId());
        this.location = location;
        ((JUnitWorld) this.getWorld()).addEntity(this);
    }

    @Override
    public World getWorld() {
        return this.getLocation().getWorld();
    }

    @Override
    public void execute(String command) {
        // TODO: CommandBus implementation
    }

    @Override
    public void kick(Text reason) {
        this.online = false;
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
    public Profile getProfile() {
        return new JUnitProfile(this.getUniqueId());
    }

    @Override
    public Exceptional<Location> getLookingAtBlockPos() {
        return Exceptional.of(this.lookingAt);
    }

    @Override
    public GameSettings getGameSettings() {
        return new SimpleGameSettings(Language.EN_US);
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
    public void send(Pagination pagination) {
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