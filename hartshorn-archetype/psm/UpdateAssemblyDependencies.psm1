function Update-AssemblyDependencies
{
    param (
        [Parameter(Mandatory)]
        [string]$FilePath,

        [Parameter(Mandatory)]
        [string]$GroupId,

        [Parameter(Mandatory)]
        [string]$ArtifactId
    )

    $pomLines = Get-Content $FilePath

    # Find the last line of the <dependencies> block that is a direct child of <project>
    # Match the <dependencies> that is not inside <dependencyManagement>
    $inProjectDependencies = $false
    $lastIndex = -1

    for ($i = 0; $i -lt $pomLines.Count; $i++) {
        $line = $pomLines[$i].Trim()

        if ($line -match '<dependencyManagement>')
        {
            $inProjectDependencies = $false
        }

        if ($line -match '<dependencies>')
        {
            # If not inside dependencyManagement, enable flag
            if (-not ($i -gt 0 -and ($pomLines[$i - 1] -match '<dependencyManagement>')))
            {
                $inProjectDependencies = $true
            }
        }

        if ($line -match '</dependencies>' -and $inProjectDependencies)
        {
            $lastIndex = $i - 1  # insert before the closing </dependencies>
            break
        }
    }

    if ($lastIndex -lt 0)
    {
        Write-Error "Could not find a <dependencies> block under <project>."
        return
    }

    $indent = "    "
    $newDependency = @(
        "$indent<dependency>"
        "$indent  <groupId>$GroupId</groupId>"
        "$indent  <artifactId>$ArtifactId</artifactId>"
        "$indent</dependency>"
    )

    # Insert the new dependency before </dependencies>
    $pomLines = $pomLines[0..$lastIndex] + $newDependency + $pomLines[($lastIndex + 1)..($pomLines.Count - 1)]
    $pomLines | Set-Content $FilePath

    Write-Host "Added dependency '${GroupId}:${ArtifactId}' to '$FilePath'."
}