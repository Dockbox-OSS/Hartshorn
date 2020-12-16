![image](https://user-images.githubusercontent.com/10957963/100515229-bc4f0b00-317a-11eb-8688-39d229eeada6.png)

[![License: LGPL v2.1](https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg)](https://www.gnu.org/licenses/lgpl-2.1)
![Build status](https://github.com/GuusLieben/Selene/workflows/Build%20status/badge.svg)

# Usage
_Note: To use Selene, your project must be configured to use Java 8 or higher._  
See [About](https://github.com/GuusLieben/Selene/wiki) and [Maven](https://github.com/GuusLieben/Selene/wiki/Maven) on the wiki
for additional information about the topics below.

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
Selene contains pre-made servers for its supported platforms, with automatic build distributions towards those platforms. These servers are included in the `servers` submodule. Depending on your Git client these may not be cloned directly, to ensure their availability use `git clone --recurse-submodules https://github.com/GuusLieben/Selene.git`.

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
