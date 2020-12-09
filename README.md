![image](https://user-images.githubusercontent.com/10957963/100515229-bc4f0b00-317a-11eb-8688-39d229eeada6.png)

[![License: LGPL v2.1](https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg)](https://www.gnu.org/licenses/lgpl-2.1)
![Build status](https://github.com/GuusLieben/Selene/workflows/Build%20status/badge.svg)  
![minecraft](https://img.shields.io/badge/Minecraft-1.12.2-green)
![sponge-api](https://img.shields.io/badge/SpongeAPI-7.2-green)  

**Project is in pre-alpha stages, please note there might be major changes without explicit notice**  
**First release candidate for Selene is expected to be available 2021.1 (Q1)**

```
A WIP branch to restructure DarwinCore/DarwinServer to a cleaner architecture under a new name.
Selene will primarily use Kotlin for utilities, with stricter stylechecks than present in earlier versions.
Selene currently targets Minecraft 1.12.2 and Sponge 7.x. Paper/Bukkit implementations are not yet supported.
```

# Project structure
The project uses both Kotlin and Java in a multi-module configuration. Each module contains Java sources at `/src/main/java/`
and Kotlin sources at `/src/main/kotlin/`. Resources in `/src/main/resources/` are shared across both sources. Tests
follow the same structure, though it is recommended to write tests for extensions in Java only. 

## Selene Core
`selene-core` is the base of the framework, containing the main APIs to work with when developing extensions.
It contains mostly interfaces and abstract types, and does not depend on a specific platform.

## Selene Common
`selene-common` contains default implementations of the APIs, which can be used across platforms. These are typically
invisible to extensions.

## Extensions
`extensions` is the base for platform-independent extensions. These are unaware of the platform they are being used
on, and only communicate with the framework directly.

## Sponge
`sponge` is the platform implementation for [Sponge](https://github.com/SpongePowered/SpongeForge/). It contains the
implementation of Selene for the platform, and defines which default implementations are used alongside Sponge-specific
implementations.  

## Sponge Extensions
`sponge-extensions` is the base for platform-specific extensions. These are aware of the platform used, and are
typically extensions designed to replace platform plugins which are not directly available on other platforms 
(like ptime).

# Usage
_Note: To use Selene, your project must be configured to use Java 8 or higher._

## Build distribution
Selene uses Maven to automate builds, performing several steps before and after a build has completed. To build, use
`mvn package`. Builds can then be found under `/dist/` in the base directory where you cloned Selene. Builds are
separated into different sub-directories based on the module that has been built. Usually you'll only need platform
specific builds, like Sponge which can be found in `/dist/sponge/`.

Two packaged builds exist for each build, `original` and `shaded`.  

- `original` contains _only_ Selene, and follows the
naming scheme `original-selene-{module}-{version}-{date}_{time}.jar`.  

- `shaded` contains required dependencies which are not already expected to be present on a platform, and follows the 
naming scheme `selene-{module}-{version}-{date}_{time}.jar`. Some dependencies may be excluded from the `shaded` build 
if they are expected to be on the platform, these dependencies differ per platform and are typically excluded in the 
`pom.xml` for the appropriate platform.

## Aggregated documentation
Documentation is typically pre-built in `/docs/` in this repository, and is available at 
[https://guuslieben.github.io/Selene/](https://guuslieben.github.io/Selene/). This contains the aggregated JavaDocs for
all sources within Selene. To generate these JavaDocs yourself, use `mvn javadoc:aggregate`.

## Development Server
Selene contains pre-made servers for its supported platforms, with automatic build distributions towards those platforms.
To get started, download [servers.zip](http://darwin.dockbox.org/stor/servers.zip). After downloading, extract the folder into the root directory of your project.

To run the server, we recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/). 

### Sponge 1.12
Create a configuration to activate a JAR Application as follows:
- Go to Run > Edit Configurations
- Add a new configuration (Alt+Insert on Windows)
- Select 'JAR Application'
- For 'Path to JAR' select `${your working directory}\servers\sponge-112\forge-1.12.2-2709.jar`
- For 'VM Options' enter your preferred flags, it is recommended to run the development server with at least 3GB of allocatable memory (`-Xmx3G`)
- For 'Working directory' select `${your working directory}\servers\sponge-112\`
- Leave the JRE and classpath settings as default
- Add Maven Goal `-DskipTests=true clean install` to run 'Before launch'

![image](https://user-images.githubusercontent.com/10957963/101648084-c1913d00-3a39-11eb-9d90-37c8ef7cdd69.png)
