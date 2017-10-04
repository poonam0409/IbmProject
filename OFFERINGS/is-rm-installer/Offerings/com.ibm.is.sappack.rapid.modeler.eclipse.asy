<?xml version='1.0' encoding='UTF-8'?>
<!--
Licensed Materials - Property of IBM
 
5724-Q55

(c) Copyright IBM Corp. 2009, 2011  All Rights Reserved.

US Government Users Restricted Rights - Use, duplication or disclosure
restricted by GSA ADP Schedule Contract with IBM Corp.
-->
<?metadata version='0.0.4'?>
<assembly id='com.ibm.is.sappack.rapid.modeler.eclipse' version='8.0.0.qualifier'>
  <selector id='main'>
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
  <includedShareableEntity id='com.ibm.is.3rdparty.sap.feature' version='0.0.0' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.gen.common.feature' version='0.0.0' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.gen.plugins.feature' version='0.0.0' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.gen.tools.metadata.feature' version='0.0.0' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.gen.oda.feature' version='0.0.0' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <!--
  <includedShareableEntity id='com.ibm.is.sappack.gen.tools.mihmodel.feature' version='0.0.0' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  -->
  <includedShareableEntity id='com.ibm.is.sappack.rapid.modeler.config' version='8.0.0.qualifier' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
  <includedShareableEntity id='com.ibm.is.sappack.rapid.modeler.vmargs' version='8.0.0.qualifier' tolerance='[8.0.0,9.0.0)'>
    <includedSelector selectorId='main'>
      <selectedBy id='main'/>
    </includedSelector>
    <includedSelector selectorId='setvm'>
      <selectedBy id='main'/>
    </includedSelector>
  </includedShareableEntity>
</assembly>
