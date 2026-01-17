function New-ModuleFromArchetype
{
    param (
        [Parameter(Mandatory = $true)][string]$Module,
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$Description,
        [Parameter(Mandatory = $true)][boolean]$NoDocs
    )

    $tmpDir = "tmp"
    $targetDir = Join-Path -Path "..\.." -ChildPath "hartshorn-$Module"

    # Clean up if previous run left temp files
    if (Test-Path $tmpDir)
    {
        Remove-Item -Recurse -Force $tmpDir
    }

    # Ensure target directory does not already exist
    if (Test-Path $targetDir)
    {
        Write-Host "Target directory '$targetDir' already exists. Please remove it before generating a new module." -ForegroundColor Red
        exit 1
    }

    # Ensure latest archetype is installed
    try
    {
        Write-Host "`nInstalling latest archetype to local Maven repository..." -ForegroundColor Green
        # Quietly install the archetype to the local Maven repository, disable output
        mvn clean install -f pom.xml -q
        Write-Host "Archetype installed successfully."
    }
    catch
    {
        Write-Error "Failed to install the latest archetype: $_"
        exit 1
    }

    ### Maven Archetype Generation ###
    Write-Host "`nGenerating module 'hartshorn-$Module'..." -ForegroundColor Green

    # Create working directory, to avoid running inside existing Maven module
    New-Item -ItemType Directory -Path $tmpDir | Out-Null
    Set-Location $tmpDir
    Write-Host "Changed working directory to temporary path '$tmpDir'."

    # Prepare Maven archetype
    $mvnArgs = @(
        "archetype:generate",
        "-DarchetypeGroupId=org.dockbox.hartshorn",
        "-DarchetypeArtifactId=hartshorn-archetype",
        "-DarchetypeVersion=1.0.0",
        "-DinteractiveMode=false",
        "-DgroupId=org.dockbox.hartshorn",
        "-DartifactId=hartshorn-$Module",
        "-Dname=$Name",
        "-Ddescription=$Description",
        "-DmoduleId=$Module"
    )

    try
    {
        Write-Host "Generating module using Maven archetype..."
        mvn @mvnArgs -q

        if ($NoDocs)
        {
            # Delete docs directory if NoDocs flag is set
            $docsPath = Join-Path -Path "hartshorn-$Module" -ChildPath "src\main\docs"
            if (Test-Path $docsPath)
            {
                Remove-Item -Recurse -Force $docsPath
            }
        }

        Write-Host "Moving generated module to target directory..."
        Move-Item -Path "hartshorn-$Module" -Destination $targetDir
        Write-Host "Module 'hartshorn-$Module' generated successfully at '$targetDir'."
    }
    catch
    {
        Write-Error "Maven archetype generation failed: $_"
        exit 1
    }
    finally
    {
        # Return to original directory and clean up temp directory
        Set-Location ..
        Remove-Item -Recurse -Force $tmpDir
    }
}