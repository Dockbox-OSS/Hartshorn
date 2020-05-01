package com.darwinreforged.server.core.util.commands.element.function;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;


@FunctionalInterface
public interface Options extends Supplier<Stream<String>> {

    Options EMPTY = Stream::empty;

    static Options of(Collection<String> collection) {
        return collection::stream;
    }

    static Options of(String... options) {
        return () -> Stream.of(options);
    }
}
