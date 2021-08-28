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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.api.exceptions.NotImplementedException;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.permissions.Permission;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.entities.Entity;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.packets.Packet;
import org.dockbox.hartshorn.server.minecraft.players.GameSettings;
import org.dockbox.hartshorn.server.minecraft.players.GameSettingsImpl;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.server.minecraft.players.Sounds;
import org.dockbox.hartshorn.server.minecraft.players.inventory.PlayerInventory;
import org.dockbox.hartshorn.test.objects.JUnitPersistentDataHolder;
import org.dockbox.hartshorn.test.objects.JUnitProfile;
import org.dockbox.hartshorn.test.objects.JUnitWorld;
import org.dockbox.hartshorn.test.objects.inventory.JUnitInventory;
import org.dockbox.hartshorn.test.util.JUnitPermissionRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

// TODO: Modification events (teleport, kick, etc)
public class JUnitPlayer extends Player implements JUnitPersistentDataHolder {

    @Getter private final PlayerInventory inventory;
    @Getter private final ApplicationContext applicationContext;
    @Getter private Location location;
    @Setter private boolean gravity = true;
    @Setter private Block lookingAt = null;

    @Getter @Setter private double health = 20;
    @Getter @Setter private boolean online = true;
    @Getter @Setter private boolean sneaking = false;
    @Getter @Setter private boolean invisible = false;
    @Getter @Setter private boolean invulnerable = false;
    @Getter @Setter private Text displayName;
    @Getter @Setter private Vector3N rotation;
    @Getter @Setter private Gamemode gamemode = Gamemode.CREATIVE;

    @Inject private JUnitPermissionRegistry permissionRegistry;

    public JUnitPlayer(final ApplicationContext context, @NotNull final UUID uniqueId, @NotNull final String name) {
        super(uniqueId, name);
        this.applicationContext = context;
        final Worlds worlds = this.applicationContext().get(Worlds.class);
        this.location(Location.of(this.applicationContext(), 0, 0, 0, worlds.world(worlds.rootUniqueId()).orNull()));
        ((JUnitWorld) this.world()).addEntity(this);
        this.inventory = new JUnitInventory(context);
    }

    @Override
    public boolean gravity() {
        return this.gravity;
    }

    @Override
    public boolean location(final Location location) {
        if (this.location != null) ((JUnitWorld) this.world()).destroyEntity(this.uniqueId());
        this.location = location;
        ((JUnitWorld) this.world()).addEntity(this);
        return true;
    }

    @Override
    public void execute(final String command) {
        // TODO: CommandBus implementation
        throw new NotImplementedException();
    }

    @Override
    public void kick(final Text reason) {
        this.online = false;
    }

    @Override
    public boolean alive() {
        return this.health() > 0;
    }

    @Override
    public World world() {
        return this.location().world();
    }

    @Override
    public Item itemInHand(final Hand hand) {
        return this.inventory().slot(hand.slot());
    }

    @Override
    public void itemInHand(final Hand hand, final Item item) {
        this.inventory().slot(item, hand.slot());
    }

    @Override
    public void play(final Sounds sound) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public Profile profile() {
        return new JUnitProfile(this.uniqueId());
    }

    @Override
    public Exceptional<Block> lookingAtBlock() {
        return Exceptional.of(this.lookingAt);
    }

    @Override
    public Exceptional<Entity> lookingAtEntity() {
        return Exceptional.empty();
    }

    @Override
    public GameSettings gameSettings() {
        return new GameSettingsImpl(Language.EN_US);
    }

    @Override
    public void send(final ResourceEntry text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void send(final Text text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void sendWithPrefix(final ResourceEntry text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void sendWithPrefix(final Text text) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void send(final Pagination pagination) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public void send(final Packet packet) {
        // TODO: Test implementation, mocking client?
        throw new NotImplementedException();
    }

    @Override
    public boolean hasPermission(final String permission) {
        return this.permissionRegistry.hasPermission(this, permission);
    }

    @Override
    public boolean hasPermission(final Permission permission) {
        return this.permissionRegistry.hasPermission(this, permission);
    }

    @Override
    public void permission(final String permission, final Tristate state) {
        this.permissionRegistry.permission(this, permission, state);
    }

    @Override
    public void permission(final Permission permission, final Tristate state) {
        this.permissionRegistry.permission(this, permission, state);
    }
}
