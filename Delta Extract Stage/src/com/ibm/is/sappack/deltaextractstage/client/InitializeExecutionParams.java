package com.ibm.is.sappack.deltaextractstage.client;

import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.ibm.is.sappack.deltaextractstage.load.*;
import com.sap.conn.jco.JCoException;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.ibm.is.sappack.deltaextractstage.client:
//            DsSapExtractorLogger, DsSapExtractorResourceBundle, DsSapExtractorConnectionManager, DsSapExtractorIdocRequest, 
//            PreRunConfiguration, ConfigFile, Utilities

public class InitializeExecutionParams
{ 

    private Properties userStageProperties = null;
    private DsSapExtractorParam dsExtractorParam = null;
    private DsSapExtractorConnectionManager sapManager = null;
    private DsSapExtractorLogger extractorLogger = null;
    private RsinfoIdocMonitor rsinfomonitor = null;
    private ReadFileImpl readfileimpl = null;
    private DsSapExtractorIdocRequest client = null;
    private PreRunConfiguration prerunconfig = null;
    private DsSapExtractorAdapter clientadapter = null;
    ArrayList<String> filt = null;
    private final String BS = ""+(char)47;
	final String DDC = ""+(char)34+(char)34;
	final String DC = ""+(char)34;
	final String COMMA = ""+(char)44;
	final String CCB = ""+(char)125;
	final String TILDA = ""+(char)126;
	final String SEMICOLON = ""+(char)59;
	DataType datatype = null;
	private boolean ishierarchy = false;
    private final String FILTERINPUT = "<FIELDNM>%s</FIELDNM><SIGN>I</SIGN><OPTION>%s</OPTION><LOW>%s</LOW><HIGH>%s</HIGH>";

    public InitializeExecutionParams(Properties userStageProperties, DsSapExtractorParam dsExtractorParam, DataType datatype)
    {
        this.userStageProperties = userStageProperties;
        this.dsExtractorParam = dsExtractorParam;
        new ArrayList<String>();
        this.datatype = datatype;
    }

    public void initilizeExecutionParameters(int x)
    {
        DsSapExtractorLogger.information("DeltaExtract_57",new Object[] {
            "DeltaExtract_57"
        });
        DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {
                (new StringBuilder("++++++ Filter +++++++ for Node:")).append(userStageProperties.getProperty("SAPDATAFILTERLIST")).toString()
            });
        dsExtractorParam.setDsinputfilters(userStageProperties.getProperty("SAPDATAFILTERLIST"));
        dsExtractorParam.setSapClient(userStageProperties.getProperty("SAPCLIENTNUMBER"));
        dsExtractorParam.setSapUser(userStageProperties.getProperty("SAPUSERID"));
        dsExtractorParam.setSapPassword(userStageProperties.getProperty("SAPPASSWORD"));
        dsExtractorParam.setSapLanguage(userStageProperties.getProperty("SAPLANGUAGE"));
        dsExtractorParam.setSapPoolId("SAPPOOLID");
        dsExtractorParam.setSegmentStructure(checkNullForLogger("SAP DATASOURCE NAME", userStageProperties.getProperty("SAPDATASOURCENAME")));
        dsExtractorParam.setDataSrcType(checkNullForLogger("SAP DATASOURCE TYPE", userStageProperties.getProperty("SAPDATASOURCETYPE")));
        if(dsExtractorParam.getDataSrcType().equals("HIER"))
        {
        ishierarchy = true;
        dsExtractorParam.setHierName(checkNullForLogger("SAP HIERARCHY NAME", userStageProperties.getProperty("SAPHIERARCHYNAME")));
        dsExtractorParam.setHierClass(checkNullForLogger("SAP HIERARCHY CLASS", userStageProperties.getProperty("SAPHIERARCHYCLASS")));
        dsExtractorParam.setDateFrom(checkNullForLogger("SAP HIERARCHY VALIDFROM", userStageProperties.getProperty("SAPHIERARCHYVALIDFROM")));
        dsExtractorParam.setDateTo(checkNullForLogger("SAP HIERARCHY VALIDTO", userStageProperties.getProperty("SAPHIERARCHYVALIDTO")));
        }
        dsExtractorParam.setDataFetchMode(checkNullForLogger("SAP DATA FETCH MODE", userStageProperties.getProperty("SAPDATAFETCHMODE")));
        
        dsExtractorParam.setExtractionTimeOut();
        dsExtractorParam.setLogTableData("0");
        datatype.ishierrarchy(ishierarchy);
     
        validateDSSAPHOME();
        try
        {
            readDSSAPConnectionsConfigFile(userStageProperties.getProperty("CONNECTIONNAME"), (new StringBuilder(String.valueOf(System.getenv("DSSAPHOME")))).append(BS).append("DSSAPConnections").toString());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        dsExtractorParam.setTidFilePath(checkNullForLogger("TIDFilePath", dsExtractorParam.getFilePath()));
   //     dsExtractorParam.setIdocFilterClause(filterConditionInput);
        DsSapExtractorResourceBundle extractorOTResourceBundle = new DsSapExtractorResourceBundle();
        extractorOTResourceBundle.initializeResource("_DsSapDeltaExtractor");
        extractorLogger = new DsSapExtractorLogger();
        dsExtractorParam.setExtractorLogger(extractorLogger);
        DsSapExtractorLogger.initLogger();
        DsSapExtractorLogger.information("DeltaExtract_65",new Object[] {
            "DeltaExtract_65", (Integer.valueOf(dsExtractorParam.getExtractionTimeOut())/1000)
        });
        DsSapExtractorLogger.information("DeltaExtract_68",new Object[] {
            "DeltaExtract_68"
        });
    }

    public void initializeSAPConnection(boolean isServerInit)
    {
        sapManager = new DsSapExtractorConnectionManager(extractorLogger, dsExtractorParam);
        sapManager.setSapApplicationServerHostName(dsExtractorParam.getSapApplicationServerHostName());
        sapManager.setSapClient(dsExtractorParam.getSapClient());
        sapManager.setSapLanguage(dsExtractorParam.getSapLanguage());
        sapManager.setSapPassword(dsExtractorParam.getSapPassword());
        sapManager.setSapPoolId(dsExtractorParam.getSapPoolId());
        sapManager.setSapSystemNr(dsExtractorParam.getSapSystemNr());
        sapManager.setSapUser(dsExtractorParam.getSapUser());
        sapManager.setGroupName(dsExtractorParam.getGroupName());
        sapManager.setMsgServer(dsExtractorParam.getMsgServer());
        sapManager.setRouterString(dsExtractorParam.getRouterString());
        sapManager.setSapSystemId(dsExtractorParam.getSapSystemId());
        sapManager.setMessageServerSystem(dsExtractorParam.isMessageServerSystem());
        
        sapManager.initializeClientConnection();
    }

    public void prepareFilterCondition(String filters)
    {
    	DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {(new StringBuilder("++++++ prepareFilterCondition:+++++"+filters))});
	    filt = new ArrayList<String>();	
		String [] filterslist = filters.split(TILDA);
		for(String filter:filterslist)
		{
			String[] fil = filter.replaceAll(DDC, "").split(COMMA);
			try {
				String filt1 = fil[2].split(":")[1].replaceAll(CCB, "").trim();
				String filt2 = "";
				if(fil.length>3)
				{
					filt2 = fil[3].split(":")[1].replaceAll(CCB, "").trim();
				}
				String filterstr = String.format(FILTERINPUT, fil[0].split(":")[1].trim(),fil[1].split(":")[1].trim(),filt1, filt2);
				DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {(new StringBuilder("filterstr: "+filterstr))});
				filt.add(filterstr);
			} catch (Exception e) {
			//	System.out.println("e:"+e.getMessage());
			}
		}
		DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {(new StringBuilder("filt"+filt))});
		dsExtractorParam.setIdocFilterClause(filt);
		DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {(new StringBuilder("----- prepareFilterCondition:-----"))});
    }

    public void idocClientInitialization()
        throws JCoException, InterruptedException
    {
        client = new DsSapExtractorIdocRequest(extractorLogger, dsExtractorParam);
        client.initializationSAPConnection(sapManager.getExtractorDestination());
        rsinfomonitor = new RsinfoIdocMonitor(dsExtractorParam, extractorLogger);
        if(dsExtractorParam.getDataStageNodeId() == 0)
        {
            performPrerunCleanup();
            if(!((dsExtractorParam.getDsinputfilters()== null) || dsExtractorParam.getDataFetchMode().equals("D") || dsExtractorParam.getDataFetchMode().equals("R")))
            prepareFilterCondition(fetchFilterConditions(dsExtractorParam.getDsinputfilters()));
            prerunconfig = new PreRunConfiguration(sapManager.getExtractorDestination(), dsExtractorParam);
            prerunconfig.updatetables();
        }
        else
        addwait();
        clientadapter = new DsSapExtractorAdapter(extractorLogger, client.getExtractorDestination(), dsExtractorParam);
        readfileimpl = new ReadFileImpl(clientadapter, dsExtractorParam, extractorLogger);
        dsExtractorParam.setReadfileimpl(readfileimpl);
        dsExtractorParam.setRsinfomonitor(rsinfomonitor);
        client.initializeInternalIDocParameters();
        client.sendIDocXMLRequest();
        DsSapExtractorLogger.information("DeltaExtract_53",new Object[] {
            "DeltaExtract_53"
        });
    }

    public int randomNumberGenrator()
    {
        Random number = new Random();
        return number.nextInt();
    }

    public void performPrerunCleanup()
    {
        DsSapExtractorLogger.information("DeltaExtract_69",new Object[] {
            "DeltaExtract_69"
        });
        File file = new File((new StringBuilder(String.valueOf(dsExtractorParam.getFilePath()))).append(BS).append(dsExtractorParam.getJobName()).toString());
        if(file.isDirectory())
        {
            try
            {
                DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {
                    (new StringBuilder("++++++ Deleting any previous run left Directory +++++++ for Node:")).append(dsExtractorParam.getDataStageNodeId()).toString()
                });
            //    FileUtils.deleteDirectory(file);
                deleteDirectory(file);
                DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {
                    (new StringBuilder("++++++ Creating Directory +++++++ for Node:")).append(dsExtractorParam.getDataStageNodeId()).toString()
                });
                file.mkdir();
                DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {
                    (new StringBuilder("++++++ Directory Created+++++++ for Node:")).append(dsExtractorParam.getDataStageNodeId()).toString()
                });
            }
            catch(IOException e)
            {
                DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {
                    (new StringBuilder("++++++ Exception Occured+++++++ for Node:")).append(dsExtractorParam.getDataStageNodeId()).append(" : ").append(e.getMessage()).toString()
                });
                DsSapExtractorLogger.fatal("DeltaExtract_FatalError_11",new Object[] {
                    "DeltaExtract_FatalError_11", Integer.valueOf(dsExtractorParam.getDataStageNodeId()), " : ", e.getMessage()
                });
                e.printStackTrace();
            }
        } else
        {
            file.mkdir();
        }
        DsSapExtractorLogger.information("DeltaExtract_70",new Object[] {
            "DeltaExtract_70"
        });
    }

    private String checkNullForLogger(String paramName, String paramValue)
    {
        DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {
            (new StringBuilder("++++++ paramName: ")).append(paramName).append(" paramValue:").append(paramValue).toString()
        });
        if(paramValue == null || paramValue.equals(""))
        {
            DsSapExtractorLogger.fatal("DeltaExtract_72",new Object[] {"DeltaExtract_72", paramName});
        }
        return paramValue.trim();
    }

    private void readDSSAPConnectionsConfigFile(String dsSAPConnectionName, String dsSAPConnectionsDirectory)
        throws IOException
    {
        String METHODNAME = "readDSSAPConnectionsConfigFile(String dsSAPConnectionName, String dsSAPConnectionsDirectory, Map<String, String> stageProperties)";
        String dsSAPConnectionsFile = (new StringBuilder(String.valueOf(dsSAPConnectionsDirectory))).append(File.separator).append("DSSAPConnections.config").toString();
        ConfigFile dsSAPConnectionsConfig = new ConfigFile(dsSAPConnectionsFile);
        List<Object> l = dsSAPConnectionsConfig.getConfiguration();
        if(l == null || l.size() == 0)
        {
            String msgId = "CC_IDOC_ConfigFileEmpty";
            throw new IOException();
        }
        for(Iterator it = ((List)ConfigFile.findInList(l, "DSSAPCONNECTIONS")).iterator(); it.hasNext();)
        {
            List<Object> connectionProperties = (List<Object>)it.next();
            String name = (String)ConfigFile.findInList(connectionProperties, "NAME");
            if(name.equals(dsSAPConnectionName))
            {
            	String programID = (String)ConfigFile.findInList(connectionProperties, "REFSERVERPROGID");
            	checkNullForLogger("IDoc Listener Program ID", programID);
            	String idocLitenerFlag = (String)ConfigFile.findInList(connectionProperties, "LISTENFORIDOCS");
            	if(! idocLitenerFlag.trim().equals("TRUE"))
            	DsSapExtractorLogger.fatal("DeltaExtract_77",new Object[] {"DeltaExtract_77"});
            	String DspartnerNumber = (String)ConfigFile.findInList(connectionProperties, "ILOADPARTNERNUMBER");
                dsExtractorParam.setSenderPort(checkNullForLogger("Partner Number to Use When Sending IDocs", DspartnerNumber));
                dsExtractorParam.setSenderPortNumber(checkNullForLogger("Partner Number to Use When Sending IDocs", DspartnerNumber));
                String sapPartnerNumber = (String)ConfigFile.findInList(connectionProperties, "ILOADDESTPARTNERNUMBER");
                dsExtractorParam.setReceiverPortNumber(checkNullForLogger("Destination Partner Number to Use When Sending IDocs", sapPartnerNumber));
                String useLoadBalancedConnection = (String)ConfigFile.findInList(connectionProperties, "USELOADBALANCING");
                if(useLoadBalancedConnection.equalsIgnoreCase("TRUE"))
                {
                	dsExtractorParam.setMessageServerSystem(true);
                    String sapMessServer = (String)ConfigFile.findInList(connectionProperties, "SAPMESSERVER");
                    dsExtractorParam.setMsgServer(checkNullForLogger("Message Server",sapMessServer));
                    String group = (String)ConfigFile.findInList(connectionProperties, "SAPGROUP");
                    dsExtractorParam.setGroupName(checkNullForLogger("Group",group));
                    String msgsvrRouterString = (String)ConfigFile.findInList(connectionProperties, "MSGSVRROUTERSTRING");
                    dsExtractorParam.setRouterString(msgsvrRouterString);
                    String sapSysId = (String)ConfigFile.findInList(connectionProperties, "SAPSYSID");
                    dsExtractorParam.setSapSystemId(checkNullForLogger("System ID",sapSysId));
                } else
                {
                    String sapAppServer = (String)ConfigFile.findInList(connectionProperties, "SAPAPPSERVER");
                    dsExtractorParam.setSapApplicationServerHostName(checkNullForLogger("Application Server", sapAppServer));
                    String sapSysNum = (String)ConfigFile.findInList(connectionProperties, "SAPSYSNUM");
                    dsExtractorParam.setSapSystemNr(checkNullForLogger("System Number", sapSysNum));
                }
                String usedefaultConnection = userStageProperties.getProperty("USEDEFAULTSAPLOGON");
                DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {
                    (new StringBuilder("usedefaultConnection: ")).append(usedefaultConnection).toString()
                });
                if(usedefaultConnection.equals("1"))
                {
                    String defaultUserName = (String)ConfigFile.findInList(connectionProperties, "DEFAULTUSERNAME");
                    dsExtractorParam.setSapUser(checkNullForLogger("Default UserName", defaultUserName));
                    String defaultClient = (String)ConfigFile.findInList(connectionProperties, "DEFAULTCLIENT");
                    int clientNum = 0;
                    if(!defaultClient.trim().isEmpty())
                    {
                        try
                        {
                            clientNum = NumberFormat.getNumberInstance().parse(defaultClient).intValue();
                        }
                        catch(ParseException e)
                        {
                            throw new IOException();
                        }
                    }
                    dsExtractorParam.setSapClient(checkNullForLogger("Default ClientNumer", String.valueOf(clientNum)));
                    String defaultPassword = (String)ConfigFile.findInList(connectionProperties, "DEFAULTPASSWORD");
                    String password = Utilities.convertConfigFilePW(defaultPassword);
                    dsExtractorParam.setSapPassword(password);
                    String defaultLanguage = (String)ConfigFile.findInList(connectionProperties, "DEFAULTLANGUAGE");
                    dsExtractorParam.setSapLanguage(checkNullForLogger("Default Language", defaultLanguage));
                }
                break;
            }
        }

    }

    public boolean validateDSSAPHOME()
    {
        boolean isvalid = false;
        String dsSapHome = System.getenv("DSSAPHOME");
        if(dsSapHome == null || dsSapHome.length() == 0)
        {
            DsSapExtractorLogger.fatal("Environment variable DSSAPHOME NOT SET");
        } else
        {
            StringBuffer path = new StringBuffer();
            path.append(dsSapHome).append(BS).append("DSSAPConnections").append(BS).append(userStageProperties.getProperty("CONNECTIONNAME")).append(BS).append("DeltaExtract").append(BS);
            dsExtractorParam.setFilePath(path.toString());
            isvalid = true;
        }
        return isvalid;
    }
    
    public void addwait()
    {
    	DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {(new StringBuilder("++++++ add wait: ")).append(dsExtractorParam.getDataStageNodeId())});
    	long currenttime = System.currentTimeMillis();
    	while(!((System.currentTimeMillis()-currenttime)>2000))
    	{
    		
    	}
    	DsSapExtractorLogger.writeToLogFile(Level.FINE, "DeltaExtract_10000", new Object[] {(new StringBuilder("----- add wait: ")).append(dsExtractorParam.getDataStageNodeId())});
    }
    
    public String fetchFilterConditions(String pat)
	{
		final String FILTERPATTERN = ""+(char)92+(char)123+(char)40+(char)46+(char)43+(char)63+(char)41+(char)92+(char)125;//\{(.+?)\}
		StringBuffer strbuf = new StringBuffer();
		Pattern pattern = Pattern.compile(FILTERPATTERN);
		Matcher matcher = pattern.matcher(pat);
		while(matcher.find())
		{
			strbuf.append(matcher.group(0)).append(TILDA);
		}
		return strbuf.toString();
	}
    
    public boolean deleteDirectory(File file) throws IOException
    {
    	addwait();
    	Path directory = Paths.get(file.getAbsolutePath());
    	   Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
    		   @Override
    		   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    			   Files.delete(file);
    			   return FileVisitResult.CONTINUE;
    		   }

    		   @Override
    		   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
    			   Files.delete(dir);
    			   return FileVisitResult.CONTINUE;
    		   }
    	   });
		return true;
    }
}