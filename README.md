# Darwin Libraries  ![build status](https://travis-ci.org/darwin-reforged/Darwin-Libraries.svg?branch=master)
**Project is in Alpha stages, please note there might be massive changes without special notice**

## Module: Commons
This module combines all other modules and registers itself as a Sponge plugin. This module is responsible for delivering all Manager classes in an organized manner.

## Module: Sponge
This module provides several levels of Managers responsible for tasks related to Sponge.

## Module: Forge
This module provides several levels of Managers responsible for tasks related to Forge. It also offers several wrappers of Forge objects, so developers do not have to setup a workspace with ForgeGradle when developing Sponge(Forge) plugins.

## How to build
```.\gradlew build -p modules```

The built file (`modules-{version}.jar`) can then be found under `./modules/build/libs`.

It is possible to build separate modules using their respective `build.gradle` files.

Or go to : https://travis-ci.org/darwin-reforged/Darwin-Libraries

## Code examples
### @Command usage
```java
// Using @Command on a class will register the base command and its required permission
@Command(command = "help", permission = "darwin.help")
 public class SampleClass {
    
     // Using @Command(isParent = true) will inherit the command from the class, which will not register a sub-command. 
     // This will ignore any other attributes in @Command on the method.
     @Command(isParent = true)
     public static void sampleCommandMethod(CommandSource src, CommandContext args) {
         System.out.println("Parent command called by " + src);
     }

     // Using @Command(command = "mod") will register the method as a sub-command, all available settings to @Command 
     // are valid here .
     @Command(command = "mod")
     public static void sampleSubCommand(CommandSource src, CommandContext args) {
         System.out.println("Sub command called by " + src);
     }  
 }
```

### Sponge Manager
The following example showcases several basic use cases of provided Manager classes.
```java
// Get different directories and default files
Path configDir = Darwin.getFileManager().getConfigDirectory(this);
Path dataDir = Darwin.getFileManager().getDataDirectory(this);
File configFile = Darwin.getFileManager().getConfigFile(this);

// Register commands
Darwin.getCommandManager().registerCommandClass(SampleCommand.class, this);

// Store data to a file
Map<String, Object> sampleData = new HashMap<>();
Darwin.getFileManager().writeYaml(sampleData, this);
Darwin.getFileManager().writeYaml(sampleData, configFile);

// Read data from a file
Map<String, Object> readData;
readData = Darwin.getFileManager().getData(this);
readData = Darwin.getFileManager().getData(configFile);

// Get P2/Sponge objects
Location p2Loc = new Location("world", 1, 1, 1);
org.spongepowered.api.world.Location<World> spLoc = Darwin.getPlotManager().convertPlotsToSpongeLoc(p2Loc);
Plot p1 = Darwin.getPlotManager().getPlotFromP2Loc(p2Loc);
Plot p2 = Darwin.getPlotManager().getPlotFromSpongeLoc(spLoc);
```

### Forge Manager
The following example showcases several basic use cases of the Forge Manager.
```java
// Color and material wrappers
EnumDyeColorWrapper enumDyeColorWrapper = EnumDyeColorWrapper.YELLOW;
MapColorWrapper mapColorWrapper = MapColorWrapper.getBlockColor(enumDyeColorWrapper);
MaterialWrapper materialWrapper = new MaterialWrapper(mapColorWrapper);
BlockWrapper blockWrapper = new BlockWrapper(materialWrapper);

// Item(Stack) wrappers
int blockId = BlockWrapper.getIdFromBlock(blockWrapper);
ItemWrapper itemWrapper = Darwin.getForgeManager().getForgeItemById(blockId);
ItemStackWrapper itemStackWrapper = new ItemStackWrapper(itemWrapper, 1);
itemStackWrapper.setItemDamage(7);
```
