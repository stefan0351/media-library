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
echo set CLASSPATH=media-core.jar;media-gui.jar> classpath.bat
set CLASSPATH=media-core.jar;media-gui.jar
for %%f in (lib\*.jar) do echo set CLASSPATH=%%CLASSPATH%%;%%f>> classpath.bat
call classpath.bat
del classpath.bat

start "MediaLib" "%JAVA_HOME%\bin\java.exe" -cp %CLASSPATH% com.kiwisoft.media.MediaManager
goto end

:error
pause

:end
