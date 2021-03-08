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

package org.dockbox.selene.common.command.convert.impl;

import org.dockbox.selene.api.command.context.CommandValue;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.common.command.convert.AbstractArgumentConverter;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CommandValueConverter<T> extends AbstractArgumentConverter<T> {

  private final BiFunction<CommandSource, String, Exceptional<T>> converter;
  private final Function<String, Collection<String>> suggestionProvider;

  public CommandValueConverter(
      Class<T> type,
      Function<String, Exceptional<T>> converter,
      Function<String, Collection<String>> suggestionProvider,
      String... keys) {
    super(type, keys);
    this.converter = (cs, in) -> converter.apply(in);
    this.suggestionProvider = suggestionProvider;
  }

  public CommandValueConverter(
      Class<T> type,
      BiFunction<CommandSource, String, Exceptional<T>> converter,
      Function<String, Collection<String>> suggestionProvider,
      String... keys) {
    super(type, keys);
    this.converter = converter;
    this.suggestionProvider = suggestionProvider;
  }

  public CommandValueConverter(
      Class<T> type, Function<String, Exceptional<T>> converter, String... keys) {
    super(type, keys);
    this.converter = (cs, in) -> converter.apply(in);
    this.suggestionProvider = in -> SeleneUtils.emptyList();
  }

  public CommandValueConverter(
      Class<T> type, BiFunction<CommandSource, String, Exceptional<T>> converter, String... keys) {
    super(type, keys);
    this.converter = converter;
    this.suggestionProvider = in -> SeleneUtils.emptyList();
  }

  @Override
  public Exceptional<T> convert(CommandSource source, String argument) {
    return this.converter.apply(source, argument);
  }

  @Override
  public Exceptional<T> convert(CommandSource source, CommandValue<String> value) {
    return this.convert(source, value.getValue());
  }

  @Override
  public Collection<String> getSuggestions(CommandSource source, String argument) {
    return this.suggestionProvider.apply(argument);
  }
}
