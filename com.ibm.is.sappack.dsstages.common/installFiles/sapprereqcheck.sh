#
# Licensed Material - Property of IBM
#
# 5724-Q55
#
# (c) Copyright IBM Corp. 2011  All Rights Reserved.
#
# US Government Users Restricted Rights - Use, duplication or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.
#

showUsage()
{
   echo "Usage: ${0##*/} <JavaExecutable> <IISHOME> engine <OutputLogFile> <ErrorFile>"
   echo

   exit 2
}


if [ $# -lt 5 ]
then
   echo
   echo "Invalid number of required arguments !!"
   echo
   showUsage
fi


RC=0 ; export RC
OS=`uname`
JFLAGS=

if [ "$OS" = "SunOS" ]
then
   JFLAGS=-d64
fi

"$1" $JFLAGS -classpath prereqcheck.jar com.ibm.is.sappack.dsstages.install.PrereqCheck "$2" "$3" "$4" "$5"
RC=$?
echo "Parameters: $1 -classpath prereqcheck.jar com.ibm.is.sappack.dsstages.install.PrereqCheck $2 $3 $4 $5"  >> $4
echo Return code = $RC  >> $4
exit $RC
