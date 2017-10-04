setlocal
rem
rem Licensed Material - Property of IBM
rem
rem 5724-Q55
rem
rem (c) Copyright IBM Corp. 2011  All Rights Reserved.
rem
rem US Government Users Restricted Rights - Use, duplication or disclosure
rem restricted by GSA ADP Schedule Contract with IBM Corp.
rem


rem Usage: sapprereqcheck.bat <JavaExecutable> <IISHOME> client|engine <OutputLogFile> <ErrorFile>
set RC=0
%1 -classpath prereqcheck.jar com.ibm.is.sappack.dsstages.install.PrereqCheck %2 %3 %4 %5
set RC=%ERRORLEVEL%
@echo "Command line: %1 -classpath prereqcheck.jar com.ibm.is.sappack.dsstages.install.PrereqCheck %2 %3 %4 %5"  >> %4
@echo Return code = %RC%  >> %4
exit %RC%
