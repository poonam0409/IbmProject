<?xml version='1.0' encoding='UTF-8'?>
<!--
Licensed Materials - Property of IBM
 
5724-Q55

(c) Copyright IBM Corp. 2009, 2011  All Rights Reserved.

US Government Users Restricted Rights - Use, duplication or disclosure
restricted by GSA ADP Schedule Contract with IBM Corp.
-->
<?metadata version='0.0.4'?>
<assembly id='com.ibm.is.sappack.conversion.workbench.eclipse' version='1.1.0.qualifier'>
  <selector id='installContext.eclipse'/>
  <selector id='main'>
    <internalSelection id='installContext.eclipse'/>
    <requiredShareableEntity shareableId='com.ibm.sdp.eclipse.ide' tolerance='[1.0.0,2.0.0)' installContext='true'>
      <requiredSelector selectorId='installContext'/>
    </requiredShareableEntity>
    <requiredShareableEntity shareableId='org.eclipse.emf.feature' tolerance='[2.4.0,3.0.0)'>
      <requiredSelector selectorId='main'/>
    </requiredShareableEntity>
    <requiredShareableEntity shareableId='org.eclipse.help.feature' tolerance='[1.0.0,2.0.0)'>
      <requiredSelector selectorId='main'/>
    </requiredShareableEntity>
    <requiredShareableEntity shareableId='org.eclipse.platform.feature' tolerance='3.4.0'>
      <requiredSelector selectorId='main'/>
    </requiredShareableEntity>
    <requiredShareableEntity shareableId='org.eclipse.rcp.feature' tolerance='[3.4.0,4.0.0)'>
      <requiredSelector selectorId='main'/>
    </requiredShareableEntity>
    <requiredShareableEntity shareableId='com.ibm.datatools.core.feature' tolerance='[1.0.0,2.0.0)'>
      <requiredSelector selectorId='main'/>
    </requiredShareableEntity>
  </selector>
  <includedShareableEntity id='com.ibm.is.3rdparty.sap.feature' version='0.0.0' tolerance='[7.0.0,8.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.gen.common.feature' version='0.0.0' tolerance='[7.0.0,8.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.cw.tools.generator.feature' version='0.0.0' tolerance='[1.0.0,2.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.cw.tools.modeler.feature' version='0.0.0' tolerance='[1.0.0,2.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.cw.plugins.feature' version='0.0.0' tolerance='[1.0.0,2.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.cw.tools.metadata.feature' version='0.0.0' tolerance='[1.0.0,2.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.conversion.workbench.config' version='1.1.0.qualifier' tolerance='[1.0.0,2.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.conversion.workbench.vmargs' version='1.1.0.qualifier' tolerance='[1.0.0,2.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
    <includedSelector selectorId='setvm'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
</assembly>
