<p align="center">
	<img alt="Kangaroo Logo" src="./selene-assembly/images/logo.png" height="175" />
	<h3 align="center">Selene</h3>
	<p align="center">Agnostic plugin/extension framework.</p>
	<p align="center">
		<a href="https://guuslieben.github.io/Selene/"><img src="https://github.com/GuusLieben/Selene/workflows/JavaDocs/badge.svg"></a>
		<img src="https://github.com/GuusLieben/Selene/workflows/Build/badge.svg">
		<img src="https://github.com/GuusLieben/Selene/workflows/Tests/badge.svg">
		<a href="https://www.gnu.org/licenses/lgpl-2.1"><img src="https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg"></a>
	</p>
</p>

Selene is a platform agnostic plugin/extension framework providing advanced utilities to develop against a variety of platforms.

## Usage
_Note: To use Selene, your project must be configured to use Java 8 or higher._  
See [About](https://github.com/GuusLieben/Selene/wiki) and [Gradle](https://github.com/GuusLieben/Selene/wiki/Gradle) on the wiki
for additional information about the topics below.

### Build distribution
Selene uses a custom Gradle wrapper to automate builds, performing several steps before and after a build has completed.  
Depending on your IDE the Gradle wrapper may be automatically used. If you encounter any issues, use `./gradlew` for Unix systems or Git Bash and `gradlew.bat` for Windows systems in place of any 'gradle' command.  

Use `gradle build` to build all Selene modules, build artifacts can then be found under `/dist/` in the base directory 
where you cloned Selene. Builds are versioned by date and by commit hash, with the artifact following the format `$archivesBaseName-$commitHash-$date.jar`.

### Aggregated documentation
Documentation is typically pre-built in `/docs/` in this repository, and is available at 
[https://guuslieben.github.io/Selene/](https://guuslieben.github.io/Selene/). This contains the aggregated JavaDocs for
all sources within Selene. To generate these JavaDocs yourself, use `gradle aggregatedJavadocs`.

### Development Server
Selene contains pre-made servers for its supported platforms, with automatic build distributions towards those platforms. 
These servers can be generated using the appropriate `downloadDevServer` task for each relevant platform module. 
This automatically downloads the appropriate server files from their respective authors/approved CDNs, see [this PR](https://github.com/GuusLieben/Selene/pull/214) for more details.

To run the server, we recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/). 
Each relevant platform module will have a `runDevServer` task which prepares and executes the development server for that platform.
The `runDevServer` exposes a debugging socket on port 5005. Using IntelliJ IDEA you can manually attach your debugger once the task starts
(the IDE will notify you of this). To attach automatically, we recommend the use of [AttachMe](https://plugins.jetbrains.com/plugin/13263-attachme).
