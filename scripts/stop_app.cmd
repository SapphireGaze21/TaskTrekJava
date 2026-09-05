@echo off
call "%~dp0stop-app.cmd" %*
exit /b %ERRORLEVEL%
