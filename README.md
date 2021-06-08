<p align="center">
	<img alt="Kangaroo Logo" src="./selene-assembly/images/logo.png" height="175" />
	<h3 align="center">Selene</h3>
	<p align="center">Agnostic plugin/extension framework.</p>
	<p align="center">
		<a href="https://guuslieben.github.io/Selene/"><img src="https://github.com/GuusLieben/Selene/workflows/JavaDocs/badge.svg"></a>
		<img src="https://github.com/GuusLieben/Selene/workflows/Build/badge.svg">
		<img src="https://github.com/GuusLieben/Selene/workflows/Tests/badge.svg"><br>
        <a href="https://www.codefactor.io/repository/github/guuslieben/selene"><img src="https://www.codefactor.io/repository/github/guuslieben/selene/badge?s=4dbef3a95ba6db638d3a86f7ffd5ff08eabdfcf4" alt="CodeFactor" /></a>
		<a href="https://www.gnu.org/licenses/lgpl-2.1"><img src="https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg"></a>
	</p>
</p>

Selene is a platform agnostic plugin/extension framework providing advanced utilities to develop against a variety of platforms.

## Usage
_Note: To use Selene, your project must be configured to use Java 8 or higher._  
See [About](https://github.com/GuusLieben/Selene/wiki) and [Gradle](https://github.com/GuusLieben/Selene/wiki/Gradle) on the wiki
for additional information about the topics below.

## Building
All platforms require a Java installation, with JDK 1.8 or more recent version.

Set the JAVA\_HOME environment variable. For example:

| Platform | Command |
| :---: | --- |
|  Unix    | ``export JAVA_HOME=/usr/java/jdk1.8.0_121``            |
|  OSX     | ``export JAVA_HOME=`/usr/libexec/java_home -v 1.8` ``  |
|  Windows | ``set JAVA_HOME="C:\Program Files\Java\jdk1.8.0_121"`` |

Selene uses a custom Gradle wrapper to automate builds, performing several steps before and after a build has completed.  
Depending on your IDE the Gradle wrapper may be automatically used. If you encounter any issues, use `./gradlew` for Unix systems or Git Bash and `gradlew.bat` for Windows systems in place of any 'gradle' command.  

Within the directory containing the unpacked source code, run the gradle build:
```bash
./gradlew build
```

Once the build completes, the project distribution archives will be installed at `/selene-assembly/distributions` in the base directory. 
Builds are versioned by date and by commit hash, with the artifact following the format `$archivesBaseName-$commitHash-$date.jar`.

## Contributing
See [CONTRIBUTING.md](https://github.com/GuusLieben/Selene/blob/selene-main/CONTRIBUTING.md) for instructions on how to contribute to the project.

## Documentation
Documentation is typically pre-built in `/docs/` in this repository, and is available at 
[https://guuslieben.github.io/Selene/](https://guuslieben.github.io/Selene/). This contains the aggregated JavaDocs for
all sources within Selene. To generate these JavaDocs yourself, use `gradle aggregatedJavadocs`.  
Additionally, the [wiki](https://github.com/GuusLieben/Selene/wiki) contains documentation for specific core, utility, and platform APIs.

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

For example, `org.dockbox.selene.common.ClassX` is tested in `org.dockbox.selene.common.ClassXTests`

### Run Testing
- Tests are performed using the [predefined Selene Servers](https://github.com/GuusLieben/Selene-Servers)
- Tests are performed against the latest (supported) version of relevant platforms
- Servers are activated using the [Selene development server configurations](https://github.com/GuusLieben/Selene/wiki/Gradle#development-server)

### Embedded server
Selene offers a embedded server which can be used when testing higher level components. To apply the embedded server to your tests, annotate your class as follows:
```java
@ExtendWith(SeleneJUnit5Runner.class)
public class ComponentTests {
    @Test
    public void testHighLevelComponent() {...}
}
```

## Development Server
Selene contains pre-made servers for its supported platforms, with automatic build distributions towards those platforms. 
These servers can be generated using the appropriate download task for each relevant platform module.
```bash
./gradlew downloadDevServer
```
This automatically downloads the appropriate server files from their respective authors/approved CDNs, see [this PR](https://github.com/GuusLieben/Selene/pull/214) for more details.

To run the server, we recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/). 
Each relevant platform module will have a server run task which prepares and executes the development server for that platform.
The server run task exposes a debugging socket on port 5005. Using IntelliJ IDEA you can manually attach your debugger once the task starts
(the IDE will notify you of this). To attach automatically, we recommend the use of [AttachMe](https://plugins.jetbrains.com/plugin/13263-attachme).
```bash
./gradlew runDevServer
```

## Contributors
<table>
  <tr>
    <td align="center"><img src="https://avatars.githubusercontent.com/u/10957963?v=4" width="100px;" alt=""/><br /><sub><b>Guus Lieben</b></sub><br /><sup>Lead developer</sup></td>
    <td align="center"><img src="https://avatars.githubusercontent.com/u/38820160?v=4" width="100px;" alt=""/><br /><sub><b>Pumbas600</b></sub><br /><sup>Developer</sup></td>
    <td align="center"><img src="https://avatars.githubusercontent.com/u/36117510?v=4" width="100px;" alt=""/><br /><sub><b>Simon Bolduc</b></sub><br /><sup>Developer</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/510472543782830087/00e6c38b98367e59b062f3680ead27c3.png" width="100px;" alt=""/><br /><sub><b>Olik1911</b></sub><br /><sup>Translator, Czech</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/537977071972909057/72b62aec1348be39cc91ccb805fb92dd.png?size=256" width="100px;" alt=""/><br /><sub><b>Anrir</b></sub><br /><sup>Translator, Russian</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/334440446874877982/3624260f35db4533bbf26eb0fe86d3bf.png?size=256" width="100px;" alt=""/><br /><sub><b>Niki</b></sub><br /><sup>Translator, Finnish</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/552210500939677720/84802723782e948ccbd8bb60cf0b31be.png?size=256" width="100px;" alt=""/><br /><sub><b>Igor</b></sub><br /><sup>Translator, Norwegian</sup></td>
  </tr>
  <tr>
    <td align="center"><img src="https://user-images.githubusercontent.com/10957963/120807203-fb09b780-c547-11eb-9d3e-3c29b040a878.png" width="100px;" alt=""/><br /><sub><b>DovahTheExplorer</b></sub><br /><sup>Translator, German</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/683656484726636553/a849bda97aba4801eeda07b8b44c4d58.png?size=256" width="100px;" alt=""/><br /><sub><b>KongTheMonkey</b></sub><br /><sup>Translator, German</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/415885019354234883/2bea4b8c2aaabed874da48580426077b.png?size=256" width="100px;" alt=""/><br /><sub><b>Slolo</b></sub><br /><sup>Translator, German</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/327405323562254336/72286ad9938c42a5c33c7bbe25d647b6.png?size=256" width="100px;" alt=""/><br /><sub><b>Asraya</b></sub><br /><sup>Translator, French</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/266193643281448960/d5cdfab3c0009837478748d4a8eb5d4d.png?size=256" width="100px;" alt=""/><br /><sub><b>Kleback</b></sub><br /><sup>Translator, Swedish</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/attachments/440964141184843777/850426706090655794/MMXIX.jpg" width="100px;" alt=""/><br /><sub><b>Sekeleton</b></sub><br /><sup>Translator, Turkish</sup></td>
    <td align="center"><img src="https://www.cumbria.ac.uk/media/staff-profile-images/staff_profile_-generic_350x350px.png" width="100px;" alt=""/><br /><sub><b>Dork</b></sub><br /><sup>Translator, Spanish</sup></td>
  </tr>
  <tr>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/416672229275992074/f8786cc6a4057fbc4032ec2dd90b8633.png" width="100px;" alt=""/><br /><sub><b>Overlord</b></sub><br /><sup>Translator, German (Switzerland)</sup></td>
    <td align="center"><img src="https://cdn.discordapp.com/avatars/573518212965072916/00945a45deb5db91dc50b6686fe30602.png" width="100px;" alt=""/><br /><sub><b>Salt</b></sub><br /><sup>Translator, Japanese</sup></td>
  </tr>
</table>
