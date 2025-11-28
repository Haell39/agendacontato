@echo off
echo ================================
echo Verificando ambiente Android
echo ================================
echo.

echo [1/4] Verificando Java...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Java nao encontrado!
    echo Instale JDK 11: https://adoptium.net/temurin/releases/?version=11
) else (
    echo [OK] Java instalado!
)
echo.

echo [2/4] Verificando ANDROID_HOME...
if defined ANDROID_HOME (
    echo [OK] ANDROID_HOME: %ANDROID_HOME%
) else (
    echo [ERRO] ANDROID_HOME nao configurado!
    echo Configure em: Variaveis de Ambiente do Sistema
)
echo.

echo [3/4] Verificando ADB (Android Debug Bridge)...
adb version
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] ADB nao encontrado!
    echo Instale Android SDK
) else (
    echo [OK] ADB instalado!
)
echo.

echo [4/4] Verificando Gradle Wrapper...
if exist gradlew.bat (
    echo [OK] Gradle Wrapper encontrado!
    echo Testando build...
    call gradlew.bat --version
) else (
    echo [ERRO] gradlew.bat nao encontrado!
)
echo.

echo ================================
echo Verificacao concluida!
echo ================================
pause
