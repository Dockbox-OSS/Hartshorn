<p align="center">
	<img alt="Hartshorn" src="./hartshorn-assembly/images/logo.png" height="175" />
	<h3 align="center">Hartshorn</h3>
	<p align="center">Agnostic service and dependency management framework.</p>
	<p align="center">
        <a href="https://www.codefactor.io/repository/github/guuslieben/hartshorn"><img src="https://www.codefactor.io/repository/github/guuslieben/hartshorn/badge?s=5e09ccbb31604049271c18af0d20c1237d9816f2" alt="CodeFactor" /></a>
		<a href="https://www.gnu.org/licenses/lgpl-2.1"><img src="https://img.shields.io/badge/license-LGPL%20v2.1-0CAB6B"></a><br>
        <img src="https://github.com/GuusLieben/Hartshorn/actions/workflows/hartshorn.yml/badge.svg"> <a href="https://deepsource.io/gh/GuusLieben/Hartshorn/?ref=repository-badge" target="_blank"><img alt="DeepSource" title="DeepSource" src="https://deepsource.io/gh/GuusLieben/Hartshorn.svg/?label=active+issues"/></a>
	</p>
</p>

Hartshorn is a platform agnostic plugin/extension framework providing advanced utilities to develop against a variety of platforms.

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
