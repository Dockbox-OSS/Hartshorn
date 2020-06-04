# Darwin Server ![ci-status](https://api.travis-ci.com/GuusLieben/DarwinServer.svg?branch=api-7.1-1.12.2)  
**Project is in pre-alpha stages, please note there might be major changes without special notice**

## How to build
To build, use ```.\gradlew build```. Distributable files will be stored in `/dist/{date}/{commit-hash}`.

## Creating modules
To register a class as a module, simply add the `@Module` annotation to the class. Types annotated with this annotation will automatically be registered as listener and command holder.  
A basic example of a module is as follows :
```java
@Module(id = "example", name = "Example Module", description = "Is a module", authors = "DeveloperMan")
public class ExampleModule {
// ...
}
```

### Requiring dependencies
If your module has specific dependencies outside of a platform and DarwinServer Core, you can indicate dependencies using the `dependencies` field in `@Module`.
By default DarwinServer provides dependency constants for FastAsyncWorldEdit, WorldEdit, PlotSquared, MultiChat, VoxelSniper, and Placeholder API.
By default all dependencies should be core-based. However, if your module depends on a specific platform's implementation of a plugin (like EssentialsX vs Nucleus), you can require those as well.
To add dependencies outside of the default values, use `Dependency.of` providing either a class type, or the String value of said class type.

```java
// Dependency using String value
Dependency.of("com.intellectualcrafters.plot.PS");

// Dependency using class type (this may cause a NoClassDefFoundException to throw, so only use this if you are prepared for that!)
Dependency.of(PS.class);
```

### Creating translations
The guidelines for Modules require all text shown to players and other MessageReceivers to be stored in a configurable translation file. To make this process easier a default type is provided to generate, store, and process translations, this type is called `Translations`.
To register custom translations, use `Translation.create` providing a key of the translation, and the default value. All translations are stored under `/{data_dir}/darwinserver/translations/translations.yml`. If the registration is created inside a module, the module ID will be used as category to make it easier to locate associated translations and prevent duplicates.
If a translation is created outside of a module, the category will default to `other`. To change this you can use the `@ConfigSetting` annotation on the type declaring the translation. `@ConfigSetting` requires a category key to be provided. Translations should be created as constants using either an `enum` type or using `public static final`. While it is possible to declare translations per-instance, it may cause unintended side-effects.

```java
@Module(id = "example", name = "Example Module", description = "Is a module", authors = "DeveloperMan")
public class ExampleModule {
    
    // Will use category "example" as the module ID is present
    public static final Translation HELLO_WORLD = Translation.create("hello_world", "Hello, world!");
    
}
```

```java
@ConfigSetting("config_example")
public class ExampleModule {
    
    // Will use category "config_example" as there is no module ID present, but @ConfigSetting declares a category
    public static final Translation HELLO_WORLD = Translation.create("hello_world", "Hello, world!");
    
}
```

### Adding permissions
**Not currently possible to create custom permissions** _(Scheduled for future release)_

## Creating listeners
To create listeners inside your modules, use the `@Listener` annotation, and include the event type as the first parameter.
To listen for early server initialization use `ServerInitEvent`, for startup use `ServerStartedEvent`, and for reload use `ServerReloadEvent`.
An example module implementing these events is as follows :
```java
@Module(id = "example", name = "Example Module", description = "Is a module", authors = "DeveloperMan")
public class ExampleModule {

    @Listener
    public void onServerInit(ServerInitEvent event) {
        // ...
    }

    @Listener
    public void onServerStart(ServerStartedEvent event) {
        // ...
    }

    @Listener
    public void onServerReload(ServerReloadEvent event) {
        // ...
    }    
}
```
Several other events are registered located in ``com.darwinreforged.server.core.events``.

## Creating commands
To register commands, use the `@Command` annotation. Command methods can only use two parameter types, any type extending `CommandSender`, and `CommandContext`. All commands require a usage context, which is defined as follows :
* `<Argument>` Required Argument
* `[Argument]` Optional Argument
* `-f Value` and `--flag Value` where value is optional  
Argument is built like `Value` or `Name{Value}`,
Value is build like `Type` or `Type:Permission`   

Type allows the following:
* **Native types** : `bool`, `double`, `entity`, `integer`, `long`, `remainingstring`, `string`, `uuid`
* **Internal types** : `location`, `player`, `module`, `user`, `vector`, `world`
* **FAWE types** : `editsession`, `mask`, `pattern`, `region`, `pattern`

Types are case insensitive. Permission is build as known with characters matching `~^\\w.*$~i` 
Arguments and flags are made available in the CommandContext, an example is as follows :
```java
@Module(id = "example", name = "Example Module", description = "Is a module", authors = "DeveloperMan")
public class ExampleModule {

    @Command(aliases = {"command", "cmd"}, usage = "command <player> [value]", desc = "Does something with the given player", context = "command <player{Player}> [value{String}]")
    public void onCommand(CommandSender src, CommandContext ctx) {
        Optional<CommandArgument<String>> valueCandidiate = ctx.getStringArgument("value");
        Optional<CommandArgument<DarwinPlayer>> playerCandidate = ctx.getArgument("player", DarwinPlayer.class);
        playerCandidate.ifPresent(playerArg -> {
            System.out.println(playerArg.getKey()); // = "player"
            DarwinPlayer player = playerArg.getValue();
            // ...
        });
    }

}
```

### Argument parsing
Command arguments, flags, and relevant context are stored in the CommandContext object passed into the command method. 
The CommandContext provides several methods to check the presence, and values of all arguments and flags.   
By default it is possible to inject the CommandSender, usually a player or the console, into the method parameters. 
However some use-cases may prefer to use only the CommandContext, without losing access to the CommandSender. To solve this, the following methods are made available : `getSender`, `getWorld`, `getLocation`. These methods provide the CommandSender instance, and the location at which the command was performed.  

It is also possible to check the presence of specific flags and arguments without them being made available directly. This can be done using the `hasArgument` and `hasFlag` methods, these methods can be used directly in a conditional statement.
Another method is to use `getNArgument` or `getNFlag` (`N` being the type, if any), which returns an `Optional` instance of the requested argument. Default methods and casts exist for the following native types : `String`, `Boolean`, `Integer`, `Double`, `Float`.

```java
@Command(aliases = {"command", "cmd"}, usage = "command <player> [value]", desc = "Does something with the given player", context = "command <player{Player}> [value{String}]")
public void onCommand(CommandSender src, CommandContext ctx) {
    // Check if the argument is present
    if (ctx.hasArgument("value")) {
    // ...
    }
    
    // Get optional argument of specific type
    Optional<CommandArgument<DarwinPlayer>> playerCandidate = ctx.getArgument("player", DarwinPlayer.class);
}
```

Sometimes a module will require the same custom type argument for several commands. To prevent the need of implementing the same solution again and again, it is possible to create custom argument parsers.
To create a custom parser, create a type extending `TypeArgumentParser`. This type will need to implement a single method, `parse`. This method has two arguments, the raw `String` value of the argument wrapped in a `AbstractCommandValue`, which also provides access to the key of the argument or flag.
It also has a secondary argument, which is to be provided by the implementation using the custom parser, this will indicate the return type of the `parse` method.
An example of a custom parser is an enum parser, which is as follows : 

```java
public static final class EnumArgumentParser extends TypeArgumentParser {
    @Override
    public <A> Optional<A> parse(AbstractCommandValue<String> commandValue, Class<A> type) {
        if (type.isEnum()) {
            String value = commandValue.getValue();
            try {
                Class<Enum> enumType = (Class<Enum>) type;
                return Optional.of((A) Enum.valueOf(enumType, value));
            } catch (IllegalArgumentException | NullPointerException e) {
                DarwinServer.getLog().warn("Attempted to get value of '" + value + "' and caught error : " + e.getMessage());
            }
        }
        return Optional.empty();
    }
}
```
 

## Utilities
_TODO_

### File management
_TODO_

### Time API
#### Scheduling
_TODO_
#### Cooldown
_TODO_
#### Time difference
_TODO_

## Chat API
_TODO_
