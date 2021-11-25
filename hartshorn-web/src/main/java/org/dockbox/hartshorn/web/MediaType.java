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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.persistence.FileFormat;
import org.dockbox.hartshorn.persistence.FileFormats;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum MediaType {
    ALL("*", "*"),
    APPLICATION_JSON("application", "json", FileFormats.JSON),
    APPLICATION_XML("application", "xml", FileFormats.XML),
    APPLICATION_YAML("application", "x-yaml", FileFormats.YAML),
    APPLICATION_TOML("application", "toml", FileFormats.TOML),
    TEXT_PLAIN("text", "plain"),
    TEXT_HTML("text", "html"),
    TEXT_CSS("text", "css"),
    TEXT_XML("text", "xml"),
    TEXT_YAML("text", "yaml"),
    TEXT_TOML("text", "toml"),
    TEXT_MARKDOWN("text", "markdown"),
    ;

    private final String type;
    private final String subtype;

    @Getter(AccessLevel.NONE)
    private FileFormat format;

    public boolean isSerializable() {
        return this.format != null;
    }

    public Exceptional<FileFormat> fileFormat() {
        return Exceptional.of(this.format);
    }

    public String value() {
        return "%s/%s".formatted(this.type(), this.subtype());
    }
}
