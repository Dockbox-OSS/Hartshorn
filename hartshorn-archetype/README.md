# Hartshorn Archetype
This project provides a Maven archetype for creating new framework modules within the Hartshorn framework. It
sets up basic project structure and configurations to aid the development of new modules.

Note that this archetype is intended for internal use by Hartshorn developers and is not suitable for
general-purpose module development.

## Usage
To create a new Hartshorn module using this archetype, run the following Maven command:
```bash
GenerateModule.ps1
```

The script prompts for the following parameters:
- `Module`: The name of the new module (e.g. `inject`)
- `Name`: The display name of the new module (e.g. `Hartshorn Inject`)
- `Description`: A brief description of the new module (e.g. `A dependency injection framework for Hartshorn`)

Optionally, if you do not want to generate documentation for the new module (e.g. for test fixtures or similar
internal modules), you can pass the `-NoDocs` flag to skip documentation generation.

### Automated Steps
Module generation includes:
- Creating a new Maven project with the appropriate `groupId`, `artifactId`, and `version`.
- Setting up the basic directory structure for:
  - Source code and resources
  - Test code and resources
  - Antora documentation
- Registers the new module in the assembly POM
- Registers the new module in the documentation indexes (local and release)

### Manual Steps
Module generation does not include:
- Registering the new module in the BOM, this must be done manually.
- Registering the new module in the parent POM, this must be done manually.

## Prerequisites
### Maven
As module generation is performed outside of the main project (and thus cannot use the integrated Maven runner
in your IDE), you should have Maven installed and configured on your system, please refer to the
[Maven installation guide](https://maven.apache.org/install.html) for instructions.

### PowerShell (Linux/Mac)
The module generation script is written in PowerShell. If you are using Linux or Mac, ensure that you have
PowerShell installed. You can find installation instructions on the 
[PowerShell Learn page](https://learn.microsoft.com/en-us/powershell/scripting/install/install-powershell).