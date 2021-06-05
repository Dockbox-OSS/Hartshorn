<p align="center">
	<img alt="Hartshorn" src="./hartshorn-assembly/images/logo.png" height="175" />
	<h3 align="center">Hartshorn</h3>
	<p align="center">Agnostic plugin/extension framework.</p>
	<p align="center">
		<a href="https://guuslieben.github.io/Hartshorn/"><img src="https://github.com/GuusLieben/Hartshorn/workflows/JavaDocs/badge.svg"></a>
		<img src="https://github.com/GuusLieben/Hartshorn/workflows/Build/badge.svg">
		<img src="https://github.com/GuusLieben/Hartshorn/workflows/Tests/badge.svg"><br>
        <a href="https://www.codefactor.io/repository/github/guuslieben/hartshorn"><img src="https://www.codefactor.io/repository/github/guuslieben/hartshorn/badge?s=4dbef3a95ba6db638d3a86f7ffd5ff08eabdfcf4" alt="CodeFactor" /></a>
		<a href="https://www.gnu.org/licenses/lgpl-2.1"><img src="https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg"></a>
	</p>
</p>

Hartshorn is a platform agnostic plugin/extension framework providing advanced utilities to develop against a variety of platforms.

## Usage
_Note: To use Hartshorn, your project must be configured to use Java 8 or higher._  
See [About](https://github.com/GuusLieben/Hartshorn/wiki) and [Gradle](https://github.com/GuusLieben/Hartshorn/wiki/Gradle) on the wiki
for additional information about the topics below.

## Building
All platforms require a Java installation, with JDK 1.8 or more recent version.

Set the JAVA\_HOME environment variable. For example:

| Platform | Command |
| :---: | --- |
|  Unix    | ``export JAVA_HOME=/usr/java/jdk1.8.0_121``            |
|  OSX     | ``export JAVA_HOME=`/usr/libexec/java_home -v 1.8` ``  |
|  Windows | ``set JAVA_HOME="C:\Program Files\Java\jdk1.8.0_121"`` |

Hartshorn uses a custom Gradle wrapper to automate builds, performing several steps before and after a build has completed.  
Depending on your IDE the Gradle wrapper may be automatically used. If you encounter any issues, use `./gradlew` for Unix systems or Git Bash and `gradlew.bat` for Windows systems in place of any 'gradle' command.  

Within the directory containing the unpacked source code, run the gradle build:
```bash
./gradlew build
```

Once the build completes, the project distribution archives will be installed at `/hartshorn-assembly/distributions` in the base directory. 
Builds are versioned by date and by commit hash, with the artifact following the format `$archivesBaseName-$commitHash-$date.jar`.

## Contributing
See [CONTRIBUTING.md](https://github.com/GuusLieben/Hartshorn/blob/hartshorn-main/CONTRIBUTING.md) for instructions on how to contribute to the project.

## Documentation
Documentation is typically pre-built in `/docs/` in this repository, and is available at 
[https://guuslieben.github.io/Hartshorn/](https://guuslieben.github.io/Hartshorn/). This contains the aggregated JavaDocs for
all sources within Hartshorn. To generate these JavaDocs yourself, use `gradle aggregatedJavadocs`.  
Additionally, the [wiki](https://github.com/GuusLieben/Hartshorn/wiki) contains documentation for specific core, utility, and platform APIs.

## Testing
### General
- Tests cover relevant use-cases
- The target coverage for all tests is 60%
- Tests are performed using [Java](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) 8

### Unit Testing
- Tests are located in `src/test/java`
- Test packages are equal to the package of the target class
- Test classes follow the naming convention `${TestedClass}Tests`
- Tests follow the [AAA pattern](https://medium.com/@pjbgf/title-testing-code-ocd-and-the-aaa-pattern-df453975ab80)
- Tests use JUnit 5 (`org.junit.jupiter.api`)

For example, `org.dockbox.hartshorn.common.ClassX` is tested in `org.dockbox.hartshorn.common.ClassXTests`

### Run Testing
- Tests are performed using the [predefined Hartshorn Servers](https://github.com/GuusLieben/Hartshorn-Servers)
- Tests are performed against the latest (supported) version of relevant platforms
- Servers are activated using the [Hartshorn development server configurations](https://github.com/GuusLieben/Hartshorn/wiki/Gradle#development-server)

### Embedded server
Hartshorn offers a embedded server which can be used when testing higher level components. To apply the embedded server to your tests, annotate your class as follows:
```java
@ExtendWith(HartshornJUnit5Runner.class)
public class ComponentTests {
    @Test
    public void testHighLevelComponent() {...}
}
```

## Development Server
Hartshorn contains pre-made servers for its supported platforms, with automatic build distributions towards those platforms. 
These servers can be generated using the appropriate download task for each relevant platform module.
```bash
./gradlew downloadDevServer
```
This automatically downloads the appropriate server files from their respective authors/approved CDNs, see [this PR](https://github.com/GuusLieben/Hartshorn/pull/214) for more details.

To run the server, we recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/). 
Each relevant platform module will have a server run task which prepares and executes the development server for that platform.
The server run task exposes a debugging socket on port 5005. Using IntelliJ IDEA you can manually attach your debugger once the task starts
(the IDE will notify you of this). To attach automatically, we recommend the use of [AttachMe](https://plugins.jetbrains.com/plugin/13263-attachme).
```bash
./gradlew runDevServer
```
