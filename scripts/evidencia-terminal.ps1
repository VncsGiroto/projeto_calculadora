param(
    [Parameter(Mandatory = $true)]
    [string]$JavaHome,
    [string]$Nome = 'java8'
)
$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
$env:JAVA_HOME = $JavaHome
$env:Path = "$JavaHome\bin;$env:Path"
$env:MAVEN_OPTS = '-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8'
$OutputEncoding = [Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$Host.UI.RawUI.WindowTitle = "FinancApp - testes reais - $Nome"
try {
    $janela = $Host.UI.RawUI.WindowSize
    $janela.Width = [Math]::Min(150, $Host.UI.RawUI.MaxPhysicalWindowSize.Width)
    $janela.Height = [Math]::Min(48, $Host.UI.RawUI.MaxPhysicalWindowSize.Height)
    $buffer = $Host.UI.RawUI.BufferSize
    $buffer.Width = [Math]::Max($buffer.Width, $janela.Width)
    $buffer.Height = [Math]::Max($buffer.Height, 3000)
    $Host.UI.RawUI.BufferSize = $buffer
    $Host.UI.RawUI.WindowSize = $janela
} catch { Write-Host 'O tamanho da janela foi mantido pelo terminal.' }
New-Item -ItemType Directory -Force evidencias/compatibilidade | Out-Null
Write-Host '> java -version'
& "$JavaHome/bin/java.exe" -version
Write-Host '> mvn -B -ntp clean verify'
& mvn -B -ntp clean verify 2>&1 | Tee-Object -FilePath "evidencias/compatibilidade/$Nome.log"
if ($LASTEXITCODE -ne 0) { throw 'Falha na execução dos testes.' }
Write-Host '> java -version'
& "$JavaHome/bin/java.exe" -version
