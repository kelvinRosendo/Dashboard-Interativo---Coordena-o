[CmdletBinding()]
param(
    [string]$DbUrl = "jdbc:postgresql://localhost:5433/dashboard_escolar",
    [string]$DbUsername = "postgres",
    [Parameter(Mandatory = $true)]
    [string]$DbPassword,
    [string]$ServerAddress = "0.0.0.0",
    [int]$ServerPort = 8081,
    [string]$SpringProfile = "prod",
    [ValidateSet("jar", "maven")]
    [string]$Mode = "jar",
    [switch]$Build,
    [switch]$OpenFirewall
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent (Split-Path -Parent $scriptDir)
$jarPath = Join-Path $projectRoot "target\dashboard-escolar-0.0.1-SNAPSHOT.jar"

Set-Location $projectRoot

$env:DB_URL = $DbUrl
$env:DB_USERNAME = $DbUsername
$env:DB_PASSWORD = $DbPassword
$env:SERVER_ADDRESS = $ServerAddress
$env:SERVER_PORT = [string]$ServerPort
$env:SPRING_PROFILES_ACTIVE = $SpringProfile

Write-Host ""
Write-Host "Dashboard Escolar - inicializacao do servidor" -ForegroundColor Cyan
Write-Host "Projeto: $projectRoot"
Write-Host "Banco:   $DbUrl"
Write-Host "Usuario: $DbUsername"
Write-Host "Profile: $SpringProfile"
Write-Host "Escuta:  http://$ServerAddress`:$ServerPort"
Write-Host ""

if ($OpenFirewall) {
    $ruleName = "Dashboard Escolar $ServerPort"
    $existingRule = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue

    if (-not $existingRule) {
        New-NetFirewallRule `
            -DisplayName $ruleName `
            -Direction Inbound `
            -Protocol TCP `
            -LocalPort $ServerPort `
            -Action Allow | Out-Null

        Write-Host "Firewall liberado para a porta $ServerPort." -ForegroundColor Green
    }
    else {
        Write-Host "Regra de firewall ja existe para a porta $ServerPort." -ForegroundColor Yellow
    }
}

if ($Build) {
    Write-Host "Gerando artefato com Maven..." -ForegroundColor Cyan
    mvn clean package -DskipTests
}

if ($Mode -eq "jar") {
    if (-not (Test-Path $jarPath)) {
        throw "Arquivo nao encontrado: $jarPath. Rode o script com -Build ou gere o jar antes."
    }

    Write-Host "Iniciando aplicacao pelo jar..." -ForegroundColor Cyan
    java -jar $jarPath
    exit $LASTEXITCODE
}

Write-Host "Iniciando aplicacao pelo Maven..." -ForegroundColor Cyan
mvn spring-boot:run
exit $LASTEXITCODE
