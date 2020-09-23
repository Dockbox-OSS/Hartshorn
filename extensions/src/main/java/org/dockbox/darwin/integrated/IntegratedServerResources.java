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

package org.dockbox.darwin.integrated;

import org.dockbox.darwin.core.i18n.common.ResourceEntry;
import org.dockbox.darwin.core.i18n.entry.ExternalResourceEntry;
import org.dockbox.darwin.core.i18n.entry.IntegratedResource;
import org.dockbox.darwin.core.util.Utils;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
enum IntegratedServerResources {
    ;
    static final ResourceEntry PAGINATION_TITLE = new ExternalResourceEntry("$1Darwin Server Info", "darwinserver.pagination.title");
    static final ResourceEntry SERVER_HEADER = new ExternalResourceEntry("$2DarwinServer $3($1Version$3: $1{0}$3)", "darwinserver.info.header");
    static final ResourceEntry SERVER_UPDATE = new ExternalResourceEntry("$2Last updated$3: $1{0}", "darwinserver.info.updated");
    static final ResourceEntry SERVER_AUTHORS = new ExternalResourceEntry("$2Authors$3: $1{0}", "darwinserver.info.authors");
    static final ResourceEntry SERVER_EXTENSIONS = new ExternalResourceEntry("$2Extensions$3:", "darwinserver.info.extensions");
    static final ResourceEntry EXTENSION_ROW = new ExternalResourceEntry("$3 - $1{0} $3- $2{1}", "darwinserver.info.extension.row");
    static final ResourceEntry EXTENSION_ROW_HOVER = new ExternalResourceEntry("$2Details for '$1{0}$2'", "darwinserver.info.extension.hover");
    static final ResourceEntry EXTENSION_INFO_BLOCK = new ExternalResourceEntry(
            String.join("",
                    Utils.repeat(IntegratedResource.DEFAULT_PAGINATION_PADDING.asString(), 20), "\n",
                    "$2Name : $1{0}", "\n",
                    "$2ID : $1{1}", "\n",
                    "$2Description : $1{2}", "\n",
                    "$2Version : $1{3}", "\n",
                    "$2URL : $1{4}", "\n",
                    "$2Dependencies : $1{5}", "\n",
                    "$2Requires NMS : $1{6}", "\n",
                    "$2Author(s) : $1{7}", "\n",
                    "$2Type : $1{8}", "\n",
                    "$2Source : $1{9}", "\n",
                    Utils.repeat(IntegratedResource.DEFAULT_PAGINATION_PADDING.asString(), 20)
            ), "darwinserver.info.extension.block");
    static final ResourceEntry EXTENSION_UNKNOWN = new ExternalResourceEntry("$4Could not find extension with ID '{0}'", "darwinserver.info.extension.unknown");
    static final ResourceEntry LANG_SWITCHED = new ExternalResourceEntry("$1Your preferred language has been switched to: $2{0}", "i18n.lang.updated");
    static final ResourceEntry MISSING_ARGUMENT = new ExternalResourceEntry("$4Missing value for argument '{0}'", "darwinserver.info.parameter.missing");
    static final ResourceEntry EXTENSION_RELOAD_SUCCESSFUL = new ExternalResourceEntry("$1Successfully reloaded '$2{0}$1'", "darwinserver.reload.single");
    static final ResourceEntry EXTENSION_RELOAD_FAILED = new ExternalResourceEntry("$4Failed to reload '{0}'", "darwinserver.reload.single.fail");
    static final ResourceEntry FULL_RELOAD_SUCCESSFUL = new ExternalResourceEntry("$1Successfully reloaded all extensions", "darwinserver.reload.all");

}
