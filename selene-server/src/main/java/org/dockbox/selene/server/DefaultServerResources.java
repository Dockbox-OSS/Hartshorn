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

package org.dockbox.selene.server;

import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.i18n.annotations.Resources;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.DefaultResource;
import org.dockbox.selene.api.i18n.entry.Resource;
import org.dockbox.selene.util.SeleneUtils;

@SuppressWarnings({ "StaticMethodOnlyUsedInOneClass", "ClassWithTooManyFields" })
@Resources(DefaultServer.class)
public enum DefaultServerResources {
    ;
    public static final ResourceEntry PAGINATION_TITLE = new Resource("$1Selene Server Info", "selene.pagination.title");
    public static final ResourceEntry SERVER_HEADER = new Resource("$2Selene $3($1Version$3: $1{0}$3)", "selene.info.header");
    public static final ResourceEntry SERVER_UPDATE = new Resource("$2Last updated$3: $1{0}", "selene.info.updated");
    public static final ResourceEntry SERVER_AUTHORS = new Resource("$2Authors$3: $1{0}", "selene.info.authors");
    public static final ResourceEntry SERVER_MODULES = new Resource("$2Modules$3:", "selene.info.modules");
    public static final ResourceEntry MODULE_ROW = new Resource("$3 - $1{0} $3- $2{1}", "selene.info.module.row");
    public static final ResourceEntry MODULE_ROW_HOVER = new Resource("$2Details for '$1{0}$2'", "selene.info.module.hover");
    public static final ResourceEntry MODULE_INFO_BLOCK = new Resource(
            String.join(
                    "",
                    SeleneUtils.repeat(DefaultResource.DEFAULT_PAGINATION_PADDING.asString(), 20),
                    "\n",
                    "$2Name : $1{0}",
                    "\n",
                    "$2ID : $1{1}",
                    "\n",
                    "$2Description : $1{2}",
                    "\n",
                    "$2Dependencies : $1{3}",
                    "\n",
                    "$2Author(s) : $1{4}",
                    "\n",
                    "$2Source : $1{5}",
                    "\n",
                    SeleneUtils.repeat(DefaultResource.DEFAULT_PAGINATION_PADDING.asString(), 20)),
            "selene.info.module.block");
    public static final ResourceEntry UNKNOWN_MODULE = new Resource("$4Could not find module with ID '{0}'", "selene.info.module.unknown");
    public static final ResourceEntry LANG_SWITCHED = new Resource("$1Your preferred language has been switched to: $2{0}", "i18n.lang.updated");
    public static final ResourceEntry LANG_SWITCHED_OTHER = new Resource(
            "$1The language preference for $2{0} $1has been switched to: $2{1}",
            "i18n.lang.updated.other");
    public static final ResourceEntry MISSING_ARGUMENT = new Resource("$4Missing value for argument '{0}'", "selene.info.parameter.missing");
    public static final ResourceEntry MODULE_RELOAD_SUCCESSFUL = new Resource("$1Successfully reloaded '$2{0}$1'", "selene.reload.single");
    public static final ResourceEntry NODULE_RELOAD_FAILED = new Resource("$4Failed to reload '{0}'", "selene.reload.single.fail");
    public static final ResourceEntry FULL_RELOAD_SUCCESSFUL = new Resource("$1Successfully reloaded all modules", "selene.reload.all");
    public static final ResourceEntry CONFIRM_INVALID_ID = new Resource("$4Could not confirm command: Invalid runner ID", "selene.confirm.invalid.id");
    public static final ResourceEntry CONFIRM_FAILED = new Resource("$4Could not confirm command", "selene.confirm.invalid.other");
    public static final ResourceEntry CONFIRM_WRONG_SOURCE = new Resource(
            "$4This command can only be used by identifiable sources (players, console) matching the original source of the command",
            "selene.confirm.invalid.source");
    public static final ResourceEntry PLATFORM_INFORMATION = new Resource(
            "$2Platform: $1{0} $3(version: {1})\n"
                    + "$2Minecraft: $1{2}\n"
                    + "$2Java: $1{3} $3(vendor: {4})\n"
                    + "$2JVM: $1{5} $3(version: {6}, vendor: {7})\n"
                    + "$2Runtime: $1{8} $3(class version: {9})",
            "selene.info.platform");

    public static final String SELENE_ADMIN = SeleneInformation.PROJECT_ID + ".admin";
}
