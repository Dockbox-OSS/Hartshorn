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
Selene is under active development. The team's primary focus is on fixing known issues, and adding compelling new features.  
You can view the list of proposed features by filtering the issue tracker by the ["Type: Enhancement" label](https://github.com/GuusLieben/Selene/issues?q=is%3Aopen+is%3Aissue+label%3A%22Type%3A+Enhancement%22).
If you have an idea for a feature first check this list. If your idea already appears then add a +1 to the top most comment.

Otherwise, open a new issue and describe your proposed feature. Is it related to a problem? Why is it needed? What are possible alternatives?

### Suggesting Extensions
Selene comes with a variety of integrated extensions to further aid developers in developing against its API. You can view
the list of proposed extensions by filtering the issue tracker by the ["Type: Extension"](https://github.com/GuusLieben/Selene/issues?q=is%3Aopen+is%3Aissue+label%3A%22Type%3A+Extension%22) 
and ["Suggested resolution: Extension"](https://github.com/GuusLieben/Selene/issues?q=is%3Aopen+is%3Aissue+label%3A%22Suggested+resolution%3A+Extension%22) labels.
If you have an idea for a extension first check this list. If your idea already appears then add a +1 to the top most comment.

Otherwise, open a new issue and describe your proposed extension. Is it related to a problem? What are possible alternatives?

### Pull Requests
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
#### Repository Structure
TODO (`org.dockbox.selene.test.packageY`)

#### Unit Testing
TODO (AAA)

#### Run Testing
TODO

## Style Guides
### Repository Structure
TODO

### JavaDocs
TODO  
No prepending `* `
All non `@tag` arguments start with capital
All `@link`, `@see` and similar tags use classname prefixes even if it is within the same type. E.g. `{@link X#doY}` 
instead of `{@link #doY}`
General description ends with `.` but any `@tag` does not

Element order:
- Description
- Empty line
- @param <Generic>, followed by newline, followed by description
- @param x, followed by newline, followed by description of param
- Empty line
- @return, followed by description of return value
- @throws, followed by the exception type, followed by newline, followed by description of error
- Empty line
- @see

Example:
```java
public class X {
    
    /**
     Performs X.
     
     @param <T>
     The type parameter for arg0
     @param arg0 
     The first argument
     @param arg1 
     The second argument
     
     @return The object X
     @throws Exception
     When Y happens
     
     @see X#doY
     */
    public <T> Object doX(T arg0, String arg1) throws Exception {
        // Do x
    }
}
```
