@echo off

:: Установка переменных
set "ROOT_DIR=..\"
set "WGET=%ROOT_DIR%program\wget.exe"
set "ZIP=%ROOT_DIR%program\7z.exe"
set "EXTRACT_DIR=%ROOT_DIR%temp"
set "INSTALLER=%ROOT_DIR%temp\DjVuLibre-3.5.28_DjView-4.12_Setup.exe"
set "TARGET_DIR=%ROOT_DIR%program\djvu"
set "URL=https://sourceforge.net/projects/djvu/files/DjVuLibre_Windows/3.5.28%%2B4.12/DjVuLibre-3.5.28_DjView-4.12_Setup.exe/download"

:: Создаем временную папку
mkdir %ROOT_DIR%\temp

:: Скачивание установщика
echo Load DjView...
"%WGET%" --no-check-certificate -O "%ROOT_DIR%temp\%INSTALLER%" "%URL%"
if errorlevel 1 (
    echo Error load file
    exit /b 1
)

:: Распаковка архива
echo Unzip package...
"%ZIP%" x -o"%EXTRACT_DIR%" "%INSTALLER%" -y >nul
if errorlevel 1 (
    echo Error unzip package
    exit /b 1
)

:: Создание целевой директории
if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"

:: Поиск и копирование файлов
for %%F in (libz.dll ddjvu.exe libdjvulibre.dll libjpeg.dll libtiff.dll) do (
    echo Copy %%F...
    for /r "%EXTRACT_DIR%" %%I in (%%F) do (
        if exist "%%I" copy /y "%%I" "%TARGET_DIR%\" >nul
    )
)

:: Удаление временных файлов
rmdir /s /q "%EXTRACT_DIR%"
del /q "%INSTALLER%"
del /q "%ROOT_DIR%program\.wget-hsts"
echo Complete!