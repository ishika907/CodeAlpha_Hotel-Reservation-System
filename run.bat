@echo off
if not exist bin\com\hotel\Main.class (
    call compile.bat
    if %ERRORLEVEL% NEQ 0 exit /b 1
)

echo Starting Hotel Reservation System...
java -cp bin com.hotel.Main
