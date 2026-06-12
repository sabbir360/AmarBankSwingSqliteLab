@echo off
REM Compile and run AmarBank on Windows (Command Prompt / PowerShell).
REM Downloads the SQLite JDBC driver into lib\ if it is missing, then
REM compiles with javac and runs with java. No build tool required.
setlocal enabledelayedexpansion
cd /d "%~dp0"

set "SRC_DIR=src\main\java"
set "LIB_DIR=lib"
set "OUT_DIR=out"
set "MAIN_CLASS=com.amarbank.Main"
set "SQLITE_JDBC_VERSION=3.36.0"
set "SQLITE_JDBC_URL=https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/%SQLITE_JDBC_VERSION%/sqlite-jdbc-%SQLITE_JDBC_VERSION%.jar"
set "JAR=%LIB_DIR%\sqlite-jdbc.jar"

if not exist "%JAR%" (
    echo Downloading SQLite JDBC %SQLITE_JDBC_VERSION%...
    if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"
    powershell -NoProfile -Command "Invoke-WebRequest -Uri '%SQLITE_JDBC_URL%' -OutFile '%JAR%'"
    if errorlevel 1 (
        echo Failed to download %SQLITE_JDBC_URL% 1>&2
        exit /b 1
    )
) else (
    echo Using SQLite JDBC: %JAR%
)

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
if not exist "db_file" mkdir "db_file"

echo Compiling...
dir /b /s "%SRC_DIR%\*.java" > "%TEMP%\amarbank_sources.txt"
javac -cp "%JAR%" -d "%OUT_DIR%" "@%TEMP%\amarbank_sources.txt"
del "%TEMP%\amarbank_sources.txt"
if errorlevel 1 exit /b 1

echo Running...
java --enable-native-access=ALL-UNNAMED -cp "%OUT_DIR%;%JAR%" %MAIN_CLASS%
endlocal
