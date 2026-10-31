param(
    [ValidateSet('inicial', 'refatoracao')]
    [string]$Etapa = 'refatoracao'
)
$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    New-Item -ItemType Directory -Force "evidencias/$Etapa" | Out-Null
    # Força UTF-8 também no processo Java iniciado pelo Surefire.
    $env:MAVEN_OPTS = "$env:MAVEN_OPTS -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
    $build = & mvn -B -ntp clean verify 2>&1
    $codigo = $LASTEXITCODE
    $build | Set-Content -Encoding utf8 "evidencias/$Etapa/maven.log"
    if ($codigo -ne 0) { throw "Falha na validação. Consulte evidencias/$Etapa/maven.log" }
    [xml]$xml = Get-Content -Raw -Encoding utf8 'target/surefire-reports/TEST-calculadora.TesteCalculadora.xml'
    # O XML completo contém propriedades da máquina; publica somente resultados.
    $linhas = foreach ($caso in $xml.testsuite.testcase) {
        $nome = [System.Net.WebUtility]::HtmlEncode($caso.name)
        $status = if ($caso.failure -or $caso.error) { 'FALHOU' } elseif ($caso.skipped) { 'IGNORADO' } else { 'PASSOU' }
        "<tr><td>$nome</td><td>$status</td></tr>"
    }
    $saida = foreach ($caso in $xml.testsuite.testcase) {
        if ($caso.'system-out') { $caso.'system-out'.InnerText }
    }
    $saida | Set-Content -Encoding utf8 "evidencias/$Etapa/cenarios.txt"
    $saidaHtml = [System.Net.WebUtility]::HtmlEncode(($saida -join "`n"))
    $html = @"
<!doctype html><html lang="pt-BR"><meta charset="utf-8"><title>Testes — $Etapa</title>
<style>body{font:16px system-ui;margin:40px;color:#182b3a;background:#f3f6f8}h1{margin-bottom:8px}header,p,table,pre{background:white;padding:20px;border-radius:8px}header{border-left:6px solid #147650}table{border-collapse:collapse;width:100%}td,th{text-align:left;padding:8px 14px;border-bottom:1px solid #ddd}td:last-child{color:#147650;font-weight:bold}pre{white-space:pre-wrap;font-size:14px}small{color:#52616b}</style>
<header><small>FINANÇAPP / QUALIDADE DE SOFTWARE</small><h1>Validação: $Etapa</h1>
<p>$($xml.testsuite.tests) testes · $($xml.testsuite.failures) falhas · $($xml.testsuite.errors) erros · $($xml.testsuite.skipped) ignorados</p>
<small>Relatório gerado a partir do XML real do Maven Surefire. Comando: mvn -B -ntp clean verify.</small></header>
<h2>Cenários executados</h2><table><tr><th>Teste</th><th>Resultado</th></tr>$($linhas -join "`n")</table>
<h2>Entradas, resultados esperados e obtidos</h2><pre>$saidaHtml</pre></html>
"@
    $html | Set-Content -Encoding utf8 "evidencias/$Etapa/relatorio.html"
    $resumo = "Etapa: $Etapa | testes: $($xml.testsuite.tests) | falhas: $($xml.testsuite.failures) | erros: $($xml.testsuite.errors)"
    $resumo | Set-Content -Encoding utf8 "evidencias/$Etapa/resumo.txt"
    Write-Output $resumo
} finally { Pop-Location }
