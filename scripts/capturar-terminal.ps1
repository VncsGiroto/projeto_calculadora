param(
    [Parameter(Mandatory = $true)]
    [int]$ProcessoTerminal,
    [Parameter(Mandatory = $true)]
    [string]$Destino
)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
# Copia os pixels reais da janela, sem enviar comandos ou teclas pela interface.
if (-not ('CapturaTerminalFinancApp' -as [type])) {
    Add-Type @'
using System;
using System.Runtime.InteropServices;
public static class CapturaTerminalFinancApp {
    [StructLayout(LayoutKind.Sequential)]
    public struct RECT { public int Left, Top, Right, Bottom; }
    [DllImport("user32.dll")]
    public static extern IntPtr SetThreadDpiAwarenessContext(IntPtr context);
    [DllImport("user32.dll")]
    public static extern bool GetWindowRect(IntPtr window, out RECT rect);
    [DllImport("user32.dll")]
    public static extern bool PrintWindow(IntPtr window, IntPtr dc, uint flags);
}
'@
}
[void][CapturaTerminalFinancApp]::SetThreadDpiAwarenessContext([IntPtr]::new(-4))
$processo = Get-Process -Id $ProcessoTerminal
if ($processo.MainWindowHandle -eq 0) { throw 'O processo não possui janela capturável.' }
$retangulo = New-Object CapturaTerminalFinancApp+RECT
if (-not [CapturaTerminalFinancApp]::GetWindowRect($processo.MainWindowHandle, [ref]$retangulo)) {
    throw 'Não foi possível obter as dimensões da janela.'
}
$bitmap = New-Object System.Drawing.Bitmap(($retangulo.Right - $retangulo.Left), ($retangulo.Bottom - $retangulo.Top))
$grafico = [System.Drawing.Graphics]::FromImage($bitmap)
$dc = $grafico.GetHdc()
try {
    if (-not [CapturaTerminalFinancApp]::PrintWindow($processo.MainWindowHandle, $dc, 2)) {
        throw 'Não foi possível capturar a janela.'
    }
} finally { $grafico.ReleaseHdc($dc) }
try {
    $caminho = [System.IO.Path]::GetFullPath($Destino)
    $bitmap.Save($caminho, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Output "Captura real salva: $caminho"
} finally { $grafico.Dispose(); $bitmap.Dispose() }
