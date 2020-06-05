# Darwin Server ![ci-status](https://api.travis-ci.com/GuusLieben/DarwinServer.svg?branch=api-7.1-1.12.2)  
**Project is in pre-alpha stages, please note there might be major changes without special notice**

## How to build
To build, use ```.\gradlew build```. Distributable files will be stored in `/dist/{date}/{commit-hash}`.

## Creating modules
To register a class as a module, simply add the `@Module` annotation to the class. Types annotated with this annotation will automatically be registered as listener and command holder.  
A basic example of a module is as follows :
```java
@Module(id = "example", name = "Example Module", description = "Is a module", authors = "GuusLieben")
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
@Module(id = "example", name = "Example Module", description = "Is a module", authors = "GuusLieben")
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
@Module(id = "example", name = "Example Module", description = "Is a module", authors = "GuusLieben")
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
@Module(id = "example", name = "Example Module", description = "Is a module", authors = "GuusLieben")
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
Several common utilities require a platform specific implementation, such as player management, item modification, and several others. 
Utilities cannot be added or implemented by single modules, and should only be implemented by platform-specific implementations of DarwinServer itself.
Abstract utilities are provided inside the `com.darwinreforged.server.core` package, and are marked by the `@Utility` annotation. To implement these utilities,
you can extend these types in your implementation. When loading the implementation of DarwinServer, types in the same package, and inner packages, will be scanned for utility implementations.
If more than one implementation exists for the same utility, the last one found by the scanner will be used.  
```java
@Utility("Sample utility")
public abstract class SampleUtils {
    
    public abstract String generateValue();
}


public class PlatformSampleUtils extends SampleUtils {
    
    public String generateValue() {
        //...
    }   
}
```

There are two ways to obtain utility instances through DarwinServer. The first option is using `get` and providing the class type of the utility you need. `get` will throw an IllegalStateException if no instance of the utility is present.
The second option is to use `getUtil`, again providing the class type of the needed utility. Unlike `get`, `getUtil` will not throw an exception, and instead returns an empty `Optional` if no implementation is present.

### File management
Storing, reading, and modifying files is often tedious. With different plugins using different file types, in different locations.
Therefore the FileManager type is made, enforcing the usage of YAML based files for smaller data and configuration files, and SQLite for bulk data storage.
By default two base directories exist, `data` and `config`. The exact location of these directories depends on the implementation of DarwinServer. By default, a new file will be created if it did not yet exist, this can however be disabled by the developer by providing an additional argument for `createIfNotExists`.   
When accessing files, an instance or class type of a module should be provided to determine where the files is stored, this uses the module id as sub-directory.
So for example, a module with id `sample_module` will have its configuration files stored in `config/sample_module` (depending on the implementation).  
The file manager uses two libraries to read and write to both YAML based files and SQLite. These are [Jackson](https://github.com/FasterXML/jackson) and [OrmLite](https://ormlite.com/).  
While YAML serialization and deserialization is done completely by the file manager, where you only need to provide the type of the object you wish to use, this isn't the case for SQLite.
SQLite storage requires you to create a custom model of the data you wish to store. To provide developers with the tools they need, without having to use additional dependencies, OrmLite comes packed with all builds of DarwinServer.
```java
@DatabaseTable(tableName = "sample")
public class StorageModel {

    @DatabaseField(dataType = DataType.INTEGER, canBeNull = false, columnName = "id")
    private int id;
    
    @DatabaseField(dataType = DataType.STRING, columnName = "user_email")
    private String userEmail;

    //...

}
```

As seen in the example above, the `userEmail` field uses a different name format for its column name. While Java's convention is to use camelCase, SQLite's convention is to use snake_case. 
While this is subject to change in later releases, it is expected of developers to provide models for their storage types.  
It is also possible to collect YAML from a remote source, this can be done using the `getYamlDataForUrl` method. This method returns a `Map<String, Object>` type by default, though it is also possible to use custom models. 
When using custom models in any file manager provided method, you'll be required to provide a default value in case an exception occurs.
 ```java
 @Module(id = "example", name = "Example Module", description = "Is a module", authors = "GuusLieben")
 public class ExampleModule {
 
     @Listener
     public void onServerInit(ServerInitEvent event) {
         File configFile = DarwinServer.get(FileManager.class).getYamlConfigFile(this);
         //...
     }
 
 }
 ```

### Time API
#### Scheduling
Some actions may be required to run on scheduled moments. To provide you with the ability to do this easily `CommonUtils.Scheduler` is available using `CommonUtils.scheduler()`. 
Schedulers have the ability to run on the main thread, or async. Schedulers are expected to have a name associated to them, so they can be easily identified.
```java
Scheduler scheduler = DarwinServer.get(CommonUtils.class).scheduler()
    .async()
    .name("Sample scheduler")
    .interval(5, ChronoUnit.MINUTES)
    .delayTicks(10)
    .execute(() -> {
        //...
    });
scheduler.submit();
```
  
#### Cooldown / Timeout
For some actions it is required that a player cannot perform the same action several times within a certain timespan. `CommonUtils` provides an easy API to solve this, the Cooldown API.
This API is available statically. To mark a player to cool down, use `CommonUtils.registerUuidTimeout`, providing the UUID of the player, and the amount of milliseconds the player should be in cooldown for.
It is possible to either override the last cooldown if the player is not yet marked, or to ignore the mark if they are already marked. To get the time since the player was (last) put in cooldown you can use `getTimeSinceLastUuidTimeout` providing the UUID of the player.
This will return a `TimeDifference` object, with which you can easily compare the amount of time in several time units. To unregister a player from a cooldown, simply use `unregisterUuidTimeout`.

```java
@Listener
public void onPlayerMove(PlayerMoveEvent event) {
    DarwinPlayer player = (DarwinPlayer) event.getTarget();
    Optional<TimeDifference> diff = CommonUtils.getTimeSinceLastUuidTimeout(player.getUniqueId(), this);
    if ((!diff.isPresent()) || diff.get().getSeconds() > 10) {
        // If the player was present, we can now reset them
        CommonUtils.unregisterUuidTimeout(player.getUniqueId(), this);
        //...
        if (someCondition) CommonUtils.registerUuidTimeout(player.getUniqueId(), this, false);
        });
    }
}
```

## Chat API
To easily generate text with preset actions for click and hover events, you can use the Chat API. Using `Text` you can create and append text to a message. Currently it is not possible to have more than one hover, and one click action per `Text` instance, this is however scheduled for a future release.
To add click events to a `Text` object, use `setClickEvent` providing an instance of `ClickEvent`. Similarly, you can add hover events with `setHoverEvent`, providing an instance of `HoverEvent`.

### Pagination
To create paginated results for lists of text objects, you can use `Pagination`. To build a `Pagination` instance, use `Pagination.builder()`. 
Pagination can be build using the following values; `padding`, `linesPerPage`, `header`, `footer`, `title`, and `contents`. If no value is provided for one of these fields, the default value of the implementation will be used.
```java
Pagination.builder()
        .title(Text.of(DefaultTranslations.DARWIN_MODULE_TITLE.s()))
        .padding(Text.of(DefaultTranslations.DARWIN_MODULE_PADDING.s()))
        .contents(moduleContext)
        .header(header)
        .build().sendTo(src);
```
