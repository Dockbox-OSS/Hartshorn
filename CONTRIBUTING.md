# Contributing to Selene
## What should I know before I get started?
### Debug Selene
The documentation about Selene's Maven setup can be found [here](https://github.com/GuusLieben/Selene/wiki/Maven). This
describes the existing project structure and how to use the provided test servers.

### Where can I ask for help?
The [Darwin Reforged Discord](https://discord.gg/PpXkDf4) or [email](mailto:guuslieben@xendox.com) are the best places
to ask for help. Alternatively you can [open a issue](https://github.com/GuusLieben/Selene/issues/new/choose) for suggestions
and/or enhancements. Please do not file support requests on the GitHub issue tracker.

## How can I contribute?
### Reporting Bugs
_[Template](https://github.com/GuusLieben/Selene/blob/selene-main/.github/ISSUE_TEMPLATE/bug_report.md)_  
If you run into an issue, please use our [issue tracker](https://github.com/GuusLieben/Selene/issues) _first_ to ensure
the issue hasn't been reported before. Open a new issue only if you haven't found anything similar to your issue.

You can open a new issue and search existing issues using the public [issue tracker](https://github.com/GuusLieben/Selene/issues).

**When opening a new issue, please include the following information in the issue:**
- What Selene version you are using
- What platform(s) you have confirmed the issue on
- Which JDK version you are using
- Describe the issue you are experiencing
- Describe how to reproduce the issue
- Describe what you expect the correct behavior to be like
- Include any warnings, arrors, stacktraces if applicable

When a new issue is opened, be prepared to work with the developers investigating your issue. It is not uncommen additional
information is requested, and your assistance is crucial in providing a quick solution.  
In general, the more detail you share about your issue the quicker it can be resolved. For example, providing a simple
test case is exceptionally helpful.

### Suggesting Enhancements
_[Template](https://github.com/GuusLieben/Selene/blob/selene-main/.github/ISSUE_TEMPLATE/enhancement.md)_    
Selene is under active development. The team's primary focus is on fixing known issues, and adding compelling new features.  
You can view the list of proposed features by filtering the issue tracker by the ["Type: Enhancement" label](https://github.com/GuusLieben/Selene/issues?q=is%3Aopen+is%3Aissue+label%3A%22Type%3A+Enhancement%22).
If you have an idea for a feature first check this list. If your idea already appears then add a +1 to the top most comment.

Otherwise, open a new issue and describe your proposed feature. Is it related to a problem? Why is it needed? What are possible alternatives?

### Suggesting Extensions
_[Template](https://github.com/GuusLieben/Selene/blob/selene-main/.github/ISSUE_TEMPLATE/extension.md)_  
Selene comes with a variety of integrated extensions to further aid developers in developing against its API. You can view
the list of proposed extensions by filtering the issue tracker by the ["Type: Extension"](https://github.com/GuusLieben/Selene/issues?q=is%3Aopen+is%3Aissue+label%3A%22Type%3A+Extension%22) 
and ["Suggested resolution: Extension"](https://github.com/GuusLieben/Selene/issues?q=is%3Aopen+is%3Aissue+label%3A%22Suggested+resolution%3A+Extension%22) labels.
If you have an idea for a extension first check this list. If your idea already appears then add a +1 to the top most comment.

Otherwise, open a new issue and describe your proposed extension. Is it related to a problem? What are possible alternatives?

### Pull Requests
Pull Requests always follow the [Pull Request Template](https://github.com/GuusLieben/Selene/blob/selene-main/PULL_REQUEST_TEMPLATE.md).

#### General
- Try to limit pull requests to a few commits which resolve a specific issue
- Make sure commit messages are descriptive of the changes made
- All proposed changes must be reviewed and approved by at least one organization member
- Ensure all new code is documented according to the [JavaDoc styleguide](TODO LINK)
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
- Tests are performed using [Java](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) 8

#### Unit Testing
- Tests are located in `src/test/java`
- Test packages are equal to the package of the target class
- Test classes follow the naming convention `${TestedClass}Tests`
- Tests follow the [AAA pattern](https://medium.com/@pjbgf/title-testing-code-ocd-and-the-aaa-pattern-df453975ab80)
- Tests use JUnit 5 (`org.junit.jupiter.api`)

For example, `org.dockbox.selene.common.ClassX` is tested in `org.dockbox.selene.common.ClassXTests`

#### Run Testing
- Tests are performed using the [predefined Selene Servers](https://github.com/GuusLieben/Selene-Servers)
- Tests are performed against the latest (supported) version of relevant platforms
- Servers are activated using the [Selene development server configurations](https://github.com/GuusLieben/Selene/wiki/Maven#development-server)

## Style Guides
### Project Structure
Selene is divided into a variety of submodules, see [Maven](https://github.com/GuusLieben/Selene/wiki/Maven) for specifications
for each submodule.

Each class and method is placed in the highest possible location in the submodule hierarchy. Exceptions can be made by 
team members.

### Repository Structure
Selene uses a standardised branching structure. The `selene-main` branch is the branch all development should be based on.
#### Branch names
- Issue related branches: S{issue number}-{short description} (e.g. [`S108-rate-limiting`](https://github.com/GuusLieben/Selene/issues/108))
- Non-issue (migration) related branches: S0-{short-description} (e.g. `S0-migrations`)

### Coding Conventions
#### Code Style
We currently use a modified version of [Jetbrains' Java Style](https://www.jetbrains.com/help/idea/code-style-java.html) 
for our coding convention.  
The base repository has an [`Selene Code Style.xml`](https://github.com/GuusLieben/Selene/blob/selene-main/Selene%20Code%20Style.xml) file. If you use any Jetbrains IDE you can import this under `Editor > Code Style`.  
Alternatively, you can also use the [.editorconfig](https://github.com/GuusLieben/Selene/blob/selene-main/.editorconfig.xml) file with any IDE that supports it.  
Additionally you should use [`Selene Code Inspections.xml`](https://github.com/GuusLieben/Selene/blob/selene-main/Selene%20Code%20Inspections.xml), which can be imported under `Editor > Inspections`.

#### Type Naming
Naming conventions follow [Oracle's Code Conventions for Java](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html#:~:text=Class%20names%20should%20be%20nouns,such%20as%20URL%20or%20HTML).
Two additional rules to these conventions are present for types which are registered to Selene's internal injector:
- Partial (abstract) implementations of interfaces are prefixed with `Default` (e.g. `DefaultCommandBus`)
- Complete (standalone) implementations of interfaces are prefixes with `Simple` (e.g. `SimpleEventBus`)

#### Modifiers
- Classes should always be `public`
- Modifiers should be in order:
    - `public` | `protected` | `private`
    - `static`
    - `abstract`
    - `synchronized`
    - `transient` | `volatile`
    - `final`
- `native` and `strictfp` are not used

Only team members can make exceptions to these rules.

### JavaDocs
#### General
- All non `@tag` arguments start with a capital letter
- All `@link`, `@see` and similar tags use classname prefixes even if it is within the same type. E.g. `{@link X#doY}` 
instead of `{@link #doY}`
- General description ends with `.` but any `@tag` does not

#### Structure
All JavaDocs follow the order:
> {method description}  
>   
> @param <T>  
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{generic type description}  
> @param x  
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{param description}  
>   
> @return {return description}    
> @throws {exception type}  
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{exception description}  
>   
> @see {link}

- Code examples are wrapped in `<pre>{@code ... }</pre>`
- Additional paragraphes are wrapped in `<p> ... </p>`
- Lists are formatted using `<ul></ul>`
- Descriptive links are formatted using `{@link org.dockbox.selene.ClassY y}` (shows as `y`)

Example:
```
/**
 * Performs X.
 *
 * <pre>{@code
 *    X x = new X();
 *    Object o = x.doX(y, z);
 * }</pre>
 *
 * <p>
 * Additional information about {@link Y y}.
 * </p>
 *
 * <ul>
 *    <li>A</li>
 *    <li>B</li>
 * </ul>
 *
 * @param <T>
 *        The type parameter for arg0
 * @param arg0
 *        The first argument
 * @param arg1
 *        The second argument
 *
 * @return The object X
 * @throws Exception
 *        When Y happens
 *
 * @see X#doY
 */
public <T> Object doX(T arg0, String arg1) throws Exception {
    // Do x
}
```
