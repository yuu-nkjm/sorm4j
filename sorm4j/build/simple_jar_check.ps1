param(
  [string]$JarPathPattern
)

$checkPattern = '\.(pdf|xls?|doc?|csv)$'
$found = $false

Get-ChildItem $JarPathPattern | ForEach-Object {
  Write-Host "Checking $($_.FullName)"
  $list = & tar -tf $_.FullName 2>$null
  if ($list -match $checkPattern) {
    Write-Error "❌ Unexpected file found in $($_.Name)"
    $found = $true
  }
}

if (-not $found) {
  Write-Host "✅ ALL_OK: No files matching. Pattern = [$checkPattern]"
}
