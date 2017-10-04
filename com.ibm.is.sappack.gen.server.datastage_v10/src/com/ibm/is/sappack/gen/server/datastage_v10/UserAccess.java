//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.server.datastage_v10
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.datastage_v10;


import java.rmi.RemoteException;

import com.ibm.iis.isf.security.SessionExpiredException;
import com.ibm.iis.isf.security.auth.AuthorizationService;
import com.ibm.iis.isf.service.ServiceFactory;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;


/**
 * Class that determines the highest user role to be retrieved against a specified project.
 */
public class UserAccess
{
    // These are for the user roles
    public static final int DSR_UROLE_NONE                            = 0;	// Project level
    public static final int DSR_UROLE_OPERATOR                        = 1;	// Project level
    public static final int DSR_UROLE_DEVELOPER                       = 2;	// Project level
    public static final int DSR_UROLE_PRODMGR                         = 3;	// Project level - Developer + can import in protected project
    public static final int DSR_UROLE_SUPEROPERATOR                   = 4;	// Project level - operator +  can view job designs in designer
    public static final int DSR_UROLE_ADMINISTRATOR                   = 5;	// Product level - admin
    public static final int DSR_UROLE_DSUSER                          = 6; // Product level - can use DataStage

    // Roles
    public static final String ROLE_DSTAGE_NO_ROLE                    = "No User role";               //$NON-NLS-1$
    public static final String ROLE_DSTAGE_UNKNOWN_ROLE               = "Unknown user role";          //$NON-NLS-1$
    public static final String ASCL_ROLE_DSTAGE_PRODUCT_USER          = "DataStageUser";              //$NON-NLS-1$
    public static final String ASCL_ROLE_DSTAGE_PRODUCT_ADMINISTRATOR = "DataStageAdmin";             //$NON-NLS-1$
    public static final String ASCL_ROLE_DSTAGE_PROJECT_PRODMAN       = "DataStageProductionManager"; //$NON-NLS-1$
    public static final String ASCL_ROLE_DSTAGE_PROJECT_DEVELOPER     = "DataStageDeveloper";         //$NON-NLS-1$
    public static final String ASCL_ROLE_DSTAGE_PROJECT_SUPEROPERATOR = "DataStageSuperOperator";     //$NON-NLS-1$
    public static final String ASCL_ROLE_DSTAGE_PROJECT_OPERATOR      = "DataStageOperator";          //$NON-NLS-1$

   public static final String UA_HOST_PROJECT_SEPARATOR               = "/";                          //$NON-NLS-1$

   private AuthorizationService m_authorizationService = null;
    
   
	static String copyright() {
		return com.ibm.is.sappack.gen.server.datastage_v10.Copyright.IBM_COPYRIGHT_SHORT;
	}


	public static String getUserRoleString(int userrole) {
		switch (userrole) {
		case DSR_UROLE_NONE:
			return ROLE_DSTAGE_NO_ROLE;
		case DSR_UROLE_OPERATOR:
			return ASCL_ROLE_DSTAGE_PROJECT_OPERATOR;
		case DSR_UROLE_DEVELOPER:
			return ASCL_ROLE_DSTAGE_PROJECT_DEVELOPER;
		case DSR_UROLE_PRODMGR:
			return ASCL_ROLE_DSTAGE_PROJECT_PRODMAN;
		case DSR_UROLE_SUPEROPERATOR:
			return ASCL_ROLE_DSTAGE_PROJECT_SUPEROPERATOR;
		case DSR_UROLE_ADMINISTRATOR:
			return ASCL_ROLE_DSTAGE_PRODUCT_ADMINISTRATOR; 
		case DSR_UROLE_DSUSER:
			return ASCL_ROLE_DSTAGE_PRODUCT_USER;
		default:
			return ROLE_DSTAGE_UNKNOWN_ROLE;
		}
	}


   /**
    * @throws DSAccessException 
    * @throws DSServiceException
   */
   public UserAccess() throws DSAccessException  
   {        
 		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

   	try
      {
   		m_authorizationService = ServiceFactory.getInstance().getService(AuthorizationService.class);
      }         
   	catch (Exception e)
   	{
   		throw new DSAccessException("120900E", Constants.NO_PARAMS, e);
   	}

 		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
    }


    /**
     * @param authorizationService
     */
    public UserAccess(AuthorizationService authorizationService)
    {
        m_authorizationService = authorizationService;
    }      


    /**
     * Method retrieves the highest datastage role associated with a user in relation to a specified project.
     * @param hostname
     * @param project
     * @return
     * @throws SessionExpiredException
     * @throws RemoteException
     */
	public int getDSUserRole(String hostName, String project) throws SessionExpiredException, RemoteException
	{
		int userProjectRole = UserAccess.DSR_UROLE_NONE;
	    	   	    
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		int userRole = getDSUserRole();
		if (userRole == DSR_UROLE_NONE || userRole == DSR_UROLE_DSUSER)
		{
			userProjectRole = getDSProjectUserRole(hostName, project);
			if (userProjectRole == UserAccess.DSR_UROLE_NONE)
			{
				userProjectRole = userRole;
			}
		}
		else {
			userProjectRole = userRole;
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("project user role = " + userProjectRole);
		}

		return userProjectRole;
	}    
   
	
   /**
    * @return
    * @throws RemoteException
    * @throws SessionExpiredException
    */
	private int getDSUserRole() throws RemoteException
	{
		int role = DSR_UROLE_NONE;

		String[] roles = m_authorizationService.getUserRoles();
		role = getHighestRole(roles);

		return role;        
   }

	
    /**
     * @param hostName
     * @param project
     * @return
     * @throws SessionExpiredException
     * @throws RemoteException
     */
	private int getDSProjectUserRole(String hostName, String project) throws RemoteException
	{
		int role = DSR_UROLE_NONE;

		String[] roles = m_authorizationService.getUserRolesForOwner(hostName + UA_HOST_PROJECT_SEPARATOR + project);       
		role = getHighestRole(roles);
        
		return role;   
	}


   /**
    * @param roles
    * @return
    */
	private int getHighestRole(String[] roles)
	{
		int highestRole = DSR_UROLE_NONE;

		if (roles != null) {
			for(int i = 0; i < roles.length; i++) {
				String userRole = roles[i];
				if (userRole.equals(ASCL_ROLE_DSTAGE_PRODUCT_USER)) {
					if (highestRole != DSR_UROLE_ADMINISTRATOR &&
						 highestRole != DSR_UROLE_PRODMGR &&
						 highestRole != DSR_UROLE_DEVELOPER &&
						 highestRole != DSR_UROLE_SUPEROPERATOR &&
						 highestRole != DSR_UROLE_OPERATOR)                             
               {
						highestRole = DSR_UROLE_DSUSER;
               }
				}
				else if(userRole.equals(ASCL_ROLE_DSTAGE_PRODUCT_ADMINISTRATOR)) {
							highestRole = DSR_UROLE_ADMINISTRATOR;
							break;
						}
            else if (userRole.equals(ASCL_ROLE_DSTAGE_PROJECT_PRODMAN)) {
            			if (highestRole != DSR_UROLE_ADMINISTRATOR) {
                        highestRole = DSR_UROLE_PRODMGR;
            			}              
            		}
            else if (userRole.equals(ASCL_ROLE_DSTAGE_PROJECT_DEVELOPER)) {
            			if (highestRole != DSR_UROLE_ADMINISTRATOR &&
                         highestRole != DSR_UROLE_PRODMGR) {
            				 highestRole = DSR_UROLE_DEVELOPER;
            			}
                	}
            else if (userRole.equals(ASCL_ROLE_DSTAGE_PROJECT_SUPEROPERATOR)) {
                    	if (highestRole != DSR_UROLE_ADMINISTRATOR &&
                    		 highestRole != DSR_UROLE_PRODMGR       &&
                    		 highestRole != DSR_UROLE_DEVELOPER) {
                    		highestRole = DSR_UROLE_SUPEROPERATOR;
                    	}                        
                	}
            else if (userRole.equals(ASCL_ROLE_DSTAGE_PROJECT_OPERATOR)) {
            			if (highestRole != DSR_UROLE_ADMINISTRATOR &&
            				 highestRole != DSR_UROLE_PRODMGR       &&
            				 highestRole != DSR_UROLE_DEVELOPER     &&
            				 highestRole != DSR_UROLE_SUPEROPERATOR) {
            				highestRole = DSR_UROLE_OPERATOR;
            			}
                	}
            		else {
            			//If no DataStage privileges then set role to none
            			if (highestRole != DSR_UROLE_ADMINISTRATOR &&
            				 highestRole != DSR_UROLE_PRODMGR       &&
            				 highestRole != DSR_UROLE_DEVELOPER     &&
            				 highestRole != DSR_UROLE_SUPEROPERATOR &&
            				 highestRole != DSR_UROLE_OPERATOR      &&
                         highestRole != DSR_UROLE_DSUSER) {
            				highestRole = DSR_UROLE_NONE;
            			}               
            		}
			} // end of for(int i = 0; i < roles.length; i++)
		} // end of if (roles != null)
        
		return highestRole;
	}

}
