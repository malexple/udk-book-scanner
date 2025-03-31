@echo off

:: Пути
set "ROOT_DIR=..\"
set "WGET=%ROOT_DIR%program\wget.exe"
set "TARGET_DIR=%ROOT_DIR%program\tesseract"
set "TESSDATA_DIR=%TARGET_DIR%\tessdata"

:: 1. Создаем папку program
if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"

:: 2. Создаем папку для языковых пакетов
if not exist "%TESSDATA_DIR%" mkdir "%TESSDATA_DIR%"

:: 3. Скачиваем языковые пакеты (ru и en)
echo Load russian language model...
"%WGET%" -O "%TESSDATA_DIR%\rus.traineddata" "https://github.com/tesseract-ocr/tessdata/raw/main/rus.traineddata"

echo Load english language model...
"%WGET%" -O "%TESSDATA_DIR%\eng.traineddata" "https://github.com/tesseract-ocr/tessdata/raw/main/eng.traineddata"

del /q "%ROOT_DIR%program\.wget-hsts"

echo Complete! Language model download in:
echo %TARGET_DIR%