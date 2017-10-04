<?xml version='1.0' encoding='UTF-8'?>
<!--
Licensed Materials - Property of IBM
 
5724-Q55

(c) Copyright IBM Corp. 2009, 2011  All Rights Reserved.

US Government Users Restricted Rights - Use, duplication or disclosure
restricted by GSA ADP Schedule Contract with IBM Corp.
-->
<?metadata version='0.0.4'?>
<assembly id='com.ibm.is.sappack.rapid.modeler.main' version='8.0.0.qualifier'>
  <selector id='installContext.native'/>
  <selector id='installContext.eclipse'/>
  <selector id='main'>
    <internalSelection id='main.eclipse'/>
    <internalSelection id='main.native'/>
  </selector>
  <selector id='main.eclipse'>
    <internalSelection id='installContext.eclipse'/>
    <requiredShareableEntity shareableId='com.ibm.sdp.eclipse.ide' tolerance='[1.0.0,2.0.0)' installContext='true'>
      <requiredSelector selectorId='installContext'/>
    </requiredShareableEntity>
  </selector>
  <selector id='main.native'>
    <internalSelection id='installContext.native'/>
    <requiredShareableEntity shareableId='com.ibm.sdp.native' tolerance='[1.0.0,2.0.0)' installContext='true'>
      <requiredSelector selectorId='installContext'/>
    </requiredShareableEntity>
  </selector>
  <!-- just in case we ever want to redistribute an IBM JDK. Currently not used. -->
  <selector id='jdk'>
    <internalSelection id='installContext.eclipse'/>
    <requiredShareableEntity shareableId='com.ibm.sdp.eclipse.ide' tolerance='[1.0.0,2.0.0)' installContext='true'>
      <requiredSelector selectorId='installContext'/>
    </requiredShareableEntity>
  </selector>
  <!--
  <includedShareableEntity id='com.ibm.is.sappack.gen.branding.id' version='8.0.0.qualifier' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main.eclipse'/>
    </includedSelector>
  </includedShareableEntity>
  -->
  <includedShareableEntity id='com.ibm.sdp.eclipse.ide' version='1.0.1' tolerance='[1.0.0,2.0.0)'>
    <includedSelector selectorId='installContext'>
      <selectedBy id='installContext.eclipse'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.sdp.native' version='1.0.1.20061017' tolerance='[1.0.0,2.0.0)'>
    <includedSelector selectorId='installContext'>
      <selectedBy id='installContext.native'/>
    </includedSelector>
  </includedShareableEntity>
  <!-- Native portions of the Rapid Modeler -->
  <includedShareableEntity id='com.ibm.is.sappack.rapid.modeler.native' version='8.0.0.qualifier' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main.native'/>
    </includedSelector>
  </includedShareableEntity>
  <!-- Eclipse-specific portions of the Rapid Modeler -->
  <includedShareableEntity id='com.ibm.is.sappack.rapid.modeler.eclipse' version='8.0.0.qualifier' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main.eclipse'/>
    </includedSelector>
  </includedShareableEntity>
  <!-- Create Windows/Linux shortcuts allowing to start the product -->
  <includedShareableEntity id='com.ibm.is.sappack.rapid.modeler.shortcuts' version='8.0.0.qualifier' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main.native'/>
    </includedSelector>
  </includedShareableEntity>
  <!-- Add an Uninstall link into the Windows Registry to make it available from Add/Remove Programs -->
  <!--
  <includedShareableEntity id='com.ibm.rational.cdi.uninstaller' version='1.0.4.20081006' tolerance='[1.0.4,2.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main.native'/>
    </includedSelector>
  </includedShareableEntity>
  -->
  <!-- Remove features and plugins directories from install Location  -->
  <!--
  <includedShareableEntity id='com.ibm.rational.cdi.uninstall.cleanup' version='1.0.0.20061108' tolerance='[1.0.0,2.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main.eclipse'/>
    </includedSelector>
  </includedShareableEntity>
  -->
</assembly>
