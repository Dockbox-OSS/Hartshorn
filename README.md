<img alt="Hartshorn" src="./hartshorn-assembly/images/hartshorn-banner.png" style="max-height: 125px" />
<p><img src="https://img.shields.io/badge/JDK%20source-17-white"> <img src="https://img.shields.io/badge/JDK%20target-19-white"> <img src="https://github.com/GuusLieben/Hartshorn/actions/workflows/hartshorn.yml/badge.svg"></p>

[Hartshorn](https://hartshorn.dockbox.org/) is a cutting-edge Java framework built on the JVM platform that offers comprehensive support for modular, scalable, and testable application development using Java and other JVM-based languages. Its main objective is to simplify the creation and administration of intricate JVM applications by providing developers with the necessary tools. To learn more about Hartshorn's fundamental technologies and principles, you can refer to the dedicated topic on [core technologies](https://hartshorn.dockbox.org/core/cdi/).

## Getting started

If you are new to Hartshorn, the official documentation website has a [Getting Started](https://hartshorn.dockbox.org/getting-started/setup/) section that provides comprehensive guides to help you get started quickly. The guides use Hartshorn's application starter to facilitate your initial setup. Additionally, if you only need the Maven dependencies for your project, they are listed below for your convenience.

### Maven configuration

The framework provides the flexibility to selectively utilize the required modules by including their dedicated dependencies. You can access all the modules and their corresponding releases on [Maven Central](https://central.sonatype.dev/namespace/org.dockbox.hartshorn).

To begin, add the following Maven dependency:

```xml
<dependency>
  <groupId>org.dockbox.hartshorn</groupId>
  <artifactId>hartshorn-core</artifactId>
  <version>${version}</version>
</dependency>
```

If you are using Gradle, use this implementation:

```groovy
implementation "org.dockbox.hartshorn:hartshorn-core:$version"
```

### Starting your first application

The process of starting with Hartshorn is straightforward, and the example below will help you understand the basic steps of setting up and executing your first application.

The `HartshornApplication` class is used to build Hartshorn applications. It serves as the entry point of your application and performs the bootstrapping process. The process initiates a self-contained `ApplicationContext` that manages your application's lifecycle.

```java
public static void main(String[] args) {
    ApplicationContext applicationContext = HartshornApplication.create();
    // ...
}
```

`ApplicationContext` is at the heart of Hartshorn, and its primary responsibility is to manage your application's lifecycle. It also manages your application's dependency injection container, which enables the injection of dependencies. The `@Binds` annotation is used to declare dependencies, which binds a type to its implementation. To inject dependencies, you can use the @Inject annotation.

```java
@Binds
public String helloWorld() {
    return "Hello World!";
}
```

Managed components are where injections take place. Components annotated with `@Component` or a stereotype annotation annotated with `@Component`, such as `@Service`, are considered managed.

```java
@Service
public class SampleService {
    
    @Inject
    private String helloWorld;
    
    public void sayHelloWorld() {
        System.out.println(helloWorld);
    }
}
```

Finally, we can bring everything together and print "Hello World!" to the console using our `SampleService`.

```java
public static void main(String[] args) {
    ApplicationContext applicationContext = HartshornApplication.create();
    SampleService sampleService = applicationContext.get(SampleService.class);
    sampleService.sayHelloWorld();
}
```

### Next steps

Once you've taken your first steps with Hartshorn, it's essential to expand your knowledge of the framework. The [documentation](https://hartshorn.dockbox.org/) is an excellent starting point that will help you become more familiar with the framework. Additionally, you can explore the [examples repository](https://github.com/Dockbox-OSS/Hartshorn-Examples) for more comprehensive examples.

## Building Hartshorn

If you want to build Hartshorn yourself, either to access pre-release versions or to customize the framework, the guide below explains how to build usable JAR artifacts.

Please note that you will need a Java installation with JDK 17 or a more recent version for all platforms.

Hartshorn uses Gradle to automate builds, performing several steps before and after a build has completed. Depending on your IDE, the Gradle wrapper may be automatically used. If you encounter any issues, use `./gradlew` for Unix systems or `gradlew.bat` for Windows systems instead of any `gradle` command.

To build all Hartshorn modules at once, run `gradle build`. To build specific modules, run `gradle :hartshorn-$module:build`.

Once the build is complete, the project distribution archives will be installed at `/hartshorn-assembly/distributions` in the base directory. Builds are versioned by release versions, with the artifact following the format `hartshorn-$module-$version.jar`. This will also generate appropriate `javadoc` and `sources` artifacts.

## Contributing

Looking to get involved with Hartshorn? We would love to have you on board! Whether you want to report a bug, have a question, or even contribute code, your help is greatly appreciated. Before you start, please take a moment to read through our [contribution guidelines](https://hartshorn.dockbox.org/contributing/) to ensure that your efforts align with our community standards and development practices.

At Dockbox, we value our community of contributors and strive to maintain an open and collaborative environment. Our project is powered by the contributions of individuals like you, who are passionate about building high-quality software and sharing their knowledge with others.

If you're looking for ways to contribute, we have plenty of opportunities available. You can help us by reporting bugs, reviewing code, writing documentation, or even contributing your own code changes. No contribution is too small, and we welcome all levels of experience.

We also welcome QA testers who try out Hartshorn in their own projects to see what works and what doesn't. Your feedback can help us improve the quality of the framework and make it even more valuable for our users.
