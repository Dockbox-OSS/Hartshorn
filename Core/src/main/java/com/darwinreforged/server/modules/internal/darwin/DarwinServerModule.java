package com.darwinreforged.server.modules.internal.darwin;


import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.commands.context.CommandContext;
import com.darwinreforged.server.core.commands.annotations.Permission;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;

@Module(id = "darwinserver", name = "Server Config Module", description = "Native module used for configurations from DarwinServer only", authors = {"GuusLieben"})
public class DarwinServerModule {

    @Command(
            aliases = {"dtest", "dt"}, // The aliases for the command, by default the first alias will be used as primary alias
            desc = "Darwin Server module information", // The short description for the command
            usage = "dserver <module>", // The usage for the command, this is returned to the player if their syntax was incorrect
            min = 2, // Minimum amount of arguments
            max = 5, // Maxiumum amount of arguments
            // Long description for the command
            help = "Returns information about the given module, if no module ID is provided this will return a list of all modules with their appropriate state and source",
            join = true, // Whether or not to join any leftover arguments (requires max() to be set)
            anyFlags = false, // Whether or not the command accepts any flag, only affects flags() if true
            flags = {"-f", "--flag"}, // Possible flags for the command, will only check if anyFlags() is false
            valueFlags = {"-n", "--noise"}, // Possible flags which require a value, unaffected by anyFlags()
            parseFlags = true, // Whether or not to parse flag values to native types (bool, int, double, float, string)
            parseArgs = true, // Whether or not to parse arguments to native types (bool, int, double, float, string)
            args = {"world", "player", "module", "plugin", "varargs"}, // Argument keys, can be used to quickly get an argument from the context
            injectLocations = true // Will inject location parameters like World and Location (uses the location of the command source)
    )
    // The permissions required for the command, defaults to ADMIN_BYPASS if absent
    @Permission({Permissions.ADMIN_BYPASS, Permissions.BRUSH_TT_USE})
    public void modules(CommandSender commandSender, CommandContext ctx) {
        // ...
    }

}
