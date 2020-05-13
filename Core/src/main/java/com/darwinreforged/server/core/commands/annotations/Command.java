package com.darwinreforged.server.core.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 The interface Command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Command {

    /**
     A list of aliases for the command. The first alias is the most
     important -- it is the main name of the command. (The method name
     is never used for anything).

     @return Aliases for a command
     */
    String[] aliases();

    /**
     Usage instruction. Example text for usage could be
     {@code [-h harps] [name] [message]}.

     @return Usage instructions for a command
     */
    String usage();

    /**
     Desc string.

     @return A short description for the command.
     */
    String desc();

    /**
     The minimum number of arguments. This should be 0 or above.

     @return the minimum number of arguments
     */
    int min() default 0;

    /**
     The maximum number of arguments. Use -1 for an unlimited number
     of arguments.

     @return the maximum number of arguments
     */
    int max() default -1;

    /**
     Flags allow special processing for flags such as -h in the command,
     allowing users to easily turn on a flag. Use A-Z+ and a-z+ as possible flags.

     @return Flags matching a-zA-Z+
     */
    String[] flags() default "";


    /**
     Flags allow special processing for flags such as -t [type] in the command,
     allowing users to easily turn on a flag. Use A-Z+ and a-z+ as possible flags.
     These flags will be parsed with their value being the first argument behind it.

     @return Flags matching a-zA-Z+
     */
    String[] valueFlags() default "";

    /**
     Help string.

     @return A long description for the command.
     */
    String help() default "";

    /**
     Get whether any flag can be used. Only affects flags().

     @return true if so
     */
    boolean anyFlags() default false;

    /**
     Get whether remaining arguments are joined into a single String object.
     Requires max() to be greater than 0.

     @return true if so
     */
    boolean join() default false;

    /**
     Get whether flag values should be parsed into native types. Requires flags()
     or anyFlags() to be present.

     @return true if so
     */
    boolean parseFlags() default false;

    /**
     Get whether argument values should be parsed into native types.

     @return true if so
     */
    boolean parseArgs() default false;

    /**
     Whether or not location type should be injected into the parameters, if
     false the command cannot register if it has location parameter types.

     @return true if so
     */
    boolean injectLocations() default true;

    /**
     A list of argument names in order of their position inside the command
     syntax. Requires max() to be present

     @return Argument names
     */
    String[] args() default "";
}
