######################################################################
# IBM Confidential
#
# OCO Source Materials
#
# 5724-Q55
#
# (C) Copyright IBM Corporation 2013
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
######################################################################

######################################################################
#
# Conversion Workbench Application
# Automated WebSphere Configuration & Installation Script
# Requires: CWAppInstallConfig.prop (property file)
#
# Tested with the following Websphere Application Server Versions:
# WAS 7 and 8.5
#
# WAS documentation:
# http://pic.dhe.ibm.com/infocenter/wasinfo/v7r0/index.jsp
# http://pic.dhe.ibm.com/infocenter/wasinfo/v8r5/index.jsp
#
######################################################################
#
# REQUIRED VALUE: Location of the property file

propertiesFile = "C:\cw4sap\CWAppInstallConfig.prop"

######################################################################


####################### Imports ######################################

from java import lang
from java import util
from java import io
import string
import sys

####################### Required functions ###########################

def loadProperties (source):
    """ Load a Java properties file into a Dictionary. """
    result = {}
    if type(source) == type(''):    # name provided, use file
        source = io.FileInputStream(source)
    bis = io.BufferedInputStream(source)
    props = util.Properties()
    props.load(bis) 
    bis.close()
    for key in props.keySet().iterator():
        result[key] = props.get(key).strip()
    return result

def getProperty (properties, name, default=""):
    """ Gets a property. """
    return properties.get(name, default)

def removePropertyFromWCC(wccName, propertyName):
	server = AdminConfig.getid("/Node:" + nodeName + "/Server:" + serverName + "/")
	tcs = AdminConfig.list("TransportChannelService", server)
	list = AdminConfig.list("WebContainerInboundChannel", tcs).split('\n')	
	for listElement in list[:]:
		listElement = listElement.strip()
		if AdminConfig.showAttribute(listElement, "name") == wccName:
			
			webmsgenabled = AdminConfig.showAttribute(listElement, "properties")
			
			# if there are no properties, AdminConfig.showAttribute(...) returns empty brackets "[]"
			if len(webmsgenabled) > 2:	
				print "Found the following WebContainerInboundChannels under " + wccName + ":"
				# possible value of webmsgenabled: "[webmsgenabled(cells/BL3AED4CNode06Cell/nodes/BL3AED4CNode06/servers/server1|server.xml#Property_1361458218484)]"
				# possible value if there are two channels: "[webmsgenabled(cells/BL3AED4CNode06Cell/nodes/BL3AED4CNode06/servers/server1|server.xml#Property_1361458218484) exampleChannel(cells/BL3AED4CNode06Cell/nodes/BL3AED4CNode06/servers/server1|server.xml#Property_1361459359859)]"
				removeLeftBracket = webmsgenabled.split('[')
				# possible value of array removeLeftBracket: ",webmsgenabled(cells/BL3AED4CNode06Cell/nodes/BL3AED4CNode06/servers/server1|server.xml#Property_1361458218484)]"
				removeRightBracket = removeLeftBracket[1].split(']')
				# possible value of array removeRightBracket: "webmsgenabled(cells/BL3AED4CNode06Cell/nodes/BL3AED4CNode06/servers/server1|server.xml#Property_1361458218484),"
				
				# the following line is only useful if there are multiple channels since they would have to be split up
				wccList = removeRightBracket[0].split(' ')
				for wcc in wccList[:]:
					wcc = wcc.strip()
					
					# split string where the bracket is, wcc looks like: webmsgenabled(cells/BL3AED4CNode06Cell/nodes/BL3AED4CNode06/servers/server1|server.xml#Property_1361458218484)
	    				# this way you can "filter" for the name and compare it to the given name
		    			wccSubstring = wcc.split('(')
		    			if wccSubstring[0] == "webmsgenabled":
		    				print wccSubstring[0]
		    				print "Removing wcc: " + wcc
						AdminConfig.remove('"' + wcc + '"')
						AdminConfig.save()
					else :
						print wccSubstring[0]
			else :
				print "No WebcontainerInboundChannels found under " + wccName + "."

def createPropertyInWCC(wccName, propertyName):
	createProperty = ""
	server = AdminConfig.getid("/Node:" + nodeName + "/Server:" + serverName + "/")
	tcs = AdminConfig.list("TransportChannelService", server)
	list = AdminConfig.list("WebContainerInboundChannel", tcs).split('\n')	
	for listElement in list[:]:
		listElement = listElement.strip()
		if AdminConfig.showAttribute(listElement, "name") == wccName:
	    		webmsgenabled = AdminConfig.showAttribute(listElement, "properties")
			if len(webmsgenabled) > 2:
				removeLeftBracket = webmsgenabled.split('[')
				removeRightBracket = removeLeftBracket[1].split(']')
				wccList = removeRightBracket[0].split(' ')
				for wcc in wccList[:]:
					wcc = wcc.strip()
	    				wccSubstring = wcc.split('(')
	    				createProperty = "true"
	    				if wccSubstring[0] == propertyName:
	    					print "WebContainerInboundChannel already exists in " + wccName + "."
	    					createProperty = "false"
						break		
			else :
				createProperty = "true"
				
			if createProperty == "true":
			    	
			    	# parameters are type, parent, attributes
				newProp = AdminConfig.create(\
				'Property', \
   				'"' + listElement + '"', \
   				'[[validationExpression ""] [name "webmsgenabled"] [description ""] [value "true"] [required "false"]]')
				AdminConfig.save()
				print "Created " + newProp
				print "Saving Web container inbound channel (" + wccName + ") configurations."	

# dsId = Data source object ID
def removeDatasource(dsId):
	
	# Find and remove the CMPConectorFactory associated with the data source to remove
	cmpConnectionFactories = AdminConfig.list("CMPConnectorFactory").split('\n')
	for cmpConnectionFactory in cmpConnectionFactories[:]:
		cmpConnectionFactory = cmpConnectionFactory.strip()
		if AdminConfig.showAttribute(cmpConnectionFactory, "cmpDatasource") == dsId:
			AdminConfig.remove('"' + cmpConnectionFactory + '"')
		 	print "Removing ConnectionFactory: " + cmpConnectionFactory
	
	# remove the actual data source
	AdminConfig.remove('"' + dsId + '"')
	print "Removing data source: " + dsId
		 	
# Data source object ID
def removeConnectionFactory(dsId):
	# Find and remove the CMPConectorFactory associated with the data source to remove
	cmpConnectionFactories = AdminConfig.list("CMPConnectorFactory").split('\n')
	for cmpConnectionFactory in cmpConnectionFactories[:]:
		cmpConnectionFactory = cmpConnectionFactory.strip()
		if AdminConfig.showAttribute(cmpConnectionFactory, "cmpDatasource") == dsId:
			AdminConfig.remove('"' + cmpConnectionFactory + '"')
		 	print "Removing ConnectionFactory: " + cmpConnectionFactory

# params:
# oracleDatabaseUrl only used for oracle
# resProp variables only used for db2
# jdbcProvider, datasourceName, datasourceJndiName, nonTransactionalDatasource used for oracle and db2
def createDatasource(jdbcProvider, datasourceName, datasourceJndiName, resProp_dbName, resProp_dbDriverType, resProp_serverName, resProp_portNumber, oracleDatabaseUrl, nonTransactionalDatasource):
		datasourceId = AdminConfig.getid('/DataSource:' + datasourceName + '/')
		resourceProperties = ""
		
		if jdbcProviderDatabaseType == ORACLE_STRING:
			# configures the resource properties that are required by the data source
			resourceProperties = '[[URL java.lang.String ' + oracleDatabaseUrl + ']]'

		elif jdbcProviderDatabaseType == DB2_STRING:
			resourceProperties = '[[databaseName java.lang.String ' + resProp_dbName + '][driverType java.lang.Integer ' + resProp_dbDriverType + '][serverName java.lang.String ' + resProp_serverName + ' ][portNumber java.lang.Integer ' + resProp_portNumber + ']]'

		else :
			print "Unknown value for databaseType in property file. Please choose either '" + DB2_STRING + "' or '" + ORACLE_STRING + "'."
			sys.exit()
		
		AdminTask.createDatasource(\
	   		'"' + jdbcProvider + '"', \
	   		'[-name ' + datasourceName + ' -jndiName ' + datasourceJndiName + ' -dataStoreHelperClassName ' + ds_dataStoreHelperClassName + ' -containerManagedPersistence ' + ds_containerManagedPersistence + ' -componentManagedAuthenticationAlias ' + ds_componentManagedAuthenticationAlias + ' -xaRecoveryAuthAlias ' + ds_xaRecoveryAuthAlias + ' -configureResourceProperties ' + resourceProperties + ' ]')
		
		print "Database Type: " + jdbcProviderDatabaseType.upper() + ", Datastore Helper Class: " + ds_dataStoreHelperClassName + "."
		print "Properties: " + resourceProperties
		
		print "Modifying CMPConnectorFactory."
	    	
		datasourceId = AdminConfig.getid('/DataSource:' + datasourceName + '/')
		cf_name = datasourceName + "_CF"
		# e.g. "BL3AED4CNode02/CWDBUser"
		cf_authDataAlias = nodeName + "/" + authAlias
		# e.g. "BL3AED4CNode02/CWDBUser"
		cf_xaRecoveryAuthAlias = nodeName + "/" + authAlias
			
		cmpConnectionFactories = AdminConfig.list("CMPConnectorFactory").split('\n')
		for cmpConnectionFactory in cmpConnectionFactories[:]:
		    cmpConnectionFactory = cmpConnectionFactory.strip()
		  
		    if AdminConfig.showAttribute(cmpConnectionFactory, "cmpDatasource") == datasourceId:
		    	globCmpConnectionFactory = cmpConnectionFactory
		    	AdminConfig.modify(\
		   	'"' + cmpConnectionFactory + '"', \
		   	'[[name "' + cf_name + '"] [authDataAlias "' + cf_authDataAlias + '"] [xaRecoveryAuthAlias "' + cf_xaRecoveryAuthAlias + '"]]')
		    	print "authDataAlias and xaRecoveryAuthAlias modified for " + cmpConnectionFactory + "."

		print "Creating MappingModule." 	
		AdminConfig.create(\
		   'MappingModule', \
		   '"' + globCmpConnectionFactory + '"', \
		   '[[authDataAlias "' + cf_authDataAlias + '"] [mappingConfigAlias ""]]') 
		   
		print "Modifying data source."  
		AdminConfig.modify(\
		   '"' + datasourceId + '"', \
		   '[[name "' + datasourceName + '"] [authDataAlias "' + cf_authDataAlias + '"] [datasourceHelperClassname ' + ds_dataStoreHelperClassName + '] [description "DB2 Universal Driver Datasource"] [category ""] [jndiName "' + datasourceJndiName + '"] [xaRecoveryAuthAlias "' + cf_xaRecoveryAuthAlias + '"]]')
		
		# to make cwDatasource work properly, set this property to true (default is false)
		if nonTransactionalDatasource == "true":
			params = ["-propertyName", "nonTransactionalDataSource", "-propertyValue", "true"]
			AdminTask.setResourceProperty(datasourceId, params)
		
		print "Creating MappingModule." 
		AdminConfig.create(\
		   'MappingModule', \
		   '"' + datasourceId + '"', \
		   '[[authDataAlias "' + cf_authDataAlias + '"] [mappingConfigAlias ""]]')
		
	# end of function CreateDatasource

def createNewUser(userUid, pw, lname, fname):
	user = AdminTask.searchUsers('[-uid ' + userUid + ']')
	if len(user) == 0:
		AdminTask.createUser('[-uid ' + userUid + ' -password ' + pw + ' -confirmPassword ' + pw + ' -sn ' + lname + ' -cn ' + fname + ']')
	else :
		print "User '" + userUid + "' already exists."
		
def createNewGroup(groupUid):
	group = AdminTask.searchGroups('[-cn ' + groupUid + ']')
	if len(group) == 0:
		AdminTask.createGroup ('[-cn ' + groupUid + ' -description ]')
	else :
		print "Group '" + groupUid + "' already exists."
		
def install():
	
	print ""
	print "###############################################################################"
	print "### Server Configuration and Application Installation #########################"
		
	print ""
	print "Installation will be run on:"
	print ""
	print "Cell: " + cellName
	print "Node: " + nodeName
	print "Server: " + serverName
	print ""
	print "Application Display Name: " + applicationDisplayName
	print "(Path: " + EARPath + ")"
	print ""
		    
	print "###############################################################################"
	print "### JDBC & Data Source Configuration ##########################################"
		
	print ""
	print "Checking for JDBC provider."
	# scope = Node=BL3AED4CNode02,Server=server1
	# AdminTask.createJDBCProvider('-interactive')
		
	#server = AdminConfig.getid("/Node:" + nodeName + "/Server:" + serverName + "/")
	#myproviderEntries = AdminConfig.list('JDBCProvider', server).split('\n') 
		
	myproviderEntries = AdminConfig.list('JDBCProvider').split('\n')
		
	providerOccurences = 0
	jdbc_prov = ""
	for provider in myproviderEntries[:]:
		provider = provider.strip()
		providerName = AdminConfig.showAttribute(provider,"name")	    

		if providerName == jdbcProviderName:
			providerOccurences = providerOccurences + 1
			jdbc_prov = provider
        				
        if providerOccurences > 1:
        	print "Multiple JDBCProvider with the name '" + jdbcProviderName + "' have been found."
        	print "Please provide a unique identifier for the JDBCProvider that should be used."
        	sys.exit()
        elif providerOccurences == 1:
        	print "JDBC provider has been found and will be used for the data source."
        	print "(JDBCProvider: " + jdbc_prov + ")"
        else :
        	print "JDBCProvider could not be found and will thus be created."	
        		
		# createJDBCProvider
		if jdbcProviderDatabaseType == ORACLE_STRING:
			jdbc_prov = AdminTask.createJDBCProvider(\
		   	'[-scope Node=' + nodeName + ',Server=' + serverName + ' -databaseType ' + jdbcProviderDatabaseType.capitalize() + ' -providerType ' + oracleJdbcProviderType + ' -implementationType ' + jdbcProviderImplementationType + ' -name "' + jdbcProviderName + '" -description ' + oracleJdbcProviderDescription + ' -classpath ' + oracleJdbcProviderClassPath + ' -nativePath "" ]')
			
		elif jdbcProviderDatabaseType == DB2_STRING:
			jdbc_prov = AdminTask.createJDBCProvider(\
		   	'[-scope Node=' + nodeName + ',Server=' + serverName + ' -databaseType ' + jdbcProviderDatabaseType.capitalize() + ' -providerType ' + db2JdbcProviderType + ' -implementationType ' + jdbcProviderImplementationType + ' -name "' + jdbcProviderName + '" -description ' + db2JdbcProviderDescription + ' -classpath ' + db2JdbcProviderClassPath + ' -nativePath "" ]')
			
		else :
			print "Unknown value for databaseType in property file. Please choose either '" + DB2_STRING + "' or '" + ORACLE_STRING + "'."
			sys.exit()
			
		print "New JDBCProvider has been created."
		print "Saving configurations (JDBCProviders)."
		AdminConfig.save()
	
		
	print "Creating new data source " + cwDatasourceName + "."
	datasourceId = AdminConfig.getid('/DataSource:' + cwDatasourceName + '/')
	if len(datasourceId) == 0:
		# The last parameter stands for the nonTransactionalDatasource property of the data source.
		# Only for the cwAppDatabase it has to be set to true. If this parameter is empty, it will be set to false by default.
		createDatasource(jdbc_prov, cwDatasourceName, cwDatasourceJndiName, cwDatasourceDatabaseName, cwDatasourceDriverType, cwDatasourceServerName, cwDatasourcePortNumber, cwDatabaseUrl, "true")
		print "Saving configurations (created and modified resources)."
		AdminConfig.save()
	else :
		print "Data source '" + cwDatasourceName + "' already exists." 
		
	print "Creating new Data source " + cwAppDatasourceName + "."
		
	datasourceId2 = AdminConfig.getid('/DataSource:' + cwAppDatasourceName + '/')
	if len(datasourceId2) == 0:	
		createDatasource(jdbc_prov, cwAppDatasourceName, cwAppDatasourceJndiName, cwAppDatasourceDatabaseName, cwAppDatasourceDriverType, cwAppDatasourceServerName, cwAppDatasourcePortNumber, cwAppDatabaseUrl, "false")
		print "Saving configurations (created and modified resources)."
		AdminConfig.save()
	else :
		print "Data source '" + cwAppDatasourceName + "' already exists."
		
	print "Creating a new Authentication Data Entry."
	# [-alias CWDBUser -user db2admin -password ******** -description ]
	# AdminTask.createAuthDataEntry('-interactive')
		
	cf_authDataAlias = nodeName + "/" + authAlias
	output = ""
	authDataEntries = AdminTask.listAuthDataEntries().split('\n')
	for authEntry in authDataEntries[:]:
		authEntry = authEntry.strip()
		if string.find(authEntry,"[alias " + cf_authDataAlias + "]") >= 0:
			output = "AuthDataEntry '" + cf_authDataAlias + "' already exists."
			print output
			break
			    	
	if output == "":
		AdminTask.createAuthDataEntry('-alias ' + authAlias + ' -user ' + dbUser + ' -password ' + dbPassword)
		print "Saving configurations (AuthDataEntry)."
		AdminConfig.save()
		
	print ""
		
	print "###############################################################################"
	print "### Service Integration Bus configuration #####################################"
		
	print ""
	print "Creating a new Service Integration Bus."
	# AdminTask.createSIBus('-interactive')
		
	siBus_id = AdminConfig.getid("/SIBus:" + siBus + "/")
	if len(siBus_id) == 0: 
		AdminTask.createSIBus(\
		   '[-bus ' + siBus + ' -busSecurity ' + siBusSecurity + ' -scriptCompatibility ' + siBusScriptCompatibility + ' ]')
		
		print "Adding member to existing Service Integration Bus."
		# AdminTask.addSIBusMember('-interactive')
		AdminTask.addSIBusMember(\
		   '[-bus ' + siBus + ' -node ' + nodeName + ' -server ' + serverName + ' -fileStore ' + bm_fileStore + ' -logSize ' + bm_logSize + ' -minPermanentStoreSize ' + bm_minPermanentStoreSize + ' -maxPermanentStoreSize ' + bm_maxPermanentStoreSize + ' -unlimitedPermanentStoreSize ' + bm_unlimitedPermanentStoreSize + ' -minTemporaryStoreSize ' + bm_minTemporaryStoreSize + ' -maxTemporaryStoreSize ' + bm_maxTemporaryStoreSize + ' -unlimitedTemporaryStoreSize ' + bm_unlimitedTemporaryStoreSize + ' ]')
		
		print "Saving new bus and configurations."
		AdminConfig.save()
	else :
		print "SIBus: '" + siBus + "' already exists."
		
	print ""
		
	print "###############################################################################"
	print "### Java Messaging Service configuration ######################################"
		
	print ""
	print "Creating a new SIBJMS Connection Factory."
		
	jmsCF = AdminConfig.getid('/Server:'+serverName+'/J2CResourceAdapter:SIB JMS Resource Adapter/J2CConnectionFactory:'+sibjms_name)
	if len(jmsCF) == 0:
		# get target object id (e.g. "server1(cells/BL3AED4CNode02Cell/nodes/BL3AED4CNode02/servers/server1|server.xml") without any id in the end
		myserv=""
		servers = AdminTask.listServers('[-serverType APPLICATION_SERVER ]').split('\n')
		for serv in servers[:]:
			serv = serv.strip()
		  
		    	if AdminConfig.showAttribute(serv, "name") == serverName:
		    		myserv = serv
		    	
		# AdminTask.createSIBJMSConnectionFactory('-interactive')
		AdminTask.createSIBJMSConnectionFactory(\
		   '"' + myserv + '"', \
		   '[-type ' + sibjms_type + ' -name ' + sibjms_name + ' -jndiName ' + sibjms_jndiName + ' -description  ' + sibjms_description + ' -category ' + sibjms_category + ' -busName ' + sibjms_busName + ' -clientID ' + sibjms_clientID + ' -nonPersistentMapping ' + sibjms_nonPersistentMapping + ' -readAhead ' + sibjms_readAhead + ' -tempTopicNamePrefix ' + sibjms_tempTopicNamePrefix + ' -durableSubscriptionHome ' + sibjms_durableSubscriptionHome + ' -shareDurableSubscriptions ' + sibjms_shareDurableSubscriptions + ' -target ' + sibjms_target + ' -targetType ' + sibjms_targetType + ' -targetSignificance ' + sibjms_targetSignificance + ' -targetTransportChain  ' + sibjms_targetTransportChain + ' -providerEndPoints ' + sibjms_providerEndPoints + ' -connectionProximity ' + sibjms_connectionProximity + ' -authDataAlias ' + sibjms_authDataAlias + ' -containerAuthAlias ' + sibjms_containerAuthAlias + ' -mappingAlias ' + sibjms_mappingAlias + ' -shareDataSourceWithCMP ' + sibjms_shareDataSourceWithCMP + ' -logMissingTransactionContext ' + sibjms_logMissingTransactionContext + ' -manageCachedHandles ' + sibjms_manageCachedHandles + ' -xaRecoveryAuthAlias ' + sibjms_xaRecoveryAuthAlias + ' -persistentMapping ' + sibjms_persistentMapping + ' -consumerDoesNotModifyPayloadAfterGet ' + sibjms_consumerDoesNotModifyPayloadAfterGet + ' -producerDoesNotModifyPayloadAfterSet ' + sibjms_producerDoesNotModifyPayloadAfterSet + ']')
		
		print "Saving SIBJMS Connection Factory configurations."
		AdminConfig.save()

	else :
		print "SIBJMS Connection Factory already exists."

	print ""
		
	print "###############################################################################"
	print "### IBM RDM SSL Certificate installation ######################################"
	print ""
		
	if ssl_host == "" or ssl_port == "" or ssl_certificateAlias == "" :
		
		""" if any variable empty or commented out """
		print "This part will be skipped."
		print "Reason: Respective variables have been commented out or are empty."
		
	else :
	
		print "Retrieving and installing a new SSL Certificate."
		output = ""
		certificates = AdminTask.listSignerCertificates('[-keyStoreName ' + keyStoreName + ' -keyStoreScope (cell):' + cellName + ':(node):' + nodeName + ' ]').split('\n')
		for cert in certificates[:]:
			cert = cert.strip()
			if string.find(cert,"[alias " + ssl_certificateAlias + "]") >= 0:
		    		output = "SSL Certificate already exists."
		    		print output
		    		break
	    	
		if output == "":
	
			AdminTask.retrieveSignerInfoFromPort(\
		   	'[-host ' + ssl_host + ' -port ' + ssl_port + ' -sslConfigName ' + ssl_sslConfigName + ' -sslConfigScopeName ' + ssl_sslConfigScopeName + ' ]')
		
			AdminTask.retrieveSignerFromPort(\
		   	'[-keyStoreName ' + keyStoreName + ' -keyStoreScope ' + keyStoreScope + ' -host ' + ssl_host + ' -port ' + ssl_port + ' -certificateAlias ' + ssl_certificateAlias + ' -sslConfigName ' + ssl_sslConfigName + ' -sslConfigScopeName ' + ssl_sslConfigScopeName + ' ]')
		
			print "Saving SSL Certificate configuration."
			AdminConfig.save()
	
	print ""
	
	print "###############################################################################"
	print "### Web Messaging configuration ###############################################"

	print ""
	print "Creating WebContainerInboundChannels."
	createPropertyInWCC("WCC_2", "webmsgenabled")
	createPropertyInWCC("WCC_4", "webmsgenabled")	
		
	print ""
	
	print "###############################################################################"
	print "### Security configuration ####################################################"
	
	print ""
	print "Configuring Security Settings."
	AdminTask.applyWizardSettings(\
	   '[-secureApps true -secureLocalResources false -adminPassword ' + adminPassword + ' -userRegistryType WIMUserRegistry -adminName ' + adminName + ' ]')
	print "Saving security wizard settings."
	
	AdminTask.configureAdminWIMUserRegistry(\
	   '[-verifyRegistry true ]')
	print "Saving AdminWIMUserRegistry configurations."
	AdminConfig.save()
	print ""
	
	print "###############################################################################"
	print "### Create Users and Groups ###################################################"
	
	print ""
	print "Creating new Users and Groups."
			
	createNewUser(cwAdminUid, cwAdminPassword, cwAdminLastname, cwAdminFirstname)
	createNewUser(fdaUid, fdaPassword, fdaLastname, fdaFirstname)
	createNewUser(readOnlyUid, readOnlyPassword, readOnlyLastname, readOnlyFirstname)
	createNewGroup(adminGroupUid)
	createNewGroup(fdaGroupUid)
	createNewGroup(readOnlyGroupUid)
	AdminTask.addMemberToGroup('[-memberUniqueName uid=' + cwAdminUid + ',o=defaultWIMFileBasedRealm -groupUniqueName cn=' + adminGroupUid + ',o=defaultWIMFileBasedRealm]')
	AdminTask.addMemberToGroup('[-memberUniqueName uid=' + fdaUid + ',o=defaultWIMFileBasedRealm -groupUniqueName cn=' + fdaGroupUid + ',o=defaultWIMFileBasedRealm]')
	AdminTask.addMemberToGroup('[-memberUniqueName uid=' + readOnlyUid + ',o=defaultWIMFileBasedRealm -groupUniqueName cn=' + readOnlyGroupUid + ',o=defaultWIMFileBasedRealm]')
		
	print "Saving user and group configurations."
	AdminConfig.save()
	print ""
	
	print "###############################################################################"
	print "### Install Application on Server #############################################"
	
	print ""
	apps = AdminApp.list("WebSphere:cell="+cellName+",node="+nodeName+",server="+serverName).split('\n')
	output = ""
	for app in apps[:]:
		app = app.strip()
		if app == applicationDisplayName:
			output = "Application '" + applicationDisplayName + "' already exists."
			print output
			
	if output == "":
		print "Installing Application.."
		AdminApp.install(EARPath, '[-node ' + nodeName + ' -cell ' + cellName + ' -server ' + serverName + ']')
		# interactive: AdminApp.installInteractive('c:/MyStuff/application1.ear')
		AdminConfig.save()
	
		# check if system is ready to start the application, argument = EAR file name (e.g. application1.ear -> 'application1')
		if AdminApp.isAppReady(applicationDisplayName) == "true":
			print "The system is ready to start the application. Application will be started.."
		else :
			print "The system is not ready to start the application."
	
		# start application
		appManager = AdminControl.queryNames('cell=' + cellName + ',node=' + nodeName + ',type=ApplicationManager,process=' + serverName + ',*')
		try :
			AdminControl.invoke(appManager, 'startApplication', applicationDisplayName)
		except Exception, err:
			print "The application could not be started due to the following error: ", err
			
		print "Installation and configuration have finished successfully."
		print "The application is up and ready."
		
def cleanup():
	
	print ""
	print "###############################################################################"
	print "### Cleanup ###################################################################"
	print ""
	
	print "Sarting Cleanup.."
	
	## Remove Data sources		 			
	datasourceId = AdminConfig.getid('/DataSource:' + cwDatasourceName + '/')
	datasourceId2 = AdminConfig.getid('/DataSource:' + cwAppDatasourceName + '/')
	if len(datasourceId) == 0: 
		print "Data source '" + cwDatasourceName + "' does not exist."  
	else :
		removeDatasource(datasourceId)
	
	if len(datasourceId2) == 0: 
		print "Data source '" + cwAppDatasourceName + "' does not exist." 
	else :
		removeDatasource(datasourceId2)
	
	## Remove JDBCProvider
	
	print "JDBCProvider will be ignored. Multiple providers with the same name may exist."

	## Remove Service Integration Bus
	
	siBus_id = AdminConfig.getid("/SIBus:" + siBus + "/")
	if len(siBus_id) == 0: 
		print "SIBus '" + siBus + "' does not exist."
	else :
		print "Removing SIBus: '" + siBus_id + "'"
		AdminTask.deleteSIBus(\
		'[-bus ' + siBus + ' ]')
	
	## Remove SignerCertificate
	
	output = ""
	certificates = AdminTask.listSignerCertificates('[-keyStoreName ' + keyStoreName + ' -keyStoreScope (cell):' + cellName + ':(node):' + nodeName + ' ]').split('\n')
	for cert in certificates[:]:
		cert = cert.strip()
		if string.find(cert,"[alias " + ssl_certificateAlias + "]") >= 0:
	    		AdminTask.deleteSignerCertificate(\
			'[-keyStoreName ' + keyStoreName + ' -keyStoreScope (cell):' + cellName + ':(node):' + nodeName + ' -certificateAlias ' + ssl_certificateAlias + ' ]')
			output = "Removing SSL Certificate: " + ssl_certificateAlias
	    		print output
	    		break
	    	
	if output == "":
		print "SSL Certificate '" + ssl_certificateAlias + "' does not exist."


	## Remove WebContainerInboundChannel
	
	removePropertyFromWCC("WCC_2", "webmsgenabled")
	removePropertyFromWCC("WCC_4", "webmsgenabled")
	
	## Remove User and Groups
	
	user = AdminTask.searchUsers('[-uid '+cwAdminUid+']')
	if len(user) == 0:
		print "User '" + cwAdminUid + "' does not exist."
	else :
		print "Removing User: '" + cwAdminUid + "'"
		AdminTask.deleteUser('-uniqueName uid='+cwAdminUid+',o=defaultWIMFileBasedRealm')
		
	user = AdminTask.searchUsers('[-uid '+readOnlyUid+']')
	if len(user) == 0:
		print "User '" + readOnlyUid + "' does not exist."
	else :
		print "Removing User: '" + readOnlyUid + "'"
		AdminTask.deleteUser('-uniqueName uid='+readOnlyUid+',o=defaultWIMFileBasedRealm')
	
	user = AdminTask.searchUsers('[-uid '+fdaUid+']')
	if len(user) == 0:
		print "User '" + fdaUid + "' does not exist."
	else :
		print "Removing User: '" + fdaUid + "'"
		AdminTask.deleteUser('-uniqueName uid='+fdaUid+',o=defaultWIMFileBasedRealm')

	group = AdminTask.searchGroups('[-cn '+adminGroupUid+']')
	if len(group) == 0:
		print "Group '" + adminGroupUid + "' does not exist."
	else :
		print "Removing Group: '" + adminGroupUid + "'"
		AdminTask.deleteGroup('-uniqueName cn='+adminGroupUid+',o=defaultWIMFileBasedRealm')
	
	group = AdminTask.searchGroups('[-cn '+fdaGroupUid+']')
	if len(group) == 0:
		print "Group '" + fdaGroupUid + "' does not exist."
	else :
		print "Removing Group: '" + fdaGroupUid + "'"
		AdminTask.deleteGroup('-uniqueName cn='+fdaGroupUid+',o=defaultWIMFileBasedRealm')
		
	group = AdminTask.searchGroups('[-cn '+readOnlyGroupUid+']')
	if len(group) == 0:
		print "Group '" + readOnlyGroupUid + "' does not exist."
	else :
		print "Removing Group: '" + readOnlyGroupUid + "'"
		AdminTask.deleteGroup('-uniqueName cn='+readOnlyGroupUid+',o=defaultWIMFileBasedRealm')
		
	## Remove SIBJMSConnectionFactory
	
	jmsCF = AdminConfig.getid('/Server:'+serverName+'/J2CResourceAdapter:SIB JMS Resource Adapter/J2CConnectionFactory:'+sibjms_name)
	if len(jmsCF) == 0:
		print "SIBJMSConnectionFactory '" + sibjms_name + "' does not exist."
	else:
	        print "Removing " + jmsCF
	        AdminConfig.remove(jmsCF)
	
	## Remove AuthDataEntry	
	cf_authDataAlias = nodeName + "/" + authAlias
	output = ""
	authDataEntries = AdminTask.listAuthDataEntries().split('\n')
	for authEntry in authDataEntries[:]:
		authEntry = authEntry.strip()
		if string.find(authEntry,"[alias " + cf_authDataAlias + "]") >= 0:
	    		AdminTask.deleteAuthDataEntry(\
			'[-alias ' + cf_authDataAlias + ' ]')
			output = "Removing AuthDataEntry: " + cf_authDataAlias
	    		print output
	    		break
	    	
	if output == "":
		print "AuthDataEntry '" + cf_authDataAlias + "' does not exist."
	
	AdminTask.configureAdminWIMUserRegistry(\
	   '[-verifyRegistry true ]')
	
	## Uninstall Application
	
	## checking for the existence of application , is application exists then updating it and if it does not exists then installing it 
	application = AdminConfig.getid("/Deployment:" + applicationDisplayName + "/")
	if len(application) > 0:
		print "Uninstalling Application.. "
		AdminApp.uninstall(applicationDisplayName)
	else :
		print "Application '" + applicationDisplayName + "' does not exist."
		
	AdminConfig.save()
	print "Finished cleanup."
								 					
####################### Get Environmental Info ########################

# get cell, node and server the script is running on

cellName = AdminControl.getCell()

objNameStringServer = AdminControl.completeObjectName('WebSphere:type=Server,*') 
nodeName = AdminControl.getAttribute(objNameStringServer, 'nodeName')

objNameStringNode = AdminControl.queryNames('WebSphere:type=Server,node=' + nodeName + ',*')
nodeConfigId = AdminControl.getConfigId(objNameStringNode)
serverName = AdminConfig.showAttribute(nodeConfigId, "name")

####################### Load all properties ###########################

props = loadProperties(propertiesFile)

### Administation Information

adminName = getProperty(props, 'adminName')
adminPassword = getProperty(props, 'adminPassword')

### JDBC Information
jdbcProviderDatabaseType = getProperty(props, 'databaseType').lower()
jdbcProviderImplementationType = '"XA data source"'
jdbcProviderName = getProperty(props, 'jdbcProviderName')

# ONLY NECESSARY IF USING DB2
db2JdbcProviderType = '"DB2 Universal JDBC Driver Provider"'
db2JdbcProviderDescription = '"Two-phase commit DB2 JCC provider that supports JDBC 3.0. Data sources that use this provider support the use of XA to perform 2-phase commit processing."'
db2JdbcProviderClassPath = "'" + getProperty(props, 'jdbcDriverClassPath') + "/db2jcc.jar;" + getProperty(props, 'jdbcDriverClassPath') + "/db2jcc_license_cu.jar;" + getProperty(props, 'jdbcDriverClassPath') + "/db2jcc_license_cisuz.jar'"
db2DatasourceDescription = '"DB2 Universal Driver Data source"'


# ONLY NECESSARY IF USING ORACLE
oracleJdbcProviderType = '"Oracle JDBC Driver"'
oracleJdbcProviderDescription = '"Oracle JDBC Driver (XA)"'
oracleJdbcProviderClassPath = "'" + getProperty(props, 'jdbcDriverClassPath') + "/ojdbc6.jar;'"
oracleDatasourceDescription = '"Oracle JDBC Data source"'
cwDatabaseUrl = getProperty(props, 'cwDatabaseUrl')
cwAppDatabaseUrl = getProperty(props, 'cwDatabaseUrl')


### General Data Source Information
ds_dataStoreHelperClassName = getProperty(props, 'datastoreHelperClass')
ds_containerManagedPersistence = "true"
ds_componentManagedAuthenticationAlias = ""
ds_xaRecoveryAuthAlias = ""

### Data source CWDB Information

cwDatasourceName = getProperty(props, 'cwDatasourceName')
cwDatasourceJndiName = getProperty(props, 'cwDatasourceJndiName')
cwDatasourceDatabaseName = getProperty(props, 'cwDatabaseName') 
cwDatasourceDriverType = "4"
cwDatasourceServerName = getProperty(props, 'cwDatabaseServer')
cwDatasourcePortNumber = getProperty(props, 'cwDatabasePort')

### Data source CWAPPDB Information

cwAppDatasourceName = getProperty(props, 'cwAppDatasourceName')
cwAppDatasourceJndiName = getProperty(props, 'cwAppDatasourceJndiName')
cwAppDatasourceDatabaseName = getProperty(props, 'cwDatabaseName') 
cwAppDatasourceDriverType = "4"
cwAppDatasourceServerName = getProperty(props, 'cwDatabaseServer')
cwAppDatasourcePortNumber = getProperty(props, 'cwDatabasePort')

### Authentication Information
authAlias = getProperty(props, 'authAlias')
dbUser = getProperty(props, 'dbUser')
dbPassword = getProperty(props, 'dbPassword')

### Service Integration Bus Information

siBus = getProperty(props, 'serviceIntegrationBus')
siBusSecurity = "false"
siBusScriptCompatibility = "6.1"

bm_fileStore = ""
bm_logSize = "100"
bm_minPermanentStoreSize = "200"
bm_maxPermanentStoreSize = "500"
bm_unlimitedPermanentStoreSize = "false"
bm_minTemporaryStoreSize = "200"
bm_maxTemporaryStoreSize = "500"
bm_unlimitedTemporaryStoreSize = "false"

### Java Messaging Services Information

sibjms_type = "topic"
sibjms_name = getProperty(props, 'sibjmsName')
sibjms_jndiName = getProperty(props, 'sibjmsJndiName')
sibjms_description = ""
sibjms_category = ""
sibjms_busName = getProperty(props, 'sibjmsBusName')
sibjms_clientID = ""
sibjms_nonPersistentMapping = "ExpressNonPersistent"
sibjms_readAhead = "Default"
sibjms_tempTopicNamePrefix = ""
sibjms_durableSubscriptionHome = nodeName + "." + serverName + "-" + sibjms_busName
sibjms_shareDurableSubscriptions = "InCluster"
sibjms_target = ""
sibjms_targetType = "BusMember"
sibjms_targetSignificance = "Preferred"
sibjms_targetTransportChain = ""
sibjms_providerEndPoints = ""
sibjms_connectionProximity = "Bus"
sibjms_authDataAlias = ""
sibjms_containerAuthAlias = ""
sibjms_mappingAlias = ""
sibjms_shareDataSourceWithCMP = "false"
sibjms_logMissingTransactionContext = "false"
sibjms_manageCachedHandles = "false"
sibjms_xaRecoveryAuthAlias = ""
sibjms_persistentMapping = "ReliablePersistent"
sibjms_consumerDoesNotModifyPayloadAfterGet = "false"
sibjms_producerDoesNotModifyPayloadAfterSet = "false"

### SSL Certificate Information

keyStoreName = "NodeDefaultTrustStore"
keyStoreScope = "(cell):" + cellName + ":(node):" + nodeName
ssl_host = getProperty(props, 'rdmHubHost')
ssl_port = getProperty(props, 'rdmHubSSLPort')
ssl_sslConfigName = "NodeDefaultSSLSettings"
ssl_sslConfigScopeName = "(cell):" + cellName + ":(node):" + nodeName 
# WAS will always save certificate in lower case letters
ssl_certificateAlias = getProperty(props, 'sslSignerCertificateAlias').lower()

### User and Group Information

cwAdminUid = getProperty(props, 'cwAdminUid')
cwAdminPassword = getProperty(props, 'cwAdminPassword')
cwAdminLastname = getProperty(props, 'cwAdminLastname')
cwAdminFirstname = getProperty(props, 'cwAdminFirstname')

fdaUid = getProperty(props, 'fdaUid')
fdaPassword = getProperty(props, 'fdaPassword')
fdaLastname = getProperty(props, 'fdaLastname')
fdaFirstname = getProperty(props, 'fdaFirstname')

readOnlyUid = getProperty(props, 'readOnlyUid')
readOnlyPassword = getProperty(props, 'readOnlyPassword')
readOnlyLastname = getProperty(props, 'readOnlyLastname')
readOnlyFirstname = getProperty(props, 'readOnlyFirstname')

adminGroupUid = getProperty(props, 'adminGroupUid')
fdaGroupUid = getProperty(props, 'fdaGroupUid')
readOnlyGroupUid = getProperty(props, 'readOnlyGroupUid')

### Other Information

applicationDisplayName = getProperty(props, 'applicationDisplayName')
EARPath = getProperty(props, 'earFile')

####################### Global Variables ##############################

global globCmpConnectionFactory
ORACLE_STRING = "oracle"
DB2_STRING = "db2"

######################################################################

# SCRIPT STARTS HERE

# sys.argv is an array that contains arguments that the script can be run with
if len(sys.argv) != 1:
	
	print ""
	print "###############################################################################"
	print "### Invalid Argument(s) #######################################################"
	print ""

	print "Invalid argument(s). Available options are: 'cleanup' or 'install'."
	print "Please rerun the script with one of the two above arguments."
	
else :
	whatToDo = sys.argv[0]	
	if whatToDo == "cleanup":
		
		cleanup()
		
	elif whatToDo == "install":
		
		install()
							
	else :
		# in case there is only one argument, but INVALID
		
		print ""
		print "###############################################################################"
		print "### Invalid Argument ##########################################################"
		print ""
	
		print "Invalid argument. Available options are: 'cleanup' or 'install'."
		print "Please rerun the script with one of the two above arguments."
			
print ""
print "###############################################################################"
print "### End of Script #############################################################"
