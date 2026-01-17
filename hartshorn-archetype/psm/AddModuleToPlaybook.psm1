function Add-ModuleToPlaybook
{
    param (
        [string]$PlayBook,
        [string]$ModuleId
    )

    Write-Host "Updating playbook '$PlayBook' with module 'hartshorn-$ModuleId'..."

    $antoraPlaybookPath = "..\hartshorn-assembly\antora\playbook-$PlayBook.yml"
    $lines = Get-Content $antoraPlaybookPath

    # Manual line insertion, as powershell-yaml does not preserve comments
    $lastIndex = ($lines | ForEach-Object { $_ } | Select-String '\s*-\s.*src\/main\/docs' | Select-Object -Last 1).LineNumber - 1
    $newLine = "        - hartshorn-$ModuleId/src/main/docs"
    $lines = $lines[0..$lastIndex] + $newLine
    $lines | Set-Content $antoraPlaybookPath
}