@ECHO OFF
SETLOCAL

WHERE mvn >NUL 2>&1
IF ERRORLEVEL 1 (
  ECHO Maven is not installed or not available on PATH.
  EXIT /B 1
)

mvn %*
EXIT /B %ERRORLEVEL%