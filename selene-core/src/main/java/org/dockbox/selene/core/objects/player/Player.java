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

package org.dockbox.selene.core.objects.player;

import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.permissions.AbstractPermission;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.inventory.PlayerInventory;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.keys.PersistentDataHolder;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.profile.Profile;
import org.dockbox.selene.core.objects.special.Sounds;
import org.dockbox.selene.core.objects.targets.Identifiable;
import org.dockbox.selene.core.objects.targets.InventoryHolder;
import org.dockbox.selene.core.objects.targets.Locatable;
import org.dockbox.selene.core.objects.targets.PacketReceiver;
import org.dockbox.selene.core.objects.targets.PermissionHolder;
import org.dockbox.selene.core.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class Player
        extends Identifiable<Player>
        implements CommandSource, PermissionHolder, Locatable, InventoryHolder, PacketReceiver, PersistentDataHolder {

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
    public boolean hasPermission(@NotNull AbstractPermission permission) {
        return this.hasPermission(permission.get());
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
    public void setPermissions(boolean value, @NotNull String @NotNull ... permissions) {
        for (String permission : permissions) {
            this.setPermission(permission, value);
        }
    }

    @Override
    public void setPermission(@NotNull AbstractPermission permission, boolean value) {
        this.setPermission(permission.get(), value);
    }

    @Override
    public void setPermissions(boolean value, @NotNull AbstractPermission @NotNull ... permissions) {
        for (AbstractPermission permission : permissions) {
            this.setPermission(permission, value);
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
