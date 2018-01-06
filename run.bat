@ECHO OFF
setlocal ENABLEDELAYEDEXPANSION
SET LIB_PATH=lib
SET ARTIFACTS_PATH=artifacts
SET LOAD_PATH=
FOR %%A IN (%LIB_PATH%\*.jar) DO SET LOAD_PATH=!LOAD_PATH!;"%%A"
FOR %%A IN (%ARTIFACTS_PATH%\*.jar) DO SET LOAD_PATH=!LOAD_PATH!;"%%A"
echo "run application with class path:\n"%LOAD_PATH%
java -Xmx1024m -classpath %LOAD_PATH% com.my.home.Runner -Dlog4j.configurationFile=log4j2.xml%*
SET LOAD_PATH=
SET LIB_PATH=