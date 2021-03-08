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

package org.dockbox.selene.common.command.values;

import java.util.List;

public abstract class AbstractFlagCollection<T> {

  private T reference;

  protected AbstractFlagCollection(T reference) {
    this.reference = reference;
  }

  protected AbstractFlagCollection() {}

  public T getReference() {
    return this.reference;
  }

  public void setReference(T reference) {
    this.reference = reference;
  }

  public abstract void addNamedFlag(String name);

  public abstract void addNamedPermissionFlag(String name, String permission);

  public abstract void addValueBasedFlag(String name, AbstractArgumentValue<?> value);

  public abstract List<AbstractArgumentElement<?>> buildAndCombines(
      AbstractArgumentElement<?> element);
}
