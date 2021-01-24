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

package org.dockbox.selene.integrated;

import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.core.annotations.i18n.Resources;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.i18n.entry.Resource;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
@Resources(responsibleExtension = IntegratedServerExtension.class)
enum IntegratedServerResources {
    ;
    static final ResourceEntry PAGINATION_TITLE = new Resource("$1Selene Server Info", "selene.pagination.title");
    static final ResourceEntry SERVER_HEADER = new Resource("$2Selene $3($1Version$3: $1{0}$3)", "selene.info.header");
    static final ResourceEntry SERVER_UPDATE = new Resource("$2Last updated$3: $1{0}", "selene.info.updated");
    static final ResourceEntry SERVER_AUTHORS = new Resource("$2Authors$3: $1{0}", "selene.info.authors");
    static final ResourceEntry SERVER_EXTENSIONS = new Resource("$2Extensions$3:", "selene.info.extensions");
    static final ResourceEntry EXTENSION_ROW = new Resource("$3 - $1{0} $3- $2{1}", "selene.info.extension.row");
    static final ResourceEntry EXTENSION_ROW_HOVER = new Resource("$2Details for '$1{0}$2'", "selene.info.extension.hover");
    static final ResourceEntry EXTENSION_INFO_BLOCK = new Resource(
            String.join("",
                    SeleneUtils.repeat(IntegratedResource.DEFAULT_PAGINATION_PADDING.asString(), 20), "\n",
                    "$2Name : $1{0}", "\n",
                    "$2ID : $1{1}", "\n",
                    "$2Description : $1{2}", "\n",
                    "$2Dependencies : $1{3}", "\n",
                    "$2Author(s) : $1{4}", "\n",
                    "$2Source : $1{5}", "\n",
                    SeleneUtils.repeat(IntegratedResource.DEFAULT_PAGINATION_PADDING.asString(), 20)
            ), "selene.info.extension.block");
    static final ResourceEntry EXTENSION_UNKNOWN = new Resource("$4Could not find extension with ID '{0}'", "selene.info.extension.unknown");
    static final ResourceEntry LANG_SWITCHED = new Resource("$1Your preferred language has been switched to: $2{0}", "i18n.lang.updated");
    static final ResourceEntry LANG_SWITCHED_OTHER = new Resource("$1The language preference for $2{0} $1has been switched to: $2{1}", "i18n.lang.updated.other");
    static final ResourceEntry MISSING_ARGUMENT = new Resource("$4Missing value for argument '{0}'", "selene.info.parameter.missing");
    static final ResourceEntry EXTENSION_RELOAD_SUCCESSFUL = new Resource("$1Successfully reloaded '$2{0}$1'", "selene.reload.single");
    static final ResourceEntry EXTENSION_RELOAD_FAILED = new Resource("$4Failed to reload '{0}'", "selene.reload.single.fail");
    static final ResourceEntry FULL_RELOAD_SUCCESSFUL = new Resource("$1Successfully reloaded all extensions", "selene.reload.all");
    static final ResourceEntry CONFIRM_INVALID_ID = new Resource("$4Could not confirm command: Invalid runner ID", "selene.confirm.invalid.id");
    static final ResourceEntry CONFIRM_FAILED = new Resource("$4Could not confirm command", "selene.confirm.invalid.other");
    static final ResourceEntry CONFIRM_WRONG_SOURCE = new Resource("$4This command can only be used by identifiable sources (players, console) matching the original source of the command", "selene.confirm.invalid.source");
    static final ResourceEntry PLATFORM_INFORMATION = new Resource(
            "$2Platform: $1{0} $3(version: {1})\n" +
                    "$2Minecraft: $1{2}\n" +
                    "$2Java: $1{3} $3(vendor: {4})\n" +
                    "$2JVM: $1{5} $3(version: {6}, vendor: {7})\n" +
                    "$2Runtime: $1{8} $3(class version: {9})",
            "selene.info.platform");

}
