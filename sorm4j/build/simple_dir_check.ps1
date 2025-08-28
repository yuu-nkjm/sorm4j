param(
  [string]$TargetDir
)

$checkPattern = '\.(pdf|xls?|doc?|csv)$'
$found = $false

# Search files recursively
Get-ChildItem -Path $TargetDir -Recurse -File | ForEach-Object {
  if ($_.Name -match $checkPattern) {
    Write-Error "❌ Unexpected file found: $($_.FullName)"
    $found = $true
  }
}

if (-not $found) {
  Write-Host "✅ ALL_OK: No files matching. Pattern = [$checkPattern]"
}
