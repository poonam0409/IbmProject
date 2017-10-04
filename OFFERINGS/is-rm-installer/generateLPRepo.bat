@echo off
rem
rem Licensed Material - Property of IBM
rem
rem 5724-Q55
rem
rem (c) Copyright IBM Corp. 2009, 2011  All Rights Reserved.
rem 
rem US Government Users Restricted Rights - Use, duplication or disclosure
rem restricted by GSA ADP Schedule Contract with IBM Corp.
rem
set JAVA_HOME=C:\java\IBM\Java60
set PKGDEV_HOME="C:\Program Files\IBM\Package Developer"

%JAVA_HOME%\bin\java ^
	-jar %PKGDEV_HOME%\plugins\org.eclipse.equinox.launcher_1.1.0.v20100307.jar ^
	-application com.ibm.eec.launchpad.headless.exporter ^
	-data C:\projects\workspaces\admin-client-installer ^
	-project ibm-admin-client-lpad ^
	-exportLocation C:\ac-lpad