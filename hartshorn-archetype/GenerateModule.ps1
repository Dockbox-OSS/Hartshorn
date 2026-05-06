param (
    [Parameter(Mandatory=$true, HelpMessage="i.e. 'inject'")][string]$Module,
    [Parameter(Mandatory=$true, HelpMessage="i.e. 'Hartshorn Inject'")][string]$Name,
    [Parameter(Mandatory=$true, HelpMessage="i.e. 'Inject module for Hartshorn'")][string]$Description,
    [Parameter(Mandatory=$false)][switch]$NoDocs
)

### Module Imports ###
Import-Module "$PSScriptRoot\psm\AddModuleToPlaybook.psm1"
Import-Module "$PSScriptRoot\psm\NewModuleFromArchetype.psm1"

### Preconditions ###
# No uncommitted changes
$gitStatus = git status --porcelain
if ($gitStatus) {
    Write-Host "You have uncommitted changes in your working directory. Please commit or stash them before running this script." -ForegroundColor Red
    exit 1
}

# Module name validation
if ($Module -notmatch '^[a-z\-]*$') {
    Write-Host "Invalid module name '$Module'. Module names must start with a lowercase letter and can only contain lowercase letters and hyphens." -ForegroundColor Red
    exit 1
}

# Directory existence check, performed in NewModuleFromArchetype.psm1

### Module Generation ###
New-ModuleFromArchetype -Module $Module -Name $Name -Description $Description -NoDocs $NoDocs

### Antora Playbook Update ###
if (-NOT $NoDocs) {
    Write-Host "`nUpdating Antora playbook to include new module..." -ForegroundColor Green
    Add-ModuleToPlaybook -playBook "local" -moduleId $Module
    Add-ModuleToPlaybook -playBook "release" -moduleId $Module
}

### Completion Message ###
Write-Host "`nModule 'hartshorn-$Module' generation and integration complete! Actions performed:" -ForegroundColor Green
Write-Host "- Generated module at '../hartshorn-$Module'"
if (-NOT $NoDocs) {
    Write-Host "- Updated Antora playbooks 'local' and 'release'"
}
Write-Host "- Updated hartshorn-assembly POM with new module dependency"

### Next Steps ###
Write-Host "`nDue to formatting and custom ordering requirements, some manual steps are required to finalize the integration of the new module." -ForegroundColor DarkYellow
Write-Host "- Add org.dockbox.hartshorn:hartshorn-$Module to pom.xml <modules>" -ForegroundColor Yellow
Write-Host "  <module>hartshorn-$Module</module>" -ForegroundColor DarkGray
Write-Host "- Add org.dockbox.hartshorn:hartshorn-$Module to hartshorn-bom/pom.xml <dependencyManagement>" -ForegroundColor Yellow
Write-Host "  <dependency>" -ForegroundColor DarkGray
Write-Host "    <groupId>org.dockbox.hartshorn</groupId>" -ForegroundColor DarkGray
Write-Host "    <artifactId>hartshorn-$Module</artifactId>" -ForegroundColor DarkGray
Write-Host "    <version>`${revision}</version>" -ForegroundColor DarkGray
Write-Host "  </dependency>" -ForegroundColor DarkGray

Write-Host "`nAfter completing the above manual steps, please:" -ForegroundColor DarkYellow
Write-Host "- Review the generated module at 'hartshorn-$Module' and make any necessary adjustments." -ForegroundColor Yellow
Write-Host "- Build the project to ensure everything is set up correctly." -ForegroundColor Yellow
Write-Host "  `$ mvn clean install -DskipTests -P ci" -ForegroundColor DarkGray
if (-NOT $NoDocs) {
    Write-Host "- Verify that the documentation for the new module is correctly integrated into the Antora site." -ForegroundColor Yellow
    Write-Host "  `$ mvn clean install -DskipTests -P ci,local -Dantora.skip=false -Dantora.playbook=playbook-local.yml" -ForegroundColor DarkGray
}