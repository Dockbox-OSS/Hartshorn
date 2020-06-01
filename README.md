# Darwin Server ![ci-status](https://api.travis-ci.com/GuusLieben/DarwinServer.svg?branch=api-7.1-1.12.2)  
**Project is in pre-alpha stages, please note there might be massive changes without special notice**

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
Argument is built like `Value` or `Name{Value}`   
Value is build like `Type` or `Type:Permission`   
Type allows the following:   
**Native types**  
`bool`, `double`, `entity`, `integer`, `long`, `remainingstring`, `string`, `uuid`   
**Internal types**  
`location`, `player`, `module`, `user`, `vector`, `world`  
**FAWE types**  
`editsession`, `mask`, `pattern`, `region`, `pattern`     
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

## Creating translations
_TODO_

## Requiring dependencies
_TODO_

## Adding permissions
_TODO_

## File management
_TODO_

## Obtaining and implementing utilities
_TODO_

## Chat API
_TODO_

## Time API
### Scheduling
_TODO_  
### Cooldown
_TODO_
### Time difference
_TODO_
