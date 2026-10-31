param([string]$Navegador = 'C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe')
$ErrorActionPreference = 'Stop'
$raiz = Split-Path $PSScriptRoot -Parent
if (-not (Test-Path -LiteralPath $Navegador)) { throw 'Informe o caminho do Edge no parâmetro -Navegador.' }
$destinos = @(
    @('evidencias/inicial/relatorio.html', 'images/testes/inicial.png', '1400,1900'),
    @('evidencias/refatoracao/relatorio.html', 'images/testes/refatoracao.png', '1400,1900'),
    @('docs/calculadora/Calculadora.html', 'images/javadoc/calculadora.png', '1400,1600'),
    @('evidencias/refatoracao/comparacao.html', 'images/refatoracao/comparacao.png', '1400,1600')
)
foreach ($destino in $destinos) {
    $origem = (Join-Path $raiz $destino[0])
    $imagem = Join-Path $raiz $destino[1]
    New-Item -ItemType Directory -Force (Split-Path $imagem -Parent) | Out-Null
    $url = ([uri]$origem).AbsoluteUri
    $perfil = Join-Path ([System.IO.Path]::GetTempPath()) ('financapp-captura-' + [guid]::NewGuid())
    $argumentos = @('--headless=new', '--disable-gpu', '--no-first-run', '--hide-scrollbars', "--window-size=$($destino[2])", "--user-data-dir=`"$perfil`"", "--screenshot=`"$imagem`"", "`"$url`"")
    $processo = Start-Process -FilePath $Navegador -ArgumentList $argumentos -WindowStyle Hidden -PassThru -Wait
    if (-not (Test-Path -LiteralPath $imagem)) { throw "Captura não gerada: $imagem" }
    Write-Output "Captura: $($destino[1])"
}
