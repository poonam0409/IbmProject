//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.jobgenerator;


import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsResponse;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.RequestJobType;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.XMLUtils;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.DSSapConnectionFileConverter;
import com.ibm.is.sappack.gen.server.datastage.DataStageAccessManager;
import com.ibm.is.sappack.gen.server.util.ColumnData;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.TableData;


public abstract class BaseIDocJob extends BaseJob {

   public BaseIDocJob(ServiceToken parSrvcToken, RequestJobType parJobType, JobGeneratorRequest parJobReqInfo,
                      Map<String, ModelInfoBlock> physModelID2TableMap) throws BaseException {
        super(parSrvcToken, parJobType, parJobReqInfo, physModelID2TableMap);
    }

    protected String generatePDRLocator(String SAPConnectionName, String IDocType, TableData segTableData, TableData tableDataArr[]) throws DSAccessException {
        
        // Get the SAP hostname from the SAP connection definition
        // This may return empty if a parameter is used for the SAP connection, which is fine.
        // The SAP hostname for data lineage will be derived from the OMD metadata in this case.
        String hostName = getSapHostname(SAPConnectionName);
        
        String locator = ""; //$NON-NLS-1$
        
        // Locator "header"
        locator += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$
        locator += "<locator xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" xs:noNamespaceSchemaLocation=\"PublicDataSourceLocator.xsd\">"; //$NON-NLS-1$

        // Hostname variable
        locator += "<paramDef name=\"hostname\" defaultValue=\"" + hostName + "\" runtimeValueLocation=\"OMD_HOSTNAME\"/>"; //$NON-NLS-1$ //$NON-NLS-2$
        locator += "<referenceToFields name=\"contains_DataField\"/>"; //$NON-NLS-1$

        // Data item definition (segment definition)
        locator += "<selectObject name=\"" + segTableData.getSegmentDefinition() + "\" subtype=\"SAP_IDOC\" fromClass=\"ASCLModel::DataItemDef\">"; //$NON-NLS-1$ //$NON-NLS-2$
        locator += "<withReference name=\"defines_DataItemBase\" restrictTo=\"ASCLModel::DataField\">"; //$NON-NLS-1$

        // Data field (segment type)
        locator += "<toObject name=\"" + segTableData.getSegmentType() + "\" subtype=\"SAP_IDOC_SEG_TYPE\">"; //$NON-NLS-1$ //$NON-NLS-2$

        // Process all parent segments
        int parents = 0;
        TableData curSegment = segTableData;
        boolean hasParent = false;
        do {
            if (curSegment.getParentSegment() != null) {
                curSegment = findParentSegmentTable(tableDataArr, curSegment.getParentSegment());
                if (curSegment != null) {
                    hasParent = true;
                    parents++;
                    
                    locator += "<withReference name=\"of_DataItemDef\">"; //$NON-NLS-1$
        
                    // Data item definition (segment definition)
                    locator += "<toObject name=\"" + curSegment.getSegmentDefinition() + "\" subtype=\"SAP_IDOC\">";  //$NON-NLS-1$ //$NON-NLS-2$
                    locator += "<withReference name=\"defines_DataItemBase\" restrictTo=\"ASCLModel::DataField\">"; //$NON-NLS-1$
        
                    // Data field (segment type)
                    locator += "<toObject name=\"" + curSegment.getSegmentType() + "\" subtype=\"SAP_IDOC_SEG_TYPE\">"; //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    hasParent = false;
                }
            } else {
                hasParent = false;
            }
        } while (hasParent);

        locator += "<withReference name=\"of_DataCollection\">"; //$NON-NLS-1$

        // Data collection (IDoc type)
        locator += "<toObject name=\"" + IDocType + "\" subtype=\"SAP_IDOC\">"; //$NON-NLS-1$ //$NON-NLS-2$
        locator += "<withReference name=\"of_DataFile\">"; //$NON-NLS-1$
        // Data field (IDoc type dummy parent object)
        locator += "<toObject name=\"" + IDocType + "\" subtype=\"SAP_IDOC\">"; //$NON-NLS-1$ //$NON-NLS-2$
        locator += "<withReference name=\"hostedBy_HostSystem\">"; //$NON-NLS-1$
        locator += "<toObject name=\"{hostname}\" subtype=\"SAP\"/>"; //$NON-NLS-1$
        locator += "</withReference>"; //$NON-NLS-1$
        locator += "</toObject>"; //$NON-NLS-1$
        locator += "</withReference>"; //$NON-NLS-1$
        locator += "</toObject>"; //$NON-NLS-1$

        // Close parent segment tags
        while (parents > 0) {
            locator += "</withReference>"; //$NON-NLS-1$
            locator += "</toObject>"; //$NON-NLS-1$
            locator += "</withReference>"; //$NON-NLS-1$
            locator += "</toObject>"; //$NON-NLS-1$
            parents--;
        }

        locator += "</withReference>"; //$NON-NLS-1$
        locator += "</toObject>"; //$NON-NLS-1$
        locator += "</withReference>"; //$NON-NLS-1$
        locator += "</selectObject>"; //$NON-NLS-1$
        locator += "</locator>"; //$NON-NLS-1$
        
        return locator;
    }

    private TableData findParentSegmentTable(TableData tableDataArr[], String segmentType) {
        TableData parent = null;
        for (int tblIdx = 0; tblIdx < tableDataArr.length; tblIdx++) {
            if (segmentType.equals(tableDataArr[tblIdx].getSegmentType())) {
                parent = tableDataArr[tblIdx];
            }
        }
        return parent;
    }

    private String getSapHostname(String SAPConnectionName) throws DSAccessException {
        TraceLogger.entry();
        String SAPHostname = ""; //$NON-NLS-1$
        
        DataStageAccessManager accessManager = DataStageAccessManager.getInstance();
        String curSAPConnectionsXML = accessManager.getAllSapConnections(_JobRequestInfo.getDSHostName(), 
                                                                         _JobRequestInfo.getDSServerRPCPort(), 
                                                                         _JobRequestInfo.getDSProjectName(), 
                                                                         getServiceToken());
        
        if(!curSAPConnectionsXML.equals(DSSapConnectionFileConverter.XMLTAG_NO_DSSAPCONNECTIONS)) {
            try {
                Node rootNode = XMLUtils.getRootElementFromXML(curSAPConnectionsXML);
                NodeList connectionNodes = rootNode.getChildNodes();
                boolean foundIt = false;
                boolean useLB = false;
                String sapAppServer = ""; //$NON-NLS-1$
                String sapMessServer = ""; //$NON-NLS-1$

                for (int i = 0; i < connectionNodes.getLength(); i++) {
                    foundIt = false;
                    SAPHostname = ""; //$NON-NLS-1$
                    Node node = connectionNodes.item(i);
                    NamedNodeMap attributes = node.getAttributes();
                    for (int a = 0; a < attributes.getLength(); a++) {
                        Node attribute = attributes.item(a);
                        /* look for the requested connection */
                        if (attribute.getNodeName().equals(GetAllSapConnectionsResponse.CONNECTION_NAME_ATTRIBUTE)) {
                            if (attribute.getNodeValue().equals(SAPConnectionName)) {
                                foundIt = true;
                            }
                        }
                        /* look for the relevant attributes */
                        if (attribute.getNodeName().equals(GetAllSapConnectionsResponse.LOAD_BALANCING_ATTRIBUTE)) {
                            useLB = (GetAllSapConnectionsResponse.LOAD_BALANCING_TRUE.equals(attribute.getNodeValue()));
                        }
                        if (attribute.getNodeName().equals(GetAllSapConnectionsResponse.SAP_APP_SERVER_ATTRIBUTE)) {
                            sapAppServer = attribute.getNodeValue();
                        }
                        if (attribute.getNodeName().equals(GetAllSapConnectionsResponse.SAP_MESS_SERVER_ATTRIBUTE)) {
                            sapMessServer = attribute.getNodeValue();
                        }
                    }
                    if (foundIt) {
                        if (useLB) {
                            SAPHostname = sapMessServer;
                        } else {
                            SAPHostname = sapAppServer;
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                // something went wrong - return an empty String
                TraceLogger.traceException(e);
                SAPHostname = ""; //$NON-NLS-1$
            }
        }
        
        TraceLogger.exit("SAP hostname: " + SAPHostname); //$NON-NLS-1$
        return SAPHostname;
    }

    protected String generateFieldMap(String SAPConnectionName, String IDocType, TableData segTableData, TableData tableDataArr[]) {
        String fieldMap = ""; //$NON-NLS-1$
        
        // Field map "header"
        fieldMap += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$
        fieldMap += "<map xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" xs:noNamespaceSchemaLocation=\"PublicDataSourceFieldMap.xsd\">"; //$NON-NLS-1$
        
        // Iterate over the columns
        ColumnData[] columnsArr = segTableData.getColumnData();
        ColumnData column;
        
        for (int colIdx = 0; colIdx < columnsArr.length; colIdx++) {
            column = columnsArr[colIdx];
            fieldMap += "<entry stageColumnName=\"" + column.getName() + "\" fieldName=\"" + column.getName() + "\"/>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        fieldMap += "</map>"; //$NON-NLS-1$

        return fieldMap;
    }
    
}
