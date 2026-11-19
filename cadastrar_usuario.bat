@echo off
chcp 65001 > nul
title Gerador de Credenciais SHA-256 - Colegio Santo Agostinho

echo =========================================================
echo   CADASTRO DE USUARIO E HASH SHA-256 (dados\usuarios.csv)
echo =========================================================
echo.

:: Verifica se a pasta dados existe antes de tentar gravar
if not exist "dados" (
    echo [ERRO] Pasta 'dados' nao encontrada!
    echo Execute este arquivo na raiz do projeto 'mvp-csa'.
    pause
    exit /b
)

:: Solicita os dados (ou pega via parametro se voce rodar pelo terminal)
set USUARIO=%~1
set SENHA=%~2
set PERFIL=%~3

if "%USUARIO%"=="" set /p USUARIO="1. Digite o novo Usuario (ex: secretaria): "
if "%SENHA%"=="" set /p SENHA="2. Digite a Senha em texto puro: "
if "%PERFIL%"=="" set /p PERFIL="3. Digite o Perfil (ex: SECRETARIA ou COORDENADOR): "

:: Validação básica para não gravar linha vazia
if "%USUARIO%"=="" (
    echo [ERRO] O usuario nao pode ficar em branco.
    pause
    exit /b
)
if "%SENHA%"=="" (
    echo [ERRO] A senha nao pode ficar em branco.
    pause
    exit /b
)
if "%PERFIL%"=="" set PERFIL=USUARIO

echo.
echo [PROCESSANDO] Gerando hash SHA-256...

:: Chama o PowerShell embutido no Windows para gerar o hash exato de 64 caracteres
for /f "usebackq tokens=*" %%i in (`powershell -NoProfile -Command "$bytes = [System.Text.Encoding]::UTF8.GetBytes('%SENHA%'); $hash = [System.Security.Cryptography.SHA256]::Create().ComputeHash($bytes); ([System.BitConverter]::ToString($hash)).Replace('-','').ToLower()"`) do set HASH_GERADO=%%i

:: Grava a nova linha no final do arquivo CSV
echo %USUARIO%;%HASH_GERADO%;%PERFIL%>> dados\usuarios.csv

echo.
echo =========================================================
echo [SUCESSO] Usuario gravado em 'dados\usuarios.csv'!
echo Linha adicionada: %USUARIO%;%HASH_GERADO%;%PERFIL%
echo =========================================================
echo.
echo IMPORTANTE: Se o Servidor.java ja estiver aberto, feche-o
echo (Ctrl + C) e inicie novamente para carregar o novo usuario.
echo.
pause