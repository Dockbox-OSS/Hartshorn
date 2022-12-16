# Contributing to Hartshorn
## How can I contribute?
### Reporting Bugs
_[Template](https://github.com/Dockbox-OSS/Hartshorn/blob/hartshorn-main/.github/ISSUE_TEMPLATE/bug_report.md)_  
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
_[Template](https://github.com/Dockbox-OSS/Hartshorn/blob/main/.github/ISSUE_TEMPLATE/feature.yml)_    
Hartshorn is under active development. The team's primary focus is on fixing known issues, and adding compelling new features.  
You can view the list of proposed features by filtering the issue tracker by the ["type: feature request" label](https://github.com/Dockbox-OSS/Hartshorn/issues?q=is%3Aopen+is%3Aissue+label%3A%22type%3A+feature+request%22).
If you have an idea for a feature first check this list. If your idea already appears then add a +1 to the top most comment.

Otherwise, open a new issue and describe your proposed feature. Is it related to a problem? Why is it needed? What are possible alternatives?

### Pull Requests
Pull Requests always follow the [Pull Request Template](https://github.com/Dockbox-OSS/Hartshorn/blob/hartshorn-main/PULL_REQUEST_TEMPLATE.md).

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
- The target coverage for all tests is 60%
- Tests are performed using the source JDK version. CI will test against all supported JDK versions

#### Unit Testing
- Tests are located in `src/test/java`
- Test packages are equal to the package of the target class, prefixed by `test` (e.g. `test.org.dockbox.hartshorn.core`)
- Test classes follow the naming convention `${TestedClass}Tests`
- Tests follow the [AAA pattern](https://medium.com/@pjbgf/title-testing-code-ocd-and-the-aaa-pattern-df453975ab80)
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
We currently use a modified version of [Jetbrains' Java Style](https://www.jetbrains.com/help/idea/code-style-java.html) 
for our coding convention.  
The base repository has an [`Hartshorn Code Style.xml`](https://github.com/Dockbox-OSS/Hartshorn/blob/hartshorn-main/Hartshorn%20Code%20Style.xml) file. If you use any Jetbrains IDE you can import this under `Editor > Code Style`.  
Alternatively, you can also use the [.editorconfig](https://github.com/Dockbox-OSS/Hartshorn/blob/hartshorn-main/.editorconfig.xml) file with any IDE that supports it.  
Additionally, you should use [`Hartshorn Code Inspections.xml`](https://github.com/Dockbox-OSS/Hartshorn/blob/hartshorn-main/Hartshorn%20Code%20Inspections.xml), which can be imported under `Editor > Inspections`.
