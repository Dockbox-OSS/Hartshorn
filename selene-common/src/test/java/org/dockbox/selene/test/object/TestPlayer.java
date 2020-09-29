/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.test.object;

import com.boydti.fawe.object.FawePlayer;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.user.Gamemode;
import org.dockbox.selene.core.objects.user.Player;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.navigation.Pagination;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TestPlayer extends Player {
    private boolean online = false;
    private Gamemode gamemode = Gamemode.SURVIVAL;
    private Language language = Language.EN_US;
    private Location location = Location.Companion.getEMPTY();
    private final Map<String, Boolean> permissions = new ConcurrentHashMap<>();

    public TestPlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
    }

    @Override
    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @NotNull
    @Override
    public Exceptional<FawePlayer<?>> getFawePlayer() {
        return Exceptional.empty();
    }

    @Override
    public void kick(@NotNull Text message) {
        // TODO: Kick from onlinePlayerPool in TPSS
    }

    @NotNull
    @Override
    public Gamemode getGamemode() {
        return this.gamemode;
    }

    @Override
    public void setGamemode(@NotNull Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return this.language;
    }

    @Override
    public void setLanguage(@NotNull Language lang) {
        this.language = lang;
    }

    @Override
    public void execute(@NotNull String command) {
        // TODO: Fire CommandEvent
    }

    @NotNull
    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    @NotNull
    @Override
    public World getWorld() {
        return this.location.getWorld();
    }

    @Override
    public void send(@NotNull ResourceEntry text) {
        Selene.log().info("[->" + super.getName() + "]: " + text.asString());
    }

    @Override
    public void send(@NotNull Text text) {
        Selene.log().info("[->" + super.getName() + "]: " + text.toStringValue());
    }

    @Override
    public void send(@NotNull CharSequence text) {
        Selene.log().info("[->" + super.getName() + "]: " + text);
    }

    @Override
    public void sendWithPrefix(@NotNull ResourceEntry text) {
        Selene.log().info("[->" + super.getName() + "]: " + text.asString());
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        Selene.log().info("[->" + super.getName() + "]: " + text.toStringValue());
    }

    @Override
    public void sendWithPrefix(@NotNull CharSequence text) {
        Selene.log().info("[->" + super.getName() + "]: " + text);
    }

    @Override
    public void sendPagination(@NotNull Pagination pagination) {
        Selene.log().info("[->" + super.getName() + "]: \u00A7c{pagination}");
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.permissions.getOrDefault(permission, false);
    }

    @Override
    public boolean hasAnyPermission(@NotNull String @NotNull ... permissions) {
        for (String perm : permissions) if (this.hasPermission(perm)) return true;
        return false;
    }

    @Override
    public boolean hasAllPermissions(@NotNull String @NotNull ... permissions) {
        for (String perm : permissions) if (!this.hasPermission(perm)) return false;
        return true;
    }

    @Override
    public void setPermission(@NotNull String permission, boolean value) {
        this.permissions.put(permission, value);
    }

    @Override
    public void setPermissions(boolean value, @NotNull String... permissions) {
        for (String perm : permissions) this.setPermission(perm, value);
    }
}
