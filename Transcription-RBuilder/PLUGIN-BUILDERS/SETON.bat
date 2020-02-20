@ECHO OFF

set CLASSPATH_=dist\SETON.jar
set CLASSPATH_=%CLASSPATH_%;dist\lib\*
set MAIN_CLASS=com.jomac.transcription.referencebuilder.Main

java -Xms64m -Xmx256m -cp %CLASSPATH_% %MAIN_CLASS%