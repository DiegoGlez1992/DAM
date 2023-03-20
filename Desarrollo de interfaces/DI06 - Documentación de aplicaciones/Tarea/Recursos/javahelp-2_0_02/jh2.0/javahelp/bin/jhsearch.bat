@echo off

if not "%OS%"=="Windows_NT" goto win9xStart
:winNTStart
@setlocal

rem %~dp0 is name of current script under NT
set DEFAULT_JAVAHELP_HOME=%~dp0

rem : operator works similar to make : operator
set DEFAULT_JAVAHELP_HOME=%DEFAULT_JAVAHELP_HOME:\javahelp\bin\=%

if %JAVAHELP_HOME%a==a set JAVAHELP_HOME=%DEFAULT_JAVAHELP_HOME%
set DEFAULT_JAVAHELP_HOME=
goto doneStart

:win9xStart
:doneStart
rem This label provides a place for NT handling to skip to.

rem find JAVAHELP_HOME
if not "%JAVAHELP_HOME%"=="" goto runjhsearch

rem check for JavaHelp in Program Files on system drive
if not exist "%SystemDrive%\Program Files\jh" goto checkSystemDrive
set JAVAHELP_HOME=%SystemDrive%\Program Files\jh
goto checkJava

:checkSystemDrive
rem check for JavaHelp in root directory of system drive
if not exist "%SystemDrive%\jh" goto noJavaHelpHome
set JAVA_HOME=%SystemDrive%\jh
goto runjhsearch

:noJavaHelpHome
echo JAVAHELP_HOME is not set and JavaHelp could not be located. Please set JAVAHELP_HOME.
goto end

:runjhsearch
java -jar %JAVAHELP_HOME%\javahelp\bin\jhsearch.jar %1

if not "%OS%"=="Windows_NT" goto mainEnd
:winNTend
@endlocal

:mainEnd



