# <img alt="Hartshorn" src="./hartshorn-assembly/images/logo.png" height="100" /> Hartshorn <img src="https://github.com/GuusLieben/Hartshorn/actions/workflows/hartshorn.yml/badge.svg"> <img src="https://camo.githubusercontent.com/ddcb65d081d4ded9548c254cb8b3b0fa4449ef53f384430732f5961cc685acea/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4a444b2d31362d6f72616e6765">  
[Hartshorn](https://hartshorn.dockbox.org/) is a modern JVM-based full stack Java framework. It is capable of aiding developers while building modular, testable, and scalable applications with support for Java and other JVM languages.  

Hartshorn is an umbrella term for the Hartshorn Framework and all official [modules](https://hartshorn.dockbox.org/modules/modules/). Hartshorn Framework itself is a service- and dependency management framework which complements [JSR 330](https://www.jcp.org/en/jsr/detail?id=330), but does not embrace the Java EE specification. You can read more about Hartshorn's core technologies and principles in the [core technologies](https://hartshorn.dockbox.org/core/cdi/) topic.  

Hartshorn aims to ease the creation and management of complex JVM applications, this is done by providing tools necessary to build these applications. These tools include:
- Context and Dependency Injection (CDI) and Inversion of Control (IoC)
- Automatic application configuration
- Component lifecycle management

### Philosophy
Hartshorn follows a range of technical [core principles](https://hartshorn.dockbox.org/core/cdi/), as well as a specific design philosophy. Here are the guiding principles of the Hartshorn Framework:
- Extensibility and flexibility. Hartshorn puts an emphasis on enabling developers to extend and switch between implementations at every level, without requiring changes to your code.
- Allow for diverse usages. Hartshorn allows for a large amount of flexibility, by providing a template for you to develop your application in.
- High standards for code quality. Hartshorn aims to use modern language features in combination with a meaningful and future-proof API to allow developers to intuitively use the framework.

### Why use Hartshorn?
Hartshorn is capable of being as complex or simplistic as you need it to be, remaining largely unbiased to allow you to build applications in the way you see fit. It aims to avoid some of the downsides of frameworks like Spring, Ktor, and Quarkus:
- Fast startup time
- Reduced memory footprint
- Full component customization
- Easy unit testing

<br>  

## Getting started
If you are just getting started with hartshorn, you'll want to view the [Getting Started](https://hartshorn.dockbox.org/getting-started/setup/) guides on the official documentation website. The provided guides use Hartshorn's application starter which enables you to get started quickly. If you are just looking for the Maven dependencies, these are listed below.

### Maven configuration
Each module has its own dedicated dependency, so you have the freedom to use only the modules you actually need.  
To get started, add the Maven dependency:
```xml
<dependency>
  <groupId>org.dockbox.hartshorn</groupId>
  <artifactId>hartshorn-core</artifactId>
  <version>${version}</version>
</dependency>
```
Or if you are using Gradle:
```groovy
implementation "org.dockbox.hartshorn:hartshorn-core:$version"
```

### Starting your first application
Getting started with Hartshorn is easy, the example below will introduce you to the application starter as well as showing you how to create a simple REST controller. You can find a full guide on this topic on the [Getting started section](https://hartshorn.dockbox.org/getting-started/first-application/) of the documentation website.  

Before creating a REST controller, we'll first need a domain object representing our response. In this example we will use a simple `Greeting` object, which contains a single message. This will result in the following response from the REST API:
```json
{
    "content": "Hello, World!"
}
```
To model the greeting, you can use a Plain Old Java Object (POJO), no annotations required.

```java
public class Greeting {

    private final String content;

    public Greeting(final String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
```
Next we'll create a REST controller capable of handling HTTP requests. In Hartshorn, REST controllers are a component stereotype, meaning they are automatically managed by the application. This allows you to simply mark the controller with `@RestController`, without needing to register the component manually.

```java

@RestController
public class GreetingController {

    @HttpGet("/greeting")
    public Greeting greeting(@RequestParam(value = "name", or = "World") final String name) {
        return new Greeting("Hello, %s!".formatted(name)); // If no value is provided for 'name', the default value 'World' will be used

    }
}
```
Finally, we start the application from our main application class. Here we can use the `HartshornApplication` starter, which will automatically perform all steps required to bootstrap your application and start the RESTful web service.

```java

@Activator
// Indicates this class is allowed to be used as an application starter. You can also define additional metadata here
@UseHttpServer // Indicates we should start a web server and process controllers
public class GreetingApplication {

    public static void main(final String[] args) {
        HartshornApplication.create(GreetingApplication.class, args);
    }
}
```
That's it! You can now navigate to http://localhost:8080/greeting, which will result in the following response:
```json
{
    "content": "Hello, World!"
}
```
Additionally, if you go to http://localhost:8080/greeting?name=YourName, the response will greet you instead of the entire world! For example, adding `?name=Hartshorn` will result in:
```json
{
    "content": "Hello, Hartshorn!"
}
```
  
<br>  
  
## Building Hartshorn
If you wish to build Hartshorn yourself, either to get access to pre-release versions, or to add customizations, the guide below explains how to set up your Gradle environment.  All platforms require a Java installation, with JDK 17 or more recent version.

Set the JAVA\_HOME environment variable. For example:

| Platform | Command                                              |
| :---: |------------------------------------------------------|
|  Unix    | ``export JAVA_HOME=/usr/lib/jvm/openjdk-17-jdk``     |
|  OSX     | ``export JAVA_HOME=`/usr/libexec/java_home -v 17` `` |
|  Windows | ``set JAVA_HOME="C:\Program Files\Java\jdk-17.0.3"`` |

Hartshorn uses a custom Gradle wrapper to automate builds, performing several steps before and after a build has completed.  
Depending on your IDE the Gradle wrapper may be automatically used. If you encounter any issues, use `./gradlew` for Unix systems or Git Bash and `gradlew.bat` for Windows systems in place of any 'gradle' command.  

Within the directory containing the unpacked source code, run the gradle build:
```bash
./gradlew build
```

Once the build completes, the project distribution archives will be installed at `/hartshorn-assembly/distributions` in the base directory. 
Builds are versioned by date and by commit hash, with the artifact following the format `$archivesBaseName-$commitHash-$date.jar`.

<br>  
  
## Contributing
Interested in contributing to Hartshorn, want to report a bug, or have a question? Before you do, please read the [contribution guidelines](https://hartshorn.dockbox.org/contributing/)
