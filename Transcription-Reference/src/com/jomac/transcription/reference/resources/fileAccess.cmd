@ECHO OFF
echo "START FILE OPENER"
SETLOCAL
SET BATCHHOME=%1
SET HOME=%HOMEDRIVE%%HOMEPATH%
SET PATH=%BATCHHOME%;%PATH%

SET FILEPATH=%2
CALL %FILEPATH%
exit /b