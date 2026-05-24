@echo off
title SPK Medika — Startup
color 0A
setlocal EnableDelayedExpansion

echo.
echo  =============================================
echo    SPK Medika - PT Medika Akses Investama
echo  =============================================
echo.

:: ─── Variabel utama ───────────────────────────────────────────────────────
set "BASE=%~dp0"
set "TOOLS=%~dp0tools"
set "APP_JAR=%~dp0target\spk-1.0.0.jar"
set "DB_NAME=spk_medika"
set "DB_PORT=3306"
set "DB_USER=root"
set "DB_PASS="

if not exist "%TOOLS%" mkdir "%TOOLS%"

:: ══════════════════════════════════════════════════════════════════════════
::  [1/4]  JAVA 17
:: ══════════════════════════════════════════════════════════════════════════
echo  [1/4] Memeriksa Java 17+...
set "JAVA_EXE="
set "JAVA_HOME="

:: 1a. Dari JAVA_HOME environment yang ada
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
        goto :java_ok
    )
)

:: 1b. Lokasi instalasi umum
for %%d in (
    "C:\Program Files\Java\jdk-17"
    "C:\Program Files\Java\jdk-21"
    "C:\Program Files\Java\jdk-23"
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.11.9-hotspot"
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot"
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot"
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.3.9-hotspot"
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.4.7-hotspot"
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
    "C:\Program Files\Microsoft\jdk-17.0.12.9-hotspot"
    "C:\Program Files\Microsoft\jdk-21.0.4.7-hotspot"
    "C:\Program Files\BellSoft\LibericaJDK-17-Full"
    "C:\Program Files\Zulu\zulu-17"
    "C:\Program Files\Zulu\zulu-21"
    "C:\Program Files\OpenJDK\openjdk-17"
) do (
    if not defined JAVA_EXE (
        if exist "%%~d\bin\java.exe" (
            set "JAVA_EXE=%%~d\bin\java.exe"
            set "JAVA_HOME=%%~d"
        )
    )
)
if defined JAVA_EXE goto :java_ok

:: 1c. Dari tools lokal (hasil download sebelumnya)
if exist "%TOOLS%\jdk\bin\java.exe" (
    set "JAVA_EXE=%TOOLS%\jdk\bin\java.exe"
    set "JAVA_HOME=%TOOLS%\jdk"
    goto :java_ok
)

:: 1d. Dari PATH
where java >nul 2>&1
if not errorlevel 1 (
    for /f "delims=" %%i in ('where java 2^>nul') do (
        if not defined JAVA_EXE (
            set "JAVA_EXE=%%i"
            set "_tp=%%~dpi"
            set "JAVA_HOME=!_tp:~0,-5!"
        )
    )
)
if defined JAVA_EXE goto :java_ok

:: 1e. Tidak ditemukan → Download Temurin JDK 17 portable (tidak perlu admin)
echo.
echo  [AUTO-INSTALL] Java tidak ditemukan.
echo        Mengunduh Temurin JDK 17 dari Adoptium (~180 MB)...
echo        Harap tunggu, jangan tutup jendela ini.
echo.
set "JDK_ZIP=%TOOLS%\jdk17.zip"
powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; try { $r=Invoke-RestMethod 'https://api.adoptium.net/v3/assets/latest/17/hotspot?architecture=x64&image_type=jdk&jvm_impl=hotspot&os=windows&vendor=eclipse' -UseBasicParsing; $url=$r[0].binary.package.link; Write-Host \"  URL: $url\"; Invoke-WebRequest $url -OutFile '%JDK_ZIP%' -UseBasicParsing; exit 0 } catch { Write-Host \"ERROR: $_\"; exit 1 }"
if errorlevel 1 goto :java_fail
if not exist "%JDK_ZIP%" goto :java_fail

echo        Mengekstrak JDK...
powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; Expand-Archive '%JDK_ZIP%' '%TOOLS%\jdk_tmp' -Force; $d=(Get-ChildItem '%TOOLS%\jdk_tmp' -Directory | Select-Object -First 1); if(Test-Path '%TOOLS%\jdk'){Remove-Item '%TOOLS%\jdk' -Recurse -Force}; Move-Item $d.FullName '%TOOLS%\jdk'; Write-Host 'Ekstrak selesai.'"
del "%JDK_ZIP%" >nul 2>&1
if exist "%TOOLS%\jdk_tmp" rmdir /s /q "%TOOLS%\jdk_tmp" >nul 2>&1
if not exist "%TOOLS%\jdk\bin\java.exe" goto :java_fail
set "JAVA_EXE=%TOOLS%\jdk\bin\java.exe"
set "JAVA_HOME=%TOOLS%\jdk"
echo        Temurin JDK 17 berhasil diinstal di: %TOOLS%\jdk
goto :java_ok

:java_fail
echo.
echo  [ERROR] Gagal mendapatkan Java. Periksa koneksi internet.
echo          Atau instal manual dari: https://adoptium.net
pause & exit /b 1

:java_ok
set "PATH=%JAVA_HOME%\bin;%PATH%"
"%JAVA_EXE%" -version >nul 2>&1
if errorlevel 1 (
    echo  [ERROR] Java tidak dapat dijalankan.
    pause & exit /b 1
)
echo        Java OK  ^(%JAVA_HOME%^)

:: ══════════════════════════════════════════════════════════════════════════
::  [2/4]  MAVEN
:: ══════════════════════════════════════════════════════════════════════════
echo.
echo  [2/4] Memeriksa Maven...
set "MVN_CMD="
set "MVN_HOME="

:: 2a. Lokasi Scoop dan umum
for %%d in (
    "C:\Users\%USERNAME%\scoop\apps\maven\current"
    "C:\ProgramData\scoop\apps\maven\current"
    "C:\tools\maven"
    "C:\maven"
    "C:\Program Files\Apache\maven"
) do (
    if not defined MVN_CMD (
        if exist "%%~d\bin\mvn.cmd" (
            set "MVN_CMD=%%~d\bin\mvn.cmd"
            set "MVN_HOME=%%~d"
        )
    )
)
if defined MVN_CMD goto :maven_ok

:: 2b. Dari tools lokal
if exist "%TOOLS%\maven\bin\mvn.cmd" (
    set "MVN_CMD=%TOOLS%\maven\bin\mvn.cmd"
    set "MVN_HOME=%TOOLS%\maven"
    goto :maven_ok
)

:: 2c. Dari PATH
where mvn >nul 2>&1
if not errorlevel 1 (
    for /f "delims=" %%i in ('where mvn 2^>nul') do (
        if not defined MVN_CMD (
            set "MVN_CMD=%%i"
            set "_tp=%%~dpi"
            set "MVN_HOME=!_tp:~0,-5!"
        )
    )
)
if defined MVN_CMD goto :maven_ok

:: 2d. Download Apache Maven 3.9 portable
echo.
echo  [AUTO-INSTALL] Maven tidak ditemukan.
echo        Mengunduh Apache Maven 3.9.9 (~10 MB)...
set "MVN_ZIP=%TOOLS%\maven.zip"
powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; try { Invoke-WebRequest 'https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip' -OutFile '%MVN_ZIP%' -UseBasicParsing; exit 0 } catch { try { Invoke-WebRequest 'https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip' -OutFile '%MVN_ZIP%' -UseBasicParsing; exit 0 } catch { Write-Host \"ERROR: $_\"; exit 1 } }"
if errorlevel 1 goto :maven_fail
if not exist "%MVN_ZIP%" goto :maven_fail

echo        Mengekstrak Maven...
powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; Expand-Archive '%MVN_ZIP%' '%TOOLS%\mvn_tmp' -Force; $d=(Get-ChildItem '%TOOLS%\mvn_tmp' -Directory | Select-Object -First 1); if(Test-Path '%TOOLS%\maven'){Remove-Item '%TOOLS%\maven' -Recurse -Force}; Move-Item $d.FullName '%TOOLS%\maven'"
del "%MVN_ZIP%" >nul 2>&1
if exist "%TOOLS%\mvn_tmp" rmdir /s /q "%TOOLS%\mvn_tmp" >nul 2>&1
if not exist "%TOOLS%\maven\bin\mvn.cmd" goto :maven_fail
set "MVN_CMD=%TOOLS%\maven\bin\mvn.cmd"
set "MVN_HOME=%TOOLS%\maven"
echo        Maven 3.9.9 berhasil diinstal di: %TOOLS%\maven
goto :maven_ok

:maven_fail
echo  [ERROR] Gagal mendapatkan Maven. Instal dari: https://maven.apache.org
pause & exit /b 1

:maven_ok
set "PATH=%MVN_HOME%\bin;%PATH%"
echo        Maven OK  ^(%MVN_HOME%^)

:: ══════════════════════════════════════════════════════════════════════════
::  [3/4]  MYSQL
:: ══════════════════════════════════════════════════════════════════════════
echo.
echo  [3/4] Memeriksa MySQL / MariaDB...
set "MYSQLD_EXE="
set "MYSQL_CLI="
set "MYSQL_DATADIR="
set "MYSQL_INIFILE="

:: 3a. Sudah berjalan di port 3306?
netstat -an 2>nul | findstr ":3306 " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo        MySQL sudah berjalan di port 3306
    goto :mysql_find_cli
)

:: 3b. Cari mysqld.exe (Laragon - scan versi otomatis)
if exist "C:\laragon\bin\mysql" (
    for /d %%d in ("C:\laragon\bin\mysql\*") do (
        if not defined MYSQLD_EXE (
            if exist "%%d\bin\mysqld.exe" (
                set "MYSQLD_EXE=%%d\bin\mysqld.exe"
                set "MYSQL_CLI=%%d\bin\mysql.exe"
                :: Deteksi versi dari nama folder: mysql-8.4.x-winx64 → mysql-8.4
                for /f "tokens=1,2 delims=-" %%a in ("%%~nxd") do (
                    for /f "tokens=1,2 delims=." %%x in ("%%b") do (
                        set "MYSQL_DATADIR=C:\laragon\data\%%a-%%x.%%y"
                    )
                )
                if not exist "!MYSQL_DATADIR!" set "MYSQL_DATADIR=C:\laragon\data\mysql"
            )
        )
    )
)
if defined MYSQLD_EXE goto :mysql_start

:: 3c. XAMPP
if exist "C:\xampp\mysql\bin\mysqld.exe" (
    set "MYSQLD_EXE=C:\xampp\mysql\bin\mysqld.exe"
    set "MYSQL_CLI=C:\xampp\mysql\bin\mysql.exe"
    set "MYSQL_DATADIR=C:\xampp\mysql\data"
    goto :mysql_start
)

:: 3d. WampServer 64
for /d %%d in ("C:\wamp64\bin\mysql\*") do (
    if not defined MYSQLD_EXE (
        if exist "%%d\bin\mysqld.exe" (
            set "MYSQLD_EXE=%%d\bin\mysqld.exe"
            set "MYSQL_CLI=%%d\bin\mysql.exe"
            set "MYSQL_DATADIR=C:\wamp64\bin\mysql\%%~nxd\data"
        )
    )
)
if defined MYSQLD_EXE goto :mysql_start

:: 3e. MySQL terinstal di Program Files
for %%d in (
    "C:\Program Files\MySQL\MySQL Server 8.4"
    "C:\Program Files\MySQL\MySQL Server 8.0"
    "C:\Program Files\MySQL\MySQL Server 5.7"
) do (
    if not defined MYSQLD_EXE (
        if exist "%%~d\bin\mysqld.exe" (
            set "MYSQLD_EXE=%%~d\bin\mysqld.exe"
            set "MYSQL_CLI=%%~d\bin\mysql.exe"
        )
    )
)
if defined MYSQLD_EXE goto :mysql_try_service

:: 3f. MariaDB di Program Files
for %%d in (
    "C:\Program Files\MariaDB 11.4"
    "C:\Program Files\MariaDB 11.2"
    "C:\Program Files\MariaDB 10.11"
    "C:\Program Files\MariaDB 10.6"
) do (
    if not defined MYSQLD_EXE (
        if exist "%%~d\bin\mysqld.exe" (
            set "MYSQLD_EXE=%%~d\bin\mysqld.exe"
            set "MYSQL_CLI=%%~d\bin\mysql.exe"
        )
    )
)
if defined MYSQLD_EXE goto :mysql_try_service

:: 3g. Tools lokal (portable install sebelumnya)
if exist "%TOOLS%\mysql\bin\mysqld.exe" (
    set "MYSQLD_EXE=%TOOLS%\mysql\bin\mysqld.exe"
    set "MYSQL_CLI=%TOOLS%\mysql\bin\mysql.exe"
    set "MYSQL_DATADIR=%TOOLS%\mysql-data"
    set "MYSQL_INIFILE=%TOOLS%\mysql\my.ini"
    goto :mysql_start
)

:: 3h. Coba Windows Service MySQL / MariaDB
:mysql_try_service
for %%s in (MySQL80 MySQL84 MySQL MariaDB mysql mariadb) do (
    sc query %%s >nul 2>&1
    if not errorlevel 1 (
        echo        Memulai service: %%s
        net start %%s >nul 2>&1
        timeout /t 5 /nobreak >nul
        netstat -an 2>nul | findstr ":3306 " | findstr "LISTENING" >nul 2>&1
        if not errorlevel 1 goto :mysql_find_cli
    )
)
if defined MYSQLD_EXE goto :mysql_start

:: ── Tidak ditemukan sama sekali: Install MariaDB 11.4 LTS portable ────────
echo.
echo  [AUTO-INSTALL] MySQL/MariaDB tidak ditemukan.
echo        Mengunduh MariaDB 11.4 LTS portable (~75 MB)...
set "MY_ZIP=%TOOLS%\mariadb.zip"
powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; try { $url='https://archive.mariadb.org/mariadb-11.4.4/winx64-packages/mariadb-11.4.4-winx64.zip'; Write-Host \"  URL: $url\"; Invoke-WebRequest $url -OutFile '%MY_ZIP%' -UseBasicParsing; exit 0 } catch { Write-Host \"ERROR: $_\"; exit 1 }"
if errorlevel 1 goto :mysql_fail
if not exist "%MY_ZIP%" goto :mysql_fail

echo        Mengekstrak MariaDB...
powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; Expand-Archive '%MY_ZIP%' '%TOOLS%\mysql_tmp' -Force; $d=(Get-ChildItem '%TOOLS%\mysql_tmp' -Directory | Select-Object -First 1); if(Test-Path '%TOOLS%\mysql'){Remove-Item '%TOOLS%\mysql' -Recurse -Force}; Move-Item $d.FullName '%TOOLS%\mysql'"
del "%MY_ZIP%" >nul 2>&1
if exist "%TOOLS%\mysql_tmp" rmdir /s /q "%TOOLS%\mysql_tmp" >nul 2>&1
if not exist "%TOOLS%\mysql\bin\mysqld.exe" goto :mysql_fail

set "MYSQLD_EXE=%TOOLS%\mysql\bin\mysqld.exe"
set "MYSQL_CLI=%TOOLS%\mysql\bin\mysql.exe"
set "MYSQL_DATADIR=%TOOLS%\mysql-data"
set "MYSQL_INIFILE=%TOOLS%\mysql\my.ini"

:: Buat my.ini
echo        Membuat konfigurasi my.ini...
if not exist "%MYSQL_DATADIR%" mkdir "%MYSQL_DATADIR%"
(
echo [mysqld]
echo basedir=%TOOLS%\mysql
echo datadir=%MYSQL_DATADIR%
echo port=3306
echo character-set-server=utf8mb4
echo collation-server=utf8mb4_unicode_ci
echo sql_mode=NO_ENGINE_SUBSTITUTION
echo [client]
echo default-character-set=utf8mb4
) > "%TOOLS%\mysql\my.ini"

:: Inisialisasi database (hanya pertama kali)
echo        Menginisialisasi database MariaDB (sekali saja, ~15 detik)...
"%MYSQLD_EXE%" --defaults-file="%TOOLS%\mysql\my.ini" --initialize-insecure >nul 2>&1
echo        Inisialisasi selesai.
echo        MariaDB 11.4 berhasil diinstal di: %TOOLS%\mysql

:mysql_start
:: Mulai mysqld
echo        Memulai MySQL server...
if defined MYSQL_INIFILE (
    start "" /B "%MYSQLD_EXE%" --defaults-file="%MYSQL_INIFILE%"
) else if defined MYSQL_DATADIR (
    start "" /B "%MYSQLD_EXE%" --datadir="%MYSQL_DATADIR%" --port=%DB_PORT% --console
) else (
    start "" /B "%MYSQLD_EXE%" --port=%DB_PORT% --console
)
echo        Menunggu MySQL siap ^(12 detik^)...
timeout /t 12 /nobreak >nul

netstat -an 2>nul | findstr ":3306 " | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo  [WARN] MySQL belum terdeteksi di port 3306.
    echo         Melanjutkan — pastikan database siap sebelum akses aplikasi.
    goto :mysql_done
)
echo        MySQL OK

:mysql_find_cli
:: Cari mysql.exe jika belum terdeteksi (MySQL sudah jalan dari luar)
if defined MYSQL_CLI goto :mysql_create_db
for %%d in (
    "C:\laragon\bin\mysql"
    "C:\xampp\mysql\bin"
    "C:\Program Files\MySQL\MySQL Server 8.4\bin"
    "C:\Program Files\MySQL\MySQL Server 8.0\bin"
    "C:\Program Files\MariaDB 11.4\bin"
    "%TOOLS%\mysql\bin"
) do (
    if not defined MYSQL_CLI (
        if exist "%%~d\mysql.exe" set "MYSQL_CLI=%%~d\mysql.exe"
    )
)
if not defined MYSQL_CLI (
    where mysql >nul 2>&1
    if not errorlevel 1 for /f "delims=" %%i in ('where mysql 2^>nul') do if not defined MYSQL_CLI set "MYSQL_CLI=%%i"
)

:mysql_create_db
:: Buat database jika belum ada
if defined MYSQL_CLI (
    "%MYSQL_CLI%" -u %DB_USER% --password=%DB_PASS% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul
    if not errorlevel 1 (
        echo        Database '%DB_NAME%' siap.
    ) else (
        echo  [WARN] Tidak dapat membuat database otomatis. Buat manual jika belum ada:
        echo         mysql -u root -e "CREATE DATABASE %DB_NAME%;"
    )
)

:mysql_done
goto :build_jar

:mysql_fail
echo.
echo  [ERROR] Gagal mendapatkan MySQL/MariaDB.
echo          Instal XAMPP/Laragon atau MariaDB dari: https://mariadb.org/download
pause & exit /b 1

:: ══════════════════════════════════════════════════════════════════════════
::  [4/4]  BUILD + RUN
:: ══════════════════════════════════════════════════════════════════════════
:build_jar
echo.
echo  [4/4] Memeriksa file JAR...
if not exist "%APP_JAR%" (
    echo        JAR belum ada — memulai build Maven...
    echo        ^(Proses ini memerlukan koneksi internet pertama kali^)
    echo.
    call "%MVN_CMD%" package -DskipTests -f "%BASE%pom.xml"
    if errorlevel 1 (
        echo.
        echo  [ERROR] Build Maven gagal. Periksa log di atas.
        pause & exit /b 1
    )
    echo.
    if not exist "%APP_JAR%" (
        echo  [ERROR] Build selesai tapi JAR tidak ditemukan: %APP_JAR%
        pause & exit /b 1
    )
    echo        Build selesai.
) else (
    echo        JAR ditemukan
)

echo.
echo  =============================================
echo  Aplikasi berjalan di : http://localhost:8080
echo  Login default        : admin / admin123
echo  Tekan Ctrl+C untuk menghentikan.
echo  =============================================
echo.

"%JAVA_EXE%" -jar "%APP_JAR%"

echo.
echo  Aplikasi dihentikan.
pause
