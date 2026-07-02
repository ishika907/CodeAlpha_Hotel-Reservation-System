@echo off
echo Compiling Hotel Reservation System...

if not exist bin mkdir bin

javac -d bin -sourcepath src src\com\hotel\Main.java src\com\hotel\HotelApp.java src\com\hotel\model\*.java src\com\hotel\service\*.java src\com\hotel\storage\*.java src\com\hotel\util\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    exit /b 1
)

echo Compilation successful.
echo Run the project using: run.bat
