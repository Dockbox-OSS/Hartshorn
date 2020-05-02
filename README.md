# Darwin Server ![ci-status](https://api.travis-ci.com/GuusLieben/DarwinServer.svg?branch=api-7.1-1.12.2)  
**Project is in pre-alpha stages, please note there might be massive changes without special notice**

## Project: API
All common code is collected here and provided to modules, this includes File management, translations, permissions etc.
Also contains the Sponge plugin (com.darwinreforged.server.core.init.DarwinServer) which loads and handles all modules.

## Project: MCP
Provided safe wrappers for required MCP objects, to prevent other projects from requiring them and potentially having game-breaking dependency issues.

## Project: Modules
All modules are stored here, these are (will be) documented accordingly.

## How to build
```.\gradlew build -p modules```

The built file (`modules-{version}.jar`) can then be found under `./modules/build/libs`.

It is possible to build separate modules using their respective `build.gradle` files.
