# Contributing to Hartshorn
## How can I contribute?
### Reporting Bugs
_[Template](https://github.com/Dockbox-OSS/Hartshorn/blob/develop/.github/ISSUE_TEMPLATE/bug_report.yml)_  
If you run into an issue, please use our [issue tracker](https://github.com/Dockbox-OSS/Hartshorn/issues) _first_ to ensure
the issue hasn't been reported before. Open a new issue only if you haven't found anything similar to your issue.

You can open a new issue and search existing issues using the public [issue tracker](https://github.com/Dockbox-OSS/Hartshorn/issues).

**When opening a new issue, please include the following information in the issue:**
- What Hartshorn version you are using
- What platform(s) you have confirmed the issue on
- Which JDK version you are using
- Describe the issue you are experiencing
- Describe how to reproduce the issue
- Describe what you expect the correct behavior to be like
- Include any warnings, errors, stacktraces if applicable

When a new issue is opened, be prepared to work with the developers investigating your issue. It is not uncommon additional
information is requested, and your assistance is crucial in providing a quick solution.  
In general, the more detail you share about your issue the quicker it can be resolved. For example, providing a simple
test case is exceptionally helpful.

### Requesting features
_[Template](https://github.com/Dockbox-OSS/Hartshorn/blob/develop/.github/ISSUE_TEMPLATE/feature.yml)_    
Hartshorn is under active development. The team's primary focus is on fixing known issues, and adding compelling new features.  
You can view the list of proposed features by filtering the issue tracker by the ["type: feature request" label](https://github.com/Dockbox-OSS/Hartshorn/issues?q=is%3Aopen+is%3Aissue+label%3A%22type%3A+feature+request%22).
If you have an idea for a feature first check this list. If your idea already appears then add a +1 to the top most comment.

Otherwise, open a new issue and describe your proposed feature. Is it related to a problem? Why is it needed? What are possible alternatives?

### Pull Requests
Pull Requests always follow the [Pull Request Template](https://github.com/Dockbox-OSS/Hartshorn/blob/develop/.github/PULL_REQUEST_TEMPLATE.md).

#### General
- Try to limit pull requests to a few commits which resolve a specific issue
- Make sure commit messages are descriptive of the changes made
- All proposed changes must be reviewed and approved by at least one organization member
- Describe the proposed changes with a relevant motivation and additional context
- Link to the original issue(s) which your changes relate to

#### Additional information
- Indicate what type of changes your proposal makes
- Indicate if your proposed changes contain breaking changes
- Indicate if your proposed changes requires additional documentation (apart from JavaDocs)

#### Testing
- Every pull request will be tested by GitHub workflows
- Test cases should be provided when appropriate. This includes making sure new features have adequate code coverage
- If your proposed changes were tested using practical use-cases (run testing) describe your test configuration
- The pull request must pass all GitHub workflow checks before being accepted

### Testing
#### General
- Tests cover relevant use-cases
- The target coverage for all tests is 80%
- Tests are performed using the source JDK version. CI will test against all supported JDK versions

#### Unit Testing
- Tests are located in `src/test/java`
- Test packages are equal to the package of the target class, prefixed by `test` (e.g. `test.org.dockbox.hartshorn.core`)
- Test classes follow the naming convention `${TestedClass}Tests`
- Tests follow the [AAA pattern](https://java-design-patterns.com/patterns/arrange-act-assert/)
- Tests use JUnit 5 (`org.junit.jupiter.api`)

For example, `org.dockbox.hartshorn.common.ClassX` is tested in `test.org.dockbox.hartshorn.common.ClassXTests`

## Style Guides
### Repository Structure
Hartshorn uses a standardised branching structure. The `develop` branch is the branch all development should be based on.

#### Branch names
- Feature branches should be named `feature/${issue-number}-${feature-name}`
- Bugfix branches should be named `bugfix/${issue-number}-${bugfix-name}`
- Release branches should be named `release/${version}`
- Hotfix branches should be named `hotfix/${version}`
- Documentation branches should be named `docs/${issue-number}-${documentation-topic}`
- Maintenance/chore branches should be named `chore/${issue-number}-${maintenance-topic}`
- Miscellaneous branches should be named `task/${issue-number}-${task-topic}`

### Coding Conventions
#### Code Style
You can find the style configuration in the repository, under [`codeStyle`](https://github.com/Dockbox-OSS/Hartshorn/tree/develop/codeStyle/).
We currently use a modified version of [Jetbrains' Java Style](https://www.jetbrains.com/help/idea/code-style-java.html) for our coding convention.
The changes made to the default style can be seen in the table below.

| Rule                                                                                                                  | Motivation                                                                                                                         |
|-----------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| Align multiline annotation parameters to the first parameter                                                          | This makes it easier to read and maintain complex annotations                                                                      |
| Insert inner class imports                                                                                            | Code reviews are easier when imports are not hidden                                                                                |
| Never use wildcard imports for classes and names                                                                      | Code reviews are easier when imports are not hidden                                                                                |
| Do not align @param and @throws tags                                                                                  | Tags should be individually aligned, so if other paraters are added/removed the alignment is not affected                          |
| Add blank line after @param tags                                                                                      | There should be a clear separation between input and output details                                                                |
| Do not keep invalid JavaDoc tags                                                                                      | Invalid tags should be removed, as they are not used by the JavaDoc tool                                                           |
| Do not wrap single line comments                                                                                      | Single line comments should not be wrapped, as they are not intended to be read as a paragraph                                     |
| Indent on continuation                                                                                                | Continuation lines should be indented, as they are part of the same statement                                                      |
| `else`, `while`, `catch`, and `finally` on new line compared to closing brace                                         | This makes Git blaming easier, as this allows adding/removing bodies without affecting the closing brace of the previous statement |
| Space within array initializer braces (e.g. `new int[] { 1, 2, 3 }`)                                                  | This makes it easier to read and maintain complex array initializers                                                               |
| Keep single classes and lambdas on one line if possible (e.g. `() -> { }` and `public static class InnerClass() { }`) | This wastes less space for simple inline classes and lambdas                                                                       |
| Always wrap enum constants on new line                                                                                | This makes Git blaming easier, as this allows adding/removing constants without affecting the previous constant                    |

### Code inspections
Hartshorn uses a number of code inspections to ensure code quality. These inspections are configured for IntelliJ IDEA, and can be found in the repository, under [`codeStyle`](https://github.com/Dockbox-OSS/Hartshorn/tree/develop/codeStyle/).

| Inspection                             | Enabled  | Motivation                                                                                                                      |
|----------------------------------------|----------|---------------------------------------------------------------------------------------------------------------------------------|
| Assignment to lambda parameter         | Enabled  | This is a common mistake, and should be avoided.                                                                                |
| AutoCloseable resource                 | Disabled | The `ApplicationContext` is a commonly used resource, and should not be closed.                                                 |
| Class references sub-class             | Enabled  | This is a common mistake, and should be avoided. Hartshorn offers several APIs, which should not expose implementation details. |
| String literal may be equals qualifier | Enabled  | Prevents preventable NPEs by swapping x.equals("string") to "string".equals(x)                                                  |
| Variable or parameter can be final     | Enabled  | Clarify that a local variable or parameter will not be re-assigned.                                                             |
| Missorted modifiers                    | Enabled  | This is a common mistake, and should be avoided.                                                                                |
| Redundant RegEx character escape       | Disabled | Sometimes it can be useful to be more explicit in a regular expression. This may be overruled during code reviews.              |
| Annotation may be simplified           | Enabled  | When an annotation only has a `value` attribute, you should not add `value=X` as this is inferred by the compiler.              |
| Size replaceable with isEmpty          | Enabled  | Using `isEmpty` is often clearer than using `size() > 0`                                                                        |
| Use of System.out or System.err        | Enabled  | Logging should be used instead of System.out and System.err.                                                                    |
| Use of Throwable.printStackTrace       | Enabled  | Logging or internal error handling should be used instead of Throwable.printStackTrace.                                         |
| Type may be weakened                   | Enabled  | For exposed APIs it can be useful to support the lowest possible type so more implementations can be used                       |
| Unnecessary continue or default        | Disabled | Sometimes it can be useful to be more explicit in a switch, for, or if statement. This may be overruled during code reviews.    |
| Unqualified method or field access     | Enabled  | Being more explicit when calling methods or accessing fields is often clearer.                                                  |
| Unused return value                    | Disabled | Some methods may not be used internally, but are exposed for external use.                                                      |