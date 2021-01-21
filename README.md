[![License: LGPL v2.1](https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg)](https://www.gnu.org/licenses/lgpl-2.1)
![Build](https://github.com/GuusLieben/Selene/workflows/Build/badge.svg)
![Tests](https://github.com/GuusLieben/Selene/workflows/Tests/badge.svg)
![JavaDocs](https://github.com/GuusLieben/Selene/workflows/JavaDocs/badge.svg)  

![image](http://dockbox.org/content/Selene.png)

# Usage
_Note: To use Selene, your project must be configured to use Java 8 or higher._  
See [About](https://github.com/GuusLieben/Selene/wiki) and [Gradle](https://github.com/GuusLieben/Selene/wiki/Gradle) on the wiki
for additional information about the topics below.

## Build distribution
Selene uses Gradle **6.x** to automate builds, performing several steps before and after a build has completed.  
__Note:__ If you do not have [Gradle] installed then use ./gradlew for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.  
Use `gradle build` to build all Selene modules, build artifacts can then be found under `/dist/` in the base directory 
where you cloned Selene. Builds are versioned by date and by commit hash, with the artifact following the format `$archivesBaseName-$commitHash-$date.jar`.

## Aggregated documentation
Documentation is typically pre-built in `/docs/` in this repository, and is available at 
[https://guuslieben.github.io/Selene/](https://guuslieben.github.io/Selene/). This contains the aggregated JavaDocs for
all sources within Selene. To generate these JavaDocs yourself, use `gradle aggregatedJavadocs`.

## Development Server
Selene contains pre-made servers for its supported platforms, with automatic build distributions towards those platforms. 
These servers are included in the `servers` submodule. Depending on your Git client these may not be cloned directly, to ensure their availability use `git clone --recurse-submodules https://github.com/GuusLieben/Selene.git`.

To run the server, we recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/). 
Each relevant platform module will have a `runDevServer` task which prepares and executes the development server for that platform.
The `runDevServer` exposes a debugging socket on port 5005. Using IntelliJ IDEA you can manually attach your debugger once the task starts
(the IDE will notify you of this). To attach automatically, we recommend the use of [AttachMe](https://plugins.jetbrains.com/plugin/13263-attachme).
