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

package org.dockbox.selene.api.annotations.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface to mark a method as a <a href="https://discord.com/">Discord</a> command holder.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DiscordCommand {
  /**
   * The command/alias for the command, excluding a prefix.
   *
   * @return the command/alias
   */
  String command();

  /**
   * The ID of the channel where the command should be listened for. If {@code *} is used, all
   * channels a bot/webhook has access to will be listened to.
   *
   * @return the channel ID
   */
  String channelId();

  /**
   * The ID of the minimum role a user should have in order for them to be able to use the command.
   * If {@code *} is used, anyone can execute the command no matter their roles.
   *
   * @return the role ID
   */
  String minimumRankId();

  /**
   * The listening level, indicating whether commands should be listened for in private chat
   * channels, chat channels (guild), or both. If {@link DiscordCommand#channelId()} is set that
   * will always be applied <i>after</i> this filter.
   *
   * @return the listening level
   */
  ListeningLevel listeningLevel() default ListeningLevel.BOTH;

  enum ListeningLevel {
    /** Only private chat channels */
    PRIVATE_ONLY,
    /** Only guild chat channels */
    CHANNEL_ONLY,
    /** Both private chat and guild chat channels */
    BOTH
  }
}
