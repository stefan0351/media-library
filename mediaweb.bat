@echo off

%~d0
cd %~p0

call setenv.bat

if "%JAVA_HOME%"=="" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto invalidJavaHome
goto start

:noJavaHome
echo JAVA_HOME not set.
goto error

:invalidJavaHome
echo java.exe not found in %JAVA_HOME%\bin
goto error

:start
cd tomcat\bin
call startup.bat
start http://localhost:8080/media/shows/index.jsp
goto end

:error
pause

:end
