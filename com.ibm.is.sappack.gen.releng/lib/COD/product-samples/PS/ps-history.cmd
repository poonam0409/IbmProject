@cls
@echo off
setlocal

set     ID=sdiering
set MODULE=ps-web
set   YEAR=2004
set PREFIX=/psv5/

set   TDIR=$$history$$

set CVSROOT=%id%@cvs.opensource.ibm.com:/cvsroot/%MODULE%
set CVS_RSH=%ProgramFiles%\PuTTY\plink.exe

echo ***************************************************************************
echo * Generate COD -filelist file from module's CVS history
echo *
echo * - CVSROOT.............%CVSROOT%
echo * - Year................%YEAR%
echo * - File name prefix....%PREFIX%
echo ***************************************************************************
echo.
        
set path=
set path=C:\dev\WinCvs\1.3\CVSNT;%path%
set path=C:\dev\cygwin\bin;%path%

if not exist %TDIR% mkdir       %TDIR%
                    del   /q /f %TDIR%\*.*
@echo on

@echo.
@echo -----------------------------------------------------
@echo * Get module's CVS history
@echo -----------------------------------------------------

cvs  history -c -a -D "%YEAR%-01-01 00:00:00 EST" > %TDIR%\cvs

@echo.
@echo -----------------------------------------------------
@echo * Convert CVS format to required COD file format
@echo -----------------------------------------------------

gawk '$8 !~ /CVSROOT/ {print $1,"%PREFIX%"$8"/"$7}' < %TDIR%\cvs >%TDIR%\unix-list

@echo.
@echo -----------------------------------------------------
@echo * Convert Unix file names to Windows format 
@rem  * 
@rem  * Note: Java seems to "tolerate" Unix format...
@echo -----------------------------------------------------

tr / \\ <%TDIR%\unix-list >%TDIR%\cod-list

@echo.
@echo -----------------------------------------------------
@echo * COD -filelist file generated
@echo -----------------------------------------------------

wc -l %TDIR%\cod-list

@echo off

endlocal
