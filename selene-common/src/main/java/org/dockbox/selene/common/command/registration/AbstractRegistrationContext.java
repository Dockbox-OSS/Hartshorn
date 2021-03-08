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

package org.dockbox.selene.common.command.registration;

import org.dockbox.selene.api.annotations.command.Command;
import org.dockbox.selene.api.command.context.CommandContext;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.i18n.entry.IntegratedResource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.targets.Identifiable;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.List;
import java.util.UUID;

public abstract class AbstractRegistrationContext {

  private final List<String> aliases = SeleneUtils.emptyConcurrentList();
  private final Command command;

  protected AbstractRegistrationContext(Command command) {
    this.command = command;
    for (String alias : command.aliases()) this.addAlias(alias);
  }

  public void addAlias(String alias) {
    if (null != alias) this.aliases.add(alias);
  }

  public Command getCommand() {
    return this.command;
  }

  public abstract Exceptional<IntegratedResource> call(
      CommandSource source, CommandContext context);

  public static String getRegistrationId(Identifiable sender, CommandContext ctx) {
    UUID uuid = sender.getUniqueId();
    String alias = ctx.alias();
    return uuid + "$" + alias;
  }

  public String getPrimaryAlias() {
    return this.getAliases().get(0);
  }

  public List<String> getAliases() {
    return this.aliases;
  }
}
