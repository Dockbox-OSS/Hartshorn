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

package org.dockbox.selene.api.i18n.permissions;

import org.dockbox.selene.api.server.Selene;

import java.util.Objects;

public class PermissionContext {

    private String user;
    private String dimension;
    private String remoteIp;
    private String localHost;
    private String localIp;
    private String world;

    private PermissionContext() {
    }

    private PermissionContext(String world) {
        this.world = world;
    }

    public static PermissionContextBuilder builder() {
        return new PermissionContextBuilder();
    }

    public AbstractPermission toPermission(String key) {
        return Selene.provide(PermissionFactory.class).of(key, this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUser(), this.getDimension(), this.getRemoteIp(), this.getLocalHost(), this.getLocalIp(), this.getWorld());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionContext)) return false;
        PermissionContext context = (PermissionContext) o;
        return Objects.equals(this.getUser(), context.getUser())
                && Objects.equals(this.getDimension(), context.getDimension())
                && Objects.equals(this.getRemoteIp(), context.getRemoteIp())
                && Objects.equals(this.getLocalHost(), context.getLocalHost())
                && Objects.equals(this.getLocalIp(), context.getLocalIp())
                && Objects.equals(this.getWorld(), context.getWorld()
        );
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDimension() {
        return this.dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getRemoteIp() {
        return this.remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getLocalHost() {
        return this.localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public String getLocalIp() {
        return this.localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getWorld() {
        return this.world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public static final class PermissionContextBuilder {

        private final PermissionContext permissionContext;

        private PermissionContextBuilder() {
            this.permissionContext = new PermissionContext();
        }

        public PermissionContextBuilder but() {
            return PermissionContext.builder()
                    .forUser(this.permissionContext.getUser())
                    .forDimension(this.permissionContext.getDimension())
                    .forRemoteIp(this.permissionContext.getRemoteIp())
                    .forLocalHost(this.permissionContext.getLocalHost())
                    .forLocalIp(this.permissionContext.getLocalIp())
                    .forWorld(this.permissionContext.getWorld());
        }

        public PermissionContextBuilder forWorld(String world) {
            this.permissionContext.setWorld(world);
            return this;
        }

        public PermissionContextBuilder forLocalIp(String localIp) {
            this.permissionContext.setLocalIp(localIp);
            return this;
        }

        public PermissionContextBuilder forLocalHost(String localHost) {
            this.permissionContext.setLocalHost(localHost);
            return this;
        }

        public PermissionContextBuilder forRemoteIp(String remoteIp) {
            this.permissionContext.setRemoteIp(remoteIp);
            return this;
        }

        public PermissionContextBuilder forDimension(String dimension) {
            this.permissionContext.setDimension(dimension);
            return this;
        }

        public PermissionContextBuilder forUser(String user) {
            this.permissionContext.setUser(user);
            return this;
        }

        public PermissionContext build() {
            return this.permissionContext;
        }
    }
}
