# Contributing to Hartshorn

Thank you for considering contributing to Hartshorn. We welcome your bug reports, feature requests, enhancements, and code contributions.

## Reporting Bugs

If you encounter a bug in Hartshorn, please follow these steps to report it:

1. Check the existing issues to ensure the bug hasn't already been reported.
2. [Create a new issue](https://github.com/Dockbox-OSS/Hartshorn/issues/new?assignees=&labels=type%3A+bug&projects=&template=bug_report.yml), describing the bug with clear and concise details.
3. Include steps to reproduce the bug, the expected behavior, and the actual behavior.
4. If applicable, provide screenshots, error messages, and your environment details (OS, Java version, etc.).

## Requesting New Features

If you have a new feature in mind, please follow these steps:

1. Check the existing issues to ensure the feature hasn't already been requested.
2. [Create a new issue](https://github.com/Dockbox-OSS/Hartshorn/issues/new?assignees=&labels=type%3A+feature+request&projects=&template=feature_request.yml), describing the feature you'd like to see, including its use case and expected behavior.
3. Provide any additional context or information that might help in understanding the request.

## Requesting Enhancements to Existing Features

If you have suggestions to enhance existing features, please follow these steps:

1. Check the existing issues to ensure the enhancement hasn't already been requested.
2. [Create a new issue](https://github.com/Dockbox-OSS/Hartshorn/issues/new?assignees=&labels=type%3A+enhancement&projects=&template=enhancement.yml), describing the current behavior, the proposed enhancements, and expected behavior.
3. Include a use case and any additional context that can help us understand your request.

## Pull Requests (PRs)

We welcome contributions in the form of pull requests. To submit a PR, always follow the [Pull Request Template](https://github.com/Dockbox-OSS/Hartshorn/blob/develop/.github/PULL_REQUEST_TEMPLATE.md).

### General notes
- Try to limit pull requests to a few commits which resolve a specific issue
- Make sure commit messages are descriptive of the changes made
- All proposed changes must be reviewed and approved by at least one organization member
- Describe the proposed changes with a relevant motivation and additional context
- Link to the original issue(s) which your changes relate to

## Testing Guidelines

- Use JUnit 5 for testing.
- Aim for at least 80% test coverage.
- Follow the Arrange-Act-Assert (AAA) testing pattern.
- Place test classes in the `test.{originalpackage}` package. For example, `org.dockbox.hartshorn` becomes `test.org.dockbox.hartshorn`.
- Name test classes using the `{TestedClass}Tests` convention. For example, `HartshornTests` for the `Hartshorn` class.

## Code Style

We maintain a consistent code style to ensure readability and maintainability. Our code style is based on the [Square code style](https://github.com/square/java-code-styles), with a few minor changes. Please follow the code style guidelines below when contributing to Hartshorn.

| Rule                                             | Motivation                                       |
|--------------------------------------------------|--------------------------------------------------|
| Indent with 4 spaces                             | Consistency with the existing codebase.          |
| Hard wrap at 140 columns                         | The default 100 column limit is too restrictive. |
| Place `else`, `catch`, etc. on new lines         | Improved readability and Git blaming             |
| Use braces around loops (`for`, `while`)         | Improved readability and Git blaming             |
| Always wrap enum constants                       | Improved readability and Git blaming             |
| Wrap annotations on classes, methods, and fields | Improved readability and Git blaming             |

## Code Inspections

We encourage the use of code inspections to maintain code quality. Please ensure the following inspections are enabled:

| Inspection                                     | Enabled | Motivation                                                                                                |
|------------------------------------------------|---------|-----------------------------------------------------------------------------------------------------------|
| Warn about use of 'System.out' or 'System.err' | Yes     | Prevent accidental use of `System.out` and `System.err`. Encourage the use of logging instead.            |
| Warn when a type may be weakened               | Yes     | Encourage APIs to support as many implementations as possible.                                            | 
| AutoCloseable used without try-with-resources  | No      | `ApplicationContext` is `AutoCloseable` but should not be used with try-with-resources in most scenarios. |
| String literal may be equals qualifier         | Yes     | Prevents preventable NPEs by swapping x.equals("string") to "string".equals(x)                            |
| Missorted modifiers                            | Yes     | This is a common mistake, and should be avoided.                                                          |
| Unqualified method or field access             | Yes     | Being more explicit when calling methods or accessing fields is often clearer.                            |
| Unused return value                            | No      | Some methods may not be used internally, but are exposed for external use.                                |