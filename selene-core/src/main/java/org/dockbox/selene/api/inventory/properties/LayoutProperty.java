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

package org.dockbox.selene.api.inventory.properties;

import org.dockbox.selene.api.inventory.InventoryLayout;
import org.dockbox.selene.api.server.properties.InjectorProperty;

public class LayoutProperty implements InjectorProperty<InventoryLayout> {

  public static final String KEY = "SeleneInternalInventoryTypeKey";
  private final InventoryLayout layout;

  public LayoutProperty(InventoryLayout layout) {
    this.layout = layout;
  }

  @Override
  public String getKey() {
    return LayoutProperty.KEY;
  }

  @Override
  public InventoryLayout getObject() {
    return this.layout;
  }
}
