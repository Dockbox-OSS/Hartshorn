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

package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.HttpWebServer;

import java.util.Set;

public interface MVCInitializer {

    String TEMPLATE_ROOT = HttpWebServer.WEB_INF + "templates/";

    void initialize(ApplicationContext applicationContext) throws ApplicationException;
    String transform(ViewTemplate template, ViewModel model) throws ApplicationException;
    Set<ViewTemplate> templates();
}
