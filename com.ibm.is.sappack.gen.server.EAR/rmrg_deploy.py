###############################################################
# Pre-requisite: InformationServer 8.x should be installed    #
# This script installs all RMRG apps to a single WAS server   #
# Usage: wsadmin -language jython -user <username>            # 
#        -password <password> -f rmrg_deploy.py               #
#        <nodename - got by clicking 'Websphere application   #
#         servers' in the Servers section>                    # 
#        <servername e.g. server1> <path to ear files         #
#        e.g. C:/IBM/InformationServer/webapps(windows)       #
#        /opt/IBM/InformationServer/webapps(linux)>           #
###############################################################

import java.io as io

# Get the cell from the command line
cell = sys.argv[2]
# Get the node from commandline
node = sys.argv[3]
# Get the server name from commandline
server = sys.argv[4]
# Get the location of the war files
ear_location = sys.argv[5]
  
robFile = io.File(ear_location)
listDirs = robFile.list()

appManager = AdminControl.queryNames('cell='+cell+',node='+node+',type=ApplicationManager,process='+server+',*')

print "Cell Name: "+cell
print "Node Name: "+node
print "Server Name: "+server
print "Application Manager: "+appManager

for x in range(0, len(listDirs), 1):
	currentDirElement = listDirs[x]
	if currentDirElement.find(".ear") > 0:
		print "EAR file found: " + currentDirElement
		earWithoutExtension = currentDirElement.replace(".ear", "")
		# deploy all the apps
		attrs = ['-appname', currentDirElement, '-MapWebModToVH', [['.*', '.*', 'default_host']],  '-node', node, '-server', server, '-contextroot', "/"+earWithoutExtension]
		print "Installing "+earWithoutExtension
		AdminApp.install(ear_location + "/" + currentDirElement, attrs)
		AdminConfig.save()
		#Start the app    
		apps = AdminApp.list().split("\n");    
		theApp = ""    
		for iApp in apps:
			if str(iApp).find(currentDirElement) >= 0:
				theApp = iApp;
		print "Starting App: ", theApp
		AdminControl.invoke(appManager, 'startApplication', theApp)
		print "Application installed and started successfuly!"
		