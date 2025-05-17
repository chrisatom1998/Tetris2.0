@echo off
set DIR=%~dp0
set JAVA_HOME=%JAVA_HOME%
"%JAVA_HOME%\bin\java" -cp "%DIR%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
