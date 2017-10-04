#
# Licensed Material - Property of IBM
#
# 5724-Q55
#
# (c) Copyright IBM Corp. 2011, 2012  All Rights Reserved.
#
# US Government Users Restricted Rights - Use, duplication or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.
#

#
#Comment in the following line and set JAVA_EXE to the absolute path to the java executable if script shows error
# NOTE: On Windows use forward slashes as path delimiter, e.g. JAVA_EXE=c:/Java/java.exe
#export JAVA_EXE=/opt/IBM/InformationServer/ASBNode/apps/jre/bin/java



###################################################
# DO NOT MODIFY ANYTHING BELOW THIS LINE

if [ "$#" -ne 2 ]
then
   echo
   echo "Usage: prereqcheck <ISHomeDir> client|engine"
   echo
   exit 1
fi


if ! [ -d "$1" ]
then
  echo
  echo Directory \'"$1"\' does not exist
  echo
  exit 1
fi


OS=`uname`
JFLAGS=

IIS_JAVAEXE="$1"/ASBNode/apps/jre/bin/java
   
if [ "$OS" = "SunOS" ]
then
   JFLAGS=-d64
   IIS_JAVAEXE="$1"/ASBNode/apps/jre/jre/lib/sparcv9/server/java
fi

if [ "$OS" = "Windows_NT" ] 
then
   IIS_JAVAEXE="$1"/ASBNode/apps/jre/bin/java.exe
fi


if [ -z "$JAVA_EXE" ]
then
  export JAVA_EXE="$IIS_JAVAEXE"
fi 


if ! [ -f "$JAVA_EXE" ];
then 
   echo "Java executable $JAVA_EXE does not exist. Please modify variable JAVA_EXE in prereqcheck.sh script"
   echo
   exit 
fi


$JAVA_EXE -jar sappackv7prereqcheck.jar $JFLAGS $* 

