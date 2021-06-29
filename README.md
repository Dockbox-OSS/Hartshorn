<p align="center">
	<img alt="Hartshorn" src="./hartshorn-assembly/images/logo.png" height="175" />
	<h3 align="center">Hartshorn</h3>
	<p align="center">Agnostic plugin/extension framework.</p>
	<p align="center">
        <a href="https://www.codefactor.io/repository/github/guuslieben/hartshorn"><img src="https://www.codefactor.io/repository/github/guuslieben/hartshorn/badge?s=5e09ccbb31604049271c18af0d20c1237d9816f2" alt="CodeFactor" /></a>
		<a href="https://www.gnu.org/licenses/lgpl-2.1"><img src="https://img.shields.io/badge/license-LGPL%20v2.1-0CAB6B"></a><br>
        <img src="https://github.com/GuusLieben/Hartshorn/actions/workflows/hartshorn.yml/badge.svg">
	</p>
</p>

Hartshorn is a platform agnostic plugin/extension framework providing advanced utilities to develop against a variety of platforms.

## Usage
_Note: To use Hartshorn, your project must be configured to use Java 16 or higher._  
See [About](https://github.com/GuusLieben/Hartshorn/wiki) and [Gradle](https://github.com/GuusLieben/Hartshorn/wiki/Gradle) on the wiki
for additional information about the topics below.

## Building
All platforms require a Java installation, with JDK 16 or more recent version.

Set the JAVA\_HOME environment variable. For example:

| Platform | Command |
| :---: | --- |
|  Unix    | ``export JAVA_HOME=/usr/lib/jvm/openjdk-16-jdk``            |
|  OSX     | ``export JAVA_HOME=`/usr/libexec/java_home -v 16` ``  |
|  Windows | ``set JAVA_HOME="C:\Program Files\Java\jdk-16.0.1"`` |

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

## Contributors
**Developers**  
<img src="https://avatars.githubusercontent.com/u/10957963?v=4" width="30px;" alt="" title="Guus Lieben" /> <img src="https://avatars.githubusercontent.com/u/38820160?v=4" width="30px;" alt="" title="Pumbas600" /> <img src="https://avatars.githubusercontent.com/u/36117510?v=4" width="30px;" alt="" title="Simon Bolduc" />  
**Translators**  
<img src="https://user-images.githubusercontent.com/10957963/122446285-c9123f80-cfa2-11eb-9e98-9b683af18147.png" width="30px;" alt="" title="Olik1911" /> <img src="https://user-images.githubusercontent.com/10957963/122446259-c0216e00-cfa2-11eb-8c85-a2a13c401c43.png" width="30px;" alt="" title="Anrir" /> <img src="https://user-images.githubusercontent.com/10957963/122446232-b861c980-cfa2-11eb-9102-ac4847c4dbf5.png" width="30px;" alt="" title="Niki" /> <img src="https://user-images.githubusercontent.com/10957963/122446212-b0a22500-cfa2-11eb-9b10-c365de4c9724.png" width="30px;" alt="" title="Igor" /> <img src="https://user-images.githubusercontent.com/10957963/120807203-fb09b780-c547-11eb-9d3e-3c29b040a878.png" width="30px;" alt="" title="DovahTheExplorer" /> <img src="https://user-images.githubusercontent.com/10957963/122446189-a97b1700-cfa2-11eb-9af7-aa4f04dfef1f.png" width="30px;" alt="" title="KongTheMonkey" /> <img src="https://user-images.githubusercontent.com/10957963/122446159-a2540900-cfa2-11eb-8786-fefab68a7e27.png" width="30px;" alt="" title="Slolo" /> <img src="https://user-images.githubusercontent.com/10957963/122445991-7173d400-cfa2-11eb-97f2-6491cec5036a.png" width="30px;" alt="" title="Asraya" /> <img src="https://user-images.githubusercontent.com/10957963/122446019-7cc6ff80-cfa2-11eb-8350-9dfd40aad7ab.png" width="30px;" alt="" title="Kleback" /> <img src="https://user-images.githubusercontent.com/10957963/122446067-88b2c180-cfa2-11eb-9180-2d87853c7423.png" width="30px;" alt="" title="Sekeleton" /> <img src="https://www.cumbria.ac.uk/media/staff-profile-images/staff_profile_-generic_350x350px.png" width="30px;" alt="" title="Dork" /> <img src="https://user-images.githubusercontent.com/10957963/122446110-9405ed00-cfa2-11eb-821c-ae391101ebcd.png" width="30px;" alt="" title="Overlord" /> <img src="https://user-images.githubusercontent.com/10957963/122446129-9a946480-cfa2-11eb-9be0-b87c301e11ce.png" width="30px;" alt="" title="Salt" />
