package com.darwinreforged.server.api.commands.annotations;

import com.darwinreforged.server.api.commands.enums.ArgumentType;

public @interface CommandArgument {
    String value();
    boolean optional() default false;
    boolean joinRemaining() default false;
    ArgumentType type() default ArgumentType.STRING;
}
