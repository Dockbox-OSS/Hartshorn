# Contributing to Hartshorn

Thank you for considering contributing to Hartshorn. We welcome your bug reports, feature requests,
enhancements, and code contributions.

## Issues

### Reporting Bugs

If you encounter a bug in Hartshorn, please follow these steps to report it:

1. Check the existing issues to ensure the bug hasn't already been reported.
2. [Create a new issue](https://github.com/Dockbox-OSS/Hartshorn/issues/new?assignees=&labels=type%3A+bug&projects=&template=bug_report.yml),
   describing the bug with clear and concise details.
3. Include steps to reproduce the bug, the expected behavior, and the actual behavior.
4. If applicable, provide screenshots, error messages, and your environment details (OS, Java
   version, etc.).

### Requesting New Features

If you have a new feature in mind, please follow these steps:

1. Check the existing issues to ensure the feature hasn't already been requested.
2. [Create a new issue](https://github.com/Dockbox-OSS/Hartshorn/issues/new?assignees=&labels=type%3A+feature+request&projects=&template=feature_request.yml),
   describing the feature you'd like to see, including its use case and expected behavior.
3. Provide any additional context or information that might help in understanding the request.

### Requesting Enhancements to Existing Features

If you have suggestions to enhance existing features, please follow these steps:

1. Check the existing issues to ensure the enhancement hasn't already been requested.
2. [Create a new issue](https://github.com/Dockbox-OSS/Hartshorn/issues/new?assignees=&labels=type%3A+enhancement&projects=&template=enhancement.yml),
   describing the current behavior, the proposed enhancements, and expected behavior.
3. Include a use case and any additional context that can help us understand your request.

## Git

### Branches

- We follow a version-based branching model for development and releases, and a feature-based
  branching model for specific functionality.
- Feature branches should be named with the issue number and a brief description, like
  `feature/#123-sample-feature`.
- Access to development and release branches is limited to project maintainers.
- Bugfixes should use a `bugfix/` branch prefix.
- Chores should use a `chore/` branch prefix.
- New or enhanced functionality development should use a `feature/` branch prefix.

### Commits

- Always start commits with the related issue number, e.g., "#123".
- Provide a clear and concise message summarizing the purpose of the commit.
- Optionally, include a description for complex changes to enhance understanding.

### Pull Requests (PRs)

We welcome contributions in the form of pull requests. To submit a PR, always follow
the [Pull Request Template](https://github.com/Dockbox-OSS/Hartshorn/blob/develop/.github/PULL_REQUEST_TEMPLATE.md).

#### General notes

- Try to limit pull requests to a few commits which resolve a specific issue
- All proposed changes must be reviewed and approved by at least one organization member
- Describe the proposed changes with a relevant motivation and additional context
- Link to the original issue(s) which your changes relate to

## Testing Guidelines

- Use JUnit 5 for testing.
- Aim for at least 80% test coverage.
- Follow the Arrange-Act-Assert (AAA) testing pattern.
- Place test classes in the `test.{package}` package. For example, `org.dockbox.hartshorn` becomes
  `test.org.dockbox.hartshorn`.
- Name test classes using the `{Class}Tests` convention. For example, `HartshornTests` for the
  `Hartshorn` class.

## Code Style

Hartshorn uses the [Palantir Java Format](https://github.com/palantir/palantir-java-format), which is enforced by the
Spotless plugin. If you are using IntelliJ, you can install the 
[palantir-java-format](https://plugins.jetbrains.com/plugin/13180-palantir-java-format) plugin to automatically format 
your code.

## Scripting utilities
Hartshorn has a set of scripting utilities that can be used to automate common tasks. For example, 
[listing coverage results](hartshorn-assembly/ListCoverageResults.ps1) and 
[generating new modules](hartshorn-archetype/GenerateModule.ps1) (also see 'Setting up new modules' below). If your
contribution involves scripting, please ensure that the scripts use PowerShell and follow best practices for 
scripting in PowerShell. Bash, Python, and other scripting languages are not supported and will not be accepted.

## Documentation

We aim to provide comprehensive documentation for Hartshorn. Please ensure that your contributions
include relevant documentation updates. This includes updating Javadocs, Asciidoc documentation, and
other documentation files as necessary.

### Previewing documentation

When working with the Asciidoc documentation, you can build the documentation locally using Antora.

```shell
npm --prefix hartshorn-assembly/antora run antora -- playbook-local.yml
```

After building the documentation, you can view it in your browser by opening the [
`hartshorn-assembly/antora/target/site/index.html`](hartshorn-assembly/antora/target/site/index.html) file.

### Working with attachments

If your contribution includes attachments, please ensure that they are placed in the correct
location within the directory, following
the [standard file and directory set as defined by Antora](https://docs.antora.org/antora/latest/standard-directories/).
Attachments should be referenced by their module name (e.g. `image::inject::diagram.svg[]`) and not
by relative paths (e.g. `image::../images/diagram.svg[]`).

SVG attachments should be inlined where possible, to retain as much information as possible.
See [Options for SVG images](https://docs.asciidoctor.org/asciidoc/latest/macros/image-svg/#options-for-svg-images)
for more information.

```asciidoc
image::sample.svg[Sample description,100,opts=inline] // OK, embedded
image::sample.svg[Sample description,100] // Not OK, image is rasterized
```

# Setting up new modules

Hartshorn is made up of multiple modules, each with its own responsibilities. Over time, the build
configuration for Hartshorn has grown to support our needs. However, this also comes with a cost:
setting up a new module can be a daunting task. This guide aims to simplify the process of setting
up a new module.

## Preparing the Maven module

### Setting up the project

As Hartshorn uses Maven, we need to set up a new Maven module. To do so, you can use the 
[Hartshorn archetype generator](hartshorn-archetype/GenerateModule.ps1) to create a new module. This will set
up the module, and ensure it is registered in various places. For more information, refer to its 
[documentation](hartshorn-archetype/README.md).

### Choosing the right parent module

Hartshorn modules are divided into several parent modules, each adding a different set of support
for the module. Below is a list of the available parent modules and their purpose. Each module
inherits from the previous one, so you can choose the one that fits your needs.

- `hartshorn-build-support`: The base parent module, providing the necessary setup to build the
  module.
- `hartshorn-staging`: A parent module, adding only the necessary setup to publish the module to the
  Maven Central repository.
- `hartshorn-platform-updates`: A parent module, adding tooling to update the module's dependencies
  to the latest versions.
- `hartshorn-platform-support`: A parent module, adding codestyle and documentation checks to the
  module.

In most cases, new modules should inherit from `hartshorn-platform-support`. Only inherit from other
parents if you have a specific need for them. For example, BOM modules may inherit from
`hartshorn-platform-updates`, as there should not be any code in these modules.

### Additional notes

#### Test fixtures

Test fixtures are a common part of Hartshorn modules. These are used to provide test templates for
unit tests. If your module requires test fixtures, you should create a `test-fixtures` module. This
module should be a separate Maven module, inheriting from the same parent as the main module. This
module should be named `hartshorn-module-name-test-fixtures`.

Test fixtures should have sources in the `main` sourceset, rather than the `test` sourceset. This is
because test fixtures are not tests themselves, but rather templates for tests. This also means you
should promote the required test dependencies to the `compile` scope, rather than the `test` scope.

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>compile</scope>
</dependency>
```