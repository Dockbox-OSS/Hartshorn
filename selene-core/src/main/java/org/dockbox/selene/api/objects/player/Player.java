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

package org.dockbox.selene.api.objects.player;

import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.entities.Entity;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.permissions.AbstractPermission;
import org.dockbox.selene.api.i18n.permissions.PermissionContext;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.inventory.PlayerInventory;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.keys.PersistentDataHolder;
import org.dockbox.selene.api.objects.location.dimensions.World;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.objects.special.Sounds;
import org.dockbox.selene.api.objects.targets.AbstractIdentifiable;
import org.dockbox.selene.api.objects.targets.InventoryHolder;
import org.dockbox.selene.api.objects.targets.Locatable;
import org.dockbox.selene.api.objects.targets.PacketReceiver;
import org.dockbox.selene.api.objects.targets.PermissionHolder;
import org.dockbox.selene.api.objects.tuple.Tristate;
import org.dockbox.selene.api.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class Player extends AbstractIdentifiable<Player> implements CommandSource, PermissionHolder, Locatable, InventoryHolder, PacketReceiver, PersistentDataHolder, Entity<Player> {

    // An empty context targets only global permissions
    private static final PermissionContext GLOBAL = PermissionContext.builder().build();

    protected Player(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
    }

    public abstract boolean isOnline();

    public abstract void kick(Text reason);

    public abstract Gamemode getGamemode();

    public abstract void setGamemode(Gamemode gamemode);

    public abstract Language getLanguage();

    public abstract void setLanguage(Language language);

    @Override
    public boolean hasAnyPermission(@NotNull String @NotNull ... permissions) {
        for (String permission : permissions) {
            if (this.hasPermission(permission)) return true;
        }
        return false;
    }

    @Override
    public boolean hasAllPermissions(@NotNull String @NotNull ... permissions) {
        for (String permission : permissions) {
            if (!this.hasPermission(permission)) return false;
        }
        return true;
    }

    @Override
    public boolean hasAnyPermission(@NotNull AbstractPermission @NotNull ... permissions) {
        for (AbstractPermission permission : permissions) {
            if (this.hasPermission(permission)) return true;
        }
        return false;
    }

    @Override
    public boolean hasAllPermissions(@NotNull AbstractPermission @NotNull ... permissions) {
        for (AbstractPermission permission : permissions) {
            if (!this.hasPermission(permission)) return false;
        }
        return true;
    }

    @Override
    public void setPermissions(Tristate state, @NotNull String @NotNull ... permissions) {
        for (String permission : permissions) {
            this.setPermission(permission, state);
        }
    }

    @Override
    public void setPermissions(Tristate state, @NotNull AbstractPermission @NotNull ... permissions) {
        for (AbstractPermission permission : permissions) {
            this.setPermission(permission, state);
        }
    }

    @Override
    public boolean isAlive() {
        return this.getHealth() > 0;
    }

    @Override
    public boolean summon(Location location) {
        throw new UnsupportedOperationException("Cannot re-summon players");
    }

    @Override
    public boolean destroy() {
        throw new UnsupportedOperationException("Cannot destroy players");
    }

    @Override
    public Player copy() {
        throw new UnsupportedOperationException("Cannot copy players");
    }

    @Override
    public World getWorld() {
        // No reference refresh required as this is done by getLocation. Should never throw NPE as
        // Location is either
        // valid or EMPTY (World instance follows this same guideline).
        return this.getLocation().getWorld();
    }

    @Override
    public PermissionContext activeContext() {
        if (!this.isOnline()) {
            return GLOBAL;
        } else {
            return PermissionContext.builder()
                    .forWorld(this.getWorld().getName())
                    .build();
        }
    }

    public abstract Item getItemInHand(Hand hand);

    public abstract void setItemInHand(Hand hand, Item item);

    public abstract void play(Sounds sound);

    @Override
    public int hashCode() {
        return this.getUniqueId().hashCode();
    }

    @SuppressWarnings("OverlyStrongTypeCast")
    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (obj instanceof Player) return this.getUniqueId().equals(((Player) obj).getUniqueId());
        return false;
    }

    public abstract boolean isSneaking();

    public abstract Profile getProfile();

    public abstract Exceptional<Location> getLookingAtBlockPos();

    @Override
    public abstract PlayerInventory getInventory();
}
