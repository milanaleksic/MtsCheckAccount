@echo off

cd prog

set JAVA_APLIKACIJA=javaw.exe

start "%JAVA_APLIKACIJA%" -classpath CheckPostpaidAccount-0.2.jar;RXTXcomm.jar;groovy-all-1.6.2.jar rs.in.milanaleksic.Startup

pause