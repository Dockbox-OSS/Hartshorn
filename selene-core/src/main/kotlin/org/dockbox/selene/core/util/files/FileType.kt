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

package org.dockbox.selene.core.util.files

import java.nio.file.Path

enum class FileType(val extension: String) {

    // Minecraft native formats
    ANVIL("mca"),

    // Compiled Java formats
    CLASS("class"),
    JAR("jar"),

    // Image formats
    PNG("png"),
    JPG("jpg"),
    BITMAP("bmp"),

    // Datbase formats
    FAWE_HISTORY("bd"),
    OLDPLOTS("db"),
    SQLITE("sqlite"),

    // Config storage formats
    YAML("yml"),
    JSON("json"),
    MOD_CONFIG("cfg"),
    CONFIG("conf"),
    XML("xml"),
    PROPERTIES("properties"),

    // Others
    ZIP("zip"),
    LOG("log")
    ;

    fun asFileName(file: String): String {
        return "$file.$extension"
    }

    fun asPath(parent: Path, file: String): Path {
        return parent.resolve(asFileName(file))
    }
}
