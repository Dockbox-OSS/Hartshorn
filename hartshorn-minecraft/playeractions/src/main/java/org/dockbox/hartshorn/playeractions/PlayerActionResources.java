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

package org.dockbox.hartshorn.playeractions;

import org.dockbox.hartshorn.api.i18n.annotations.Resource;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.di.annotations.Service;

@Service(owner = PlayerActions.class)
public interface PlayerActionResources {
    
    @Resource(value = "$4You are not allowed to teleport while in spectator mode", key = "playeractions.spectator.notallowed")
    ResourceEntry getSpectatorNotAllowed();

    @Resource(value = "$4You are denied from the plot you are teleporting to", key = "playeractions.plot.denied")
    ResourceEntry getDeniedFromPlot();

    @Resource(value = "$4You are outside a plot", key = "playeractions.plot.outside")
    ResourceEntry getOutsidePlot();

    @Resource(value = "$4You do not have permission to interact with entities here", key = "playeractions.plot.interact")
    ResourceEntry getInteractionError();

    @Resource(value = "$4You are not permitted to move in this world", key = "playeractions.rootworld")
    ResourceEntry getMoveError();

    @Resource(value = "$1Hi there! We detected you prefer to see messages in {0}, if you would like our commands to send you messages in {0} click on this message to change your language preference.", key = "playeractions.lang.notify")
    ResourceEntry getLanguageNotification(String languageEnglish);

    @Resource(value = "$1Click here to change your language preference to $2{0}", key = "playeractions.lang.notify.hover")
    ResourceEntry getLanguageNotificationHover(String languageEnglish);
}
