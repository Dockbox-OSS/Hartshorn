$results = @()

# Find all jacoco index.html files
Get-ChildItem -Path ../ -Recurse -Filter index.html | Where-Object {
    $_.FullName -match "target[\\/]+site[\\/]+jacoco[\\/]+index\.html$"
} | ForEach-Object {

    $file = $_.FullName
    $projectDir = $_.Directory.Parent.Parent.Parent.FullName

    $content = Get-Content $file -Raw

    # Regex to capture the coverage percentage from the Total row
    if ($content -match '<tr><td>Total</td><td class="bar">.*?</td><td class="ctr2">(\d+)%</td>') {
        $coverage = $matches[1]

        $results += [PSCustomObject]@{
            Project  = $projectDir
            Coverage = "$coverage%"
        }
    }
}

# Print overview
$results | Sort-Object Project | Format-Table -AutoSize