# SPDX-FileCopyrightText: 2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
#
# SPDX-License-Identifier: Apache-2.0

param (
  $SourceRoot = "$PSScriptRoot/../.."
)

# REUSE-IgnoreStart

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Parse-CopyrightStatement
{
  param(
    [Parameter(Mandatory = $true)]
    [string] $Statement
  )

  $trimmed = $Statement.Trim()
  $spdxPrefix = 'SPDX-FileCopyrightText:\s*'
  $match = [Regex]::Match($trimmed, "^(?:$spdxPrefix)?(?<years>\d{4}(?:\s*-\s*\d{4})?)\s+(?<holder>.+)$")
  if (!$match.Success)
  {
    Write-Warning "Cannot parse statement: $Statement"
    return $null
  }

  $yearMatch = [Regex]::Match($match.Groups['years'].Value, '^(?<start>\d{4})(?:\s*-\s*(?<end>\d{4}))?$')
  if (!$yearMatch.Success)
  {
    Write-Warning "Cannot parse year: $($match.Groups['years'].Value)"
    return $null
  }

  $startYear = [int] $yearMatch.Groups['start'].Value
  $endYear = if ($yearMatch.Groups['end'].Success) { [int] $yearMatch.Groups['end'].Value } else { $startYear }
  if ($endYear -lt $startYear)
  {
    $temp = $startYear
    $startYear = $endYear
    $endYear = $temp
  }

  [PSCustomObject] @{
    Holder = $match.Groups['holder'].Value.Trim()
    StartYear = $startYear
    EndYear = $endYear
  }
}

Push-Location $SourceRoot
try
{
  $reuseJson = reuse lint --json
  if ($LASTEXITCODE -ne 0)
  {
    throw "Cannot collect copyright statements from the REUSE tool. Exit code: $LASTEXITCODE."
  }
  $licenses = $reuseJson | ConvertFrom-Json
}
finally
{
  Pop-Location
}

$allStatements = foreach ($file in $licenses.files)
{
  if (
    $file.path -like 'gradle/wrapper/*' -or
    $file.path -eq 'gradlew' -or
    $file.path -eq 'gradlew.bat'
  )
  {
    continue
  }

  foreach ($copyright in $file.copyrights)
  {
    foreach ($value in @($copyright.value))
    {
      if (![string]::IsNullOrWhiteSpace($value))
      {
        $value.Trim()
      }
    }
  }
}

$byHolder = [System.Collections.Generic.Dictionary[string, object]]::new([StringComparer]::Ordinal)
$unparsed = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)

foreach ($statement in $allStatements)
{
  $parsed = Parse-CopyrightStatement $statement
  if ($null -eq $parsed)
  {
    $null = $unparsed.Add($statement)
    continue
  }

  if ($byHolder.ContainsKey($parsed.Holder))
  {
    $existing = $byHolder[$parsed.Holder]
    $existing.StartYear = [Math]::Min($existing.StartYear, $parsed.StartYear)
    $existing.EndYear = [Math]::Max($existing.EndYear, $parsed.EndYear)
  }
  else
  {
    $byHolder[$parsed.Holder] = [PSCustomObject] @{
      Holder = $parsed.Holder
      StartYear = $parsed.StartYear
      EndYear = $parsed.EndYear
    }
  }
}

$sortedParsed = $byHolder.Values | Sort-Object StartYear, Holder
$result = foreach ($entry in $sortedParsed)
{
  $years = if ($entry.StartYear -eq $entry.EndYear) { "$($entry.StartYear)" } else { "$($entry.StartYear)-$($entry.EndYear)" }
  "$years $($entry.Holder)"
}

$result
$unparsed | Sort-Object

# REUSE-IgnoreEnd
