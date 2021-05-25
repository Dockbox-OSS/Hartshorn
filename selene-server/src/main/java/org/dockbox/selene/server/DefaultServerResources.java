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

import org.dockbox.selene.api.i18n.annotations.Resource;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.di.annotations.Service;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "StaticMethodOnlyUsedInOneClass", "ClassWithTooManyFields" })
@Service(owner = DefaultServer.class)
public interface DefaultServerResources {

    @Resource(value = "&m$2====================\n" +
            "$2Name : $1{0}\n" +
            "$2ID : $1{1}\n" +
            "$2Dependencies : $1{2}\n" +
            "&m$2====================\n")
    ResourceEntry getInfoServiceBlock(String name, String id, String dependencies);

    @Resource(value = "$4Could not find service with ID '{0}'")
    ResourceEntry getInfoServiceUnknown();

    @Resource(value = "$1Your preferred language has been switched to: $2{0}", key = "i18n.lang.updated")
    ResourceEntry getLanguageUpdated(String languageLocalized);

    @Resource(value = "$1The language preference for $2{0} $1has been switched to: $2{1}", key = "i18n.lang.updated.other")
    ResourceEntry getOtherLanguageUpdated(String name, String languageLocalized);

    @Resource(value = "$4Missing value for argument '{0}'")
    ResourceEntry getInfoParameterMissing();

    @Resource(value = "$1Successfully reloaded '$2{0}$1'", key = "selene.reload.single")
    ResourceEntry getReloadSuccessful(String name);

    @Resource(value = "$4Failed to reload '{0}'", key = "selene.reload.single.fail")
    ResourceEntry getReloadFailed(String name);

    @Resource(value = "$1Successfully reloaded all services")
    ResourceEntry getReloadAll();

    @Resource(value = "$4Could not confirm command: Invalid runner ID")
    ResourceEntry getConfirmInvalidId();

    @Resource(value = "$4Could not confirm command")
    ResourceEntry getConfirmInvalidOther();

    @Resource(value = "$4This command can only be used by identifiable sources (players, console) matching the original source of the command")
    ResourceEntry getConfirmInvalidSource();

    @Resource(value = "$2Platform: $1{0} $3(version: {1})\n" + "$2Minecraft: $1{2}\n" + "$2Java: $1{3} $3(vendor: {4})\n" + "$2JVM: $1{5} $3(version: {6}, vendor: {7})\n" + "$2Runtime: $1{8} $3(class version: {9})", key = "selene.info.platform")
    ResourceEntry getPlatformInformation(String displayName, String platformVersion, String mcVersion,
                                         Object javaVersion, Object javaVendor,
                                         Object vmVersion, Object vmName,
                                         Object vmVendor, Object runtimeVersion, Object classVersion);

    @Resource(value = "$1Selene Server Info")
    ResourceEntry getPaginationTitle();

    @Resource(value = "$2Selene $3($1Version$3: $1{0}$3)")
    ResourceEntry getInfoHeader(@NotNull String version);

    @Resource(value = "$2Services$3:", key = "selene.info.services")
    ResourceEntry getServices();

    @Resource(value = "$3 - $1{0} $3- $2{1}", key = "selene.info.service.row")
    ResourceEntry getServiceRow(String name, String id);

    @Resource(value = "$2Details for '$1{0}$2'", key = "selene.info.service.hover")
    ResourceEntry getServiceRowHover(String name);

    @Resource(value = "$4This command can only be used by players", key = "selene.invalid.source")
    ResourceEntry getWrongSource();
}
