package com.darwinreforged.server.core.commands.element.function;

import java.util.stream.Stream;


public interface ChainOptions<D> {

    Stream<String> get(D d);
}
