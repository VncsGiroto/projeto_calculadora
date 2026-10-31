$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    $javadoc = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/javadoc.exe' } else { 'javadoc' }
    & $javadoc -quiet -private -Xdoclint:all -Werror -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 -author -version -notimestamp -d docs -sourcepath src/main/java -subpackages calculadora -doctitle 'FinançApp — Calculadora' -windowtitle 'FinançApp — JavaDoc'
    if ($LASTEXITCODE -ne 0) { throw 'Falha na geração do JavaDoc.' }
    Write-Output 'JavaDoc gerado: docs/index.html'
} finally { Pop-Location }
