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

package org.dockbox.selene.dave.models;

import org.dockbox.selene.api.entity.annotations.Metadata;

import java.util.List;

@Metadata(alias = "trigger")
public class DaveTrigger {

    private String id;
    private List<String> triggers;
    private boolean important;
    private List<DaveResponse> responses;
    private String permission;

    public DaveTrigger() {}

    public DaveTrigger(
            String id,
            List<String> triggers,
            boolean important,
            List<DaveResponse> responses,
            String permission
    ) {
        this.id = id;
        this.triggers = triggers;
        this.important = important;
        this.responses = responses;
        this.permission = permission;
    }

    public String getId() {
        return this.id;
    }

    public List<String> getRawTriggers() {
        return this.triggers;
    }

    public boolean isImportant() {
        return this.important;
    }

    public List<DaveResponse> getResponses() {
        return this.responses;
    }

    public String getPermission() {
        return this.permission;
    }
}
