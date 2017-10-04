package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.model.multipart.BufferedInMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Security;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportFileContainer;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportFileType;

@Path("/bdr/bphimportfile")
public class BphImportFileService {

	private static final Pattern CD_SEMICOLON_PATTERN = Pattern.compile("\\s*+;\\s*+");
	private static final Pattern CD_NON_WHITESPACE_PATTERN = Pattern.compile("\\s*+(\\S*)\\s*+");
	private static final Pattern CD_BETWEEN_QUOTES_PATTERN = Pattern.compile("\"([^\"]*)\"");

	private static final String CD_HEADER = "Content-Disposition";
	private static final String CD_HEADER_NAME = "name";
	private static final String CD_HEADER_FILENAME = "filename";
	private static final String CD_IFRAME_TYPE_STRING = "uploadedfile";
	private static final String CD_HTML5_TYPE_STRING = "s[]";
	private static final String CD_FLASH_TYPE_STRING = "uploadedfileFlash";
	private static final String CD_FLASH_USERAGENT = "Shockwave Flash";
	EntityManager manager = JPAResourceFactory.getEntityManager();
	UserTransaction transaction = JPAResourceFactory.getUserTransaction();

	enum UPLOAD_TYPE {
		HTML5, HTML, FLASH
	};

	enum UPLOAD_RESPONSE_CODE {
		NO_FILE_UPLOADED(4000, "No file is selected."),
		FILE_NOT_VALID(5000, "File is no valid data model."),
		FILE_SUCCESSFULLY_PARSED(2000, "File has been successfully parsed."),
		INTERNAL_ERROR(6000, "");

		private final int statusCode;
		private final String message;

		private UPLOAD_RESPONSE_CODE(int statusCode, String message) {
			this.statusCode = statusCode;
			this.message = message;
		}

		public int getStatusCode() {
			return this.statusCode;
		}

		public String getMessage() {
			return this.message;
		}
	};

	private UPLOAD_TYPE uploadType;

	@Path("/retrieve")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BphImportFileContainer getImportFileContainer(@Context HttpServletRequest httpServletRequest) {
		HttpSession session = httpServletRequest.getSession();
		return (BphImportFileContainer) session.getAttribute(Constants.SESSION_ATTRIBUTE_NAME_BPH_IMPORT_FILE);
	}

	
	
	
	@POST
	@Path("/sapViewUpload/{sapViewParam}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public String uploadSapViewImportFile(@PathParam("sapViewParam") String sapViewParam,@Context HttpHeaders httpHeaders, @Context HttpServletRequest httpServletRequest,
			BufferedInMultiPart bimp) throws NotSupportedException, SystemException, IllegalStateException, SecurityException, HeuristicMixedException, HeuristicRollbackException, RollbackException {

		// standard upload type is supposed to be HTML
		// (e.g. anything send by dojo.io.frame.send)
		uploadType = UPLOAD_TYPE.HTML;

		List<InPart> parts = bimp.getParts();
		String result = null;
		
		try {
			// compare webApp context root with referrer context root to prevent CSRF
			// abort when not the same
			if (!Security.checkRefererURL(httpServletRequest)){
				result = composeResult(
						UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getStatusCode(),
						UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getMessage());
			} else {
				for (int i = 0; i < parts.size(); i++) {
					if (parts.get(i).getHeaders().containsKey(CD_HEADER)) {
						ArrayList<String> list = (ArrayList<String>) parts.get(i).getHeaders().get(CD_HEADER);
						Map<String, String> cdHeaderMap = parseContentDispositionHeader(list.get(0));
						String name = cdHeaderMap.get(CD_HEADER_NAME);
	
						// we need to determine the client type for dojox.form.Uploader
						// so that we are able to give the correct response
						List<String> userAgentHeaders = httpHeaders.getRequestHeader(HttpHeaders.USER_AGENT);
						String userAgent = userAgentHeaders == null ? null : userAgentHeaders.get(0);
						
						if (name != null && name.contains(CD_HTML5_TYPE_STRING)) {
							uploadType = UPLOAD_TYPE.HTML5;
						} else if (name != null && name.equals(CD_FLASH_TYPE_STRING) || CD_FLASH_USERAGENT.equals(userAgent)) {
							uploadType = UPLOAD_TYPE.FLASH;
						} else if (name != null && name.equals(CD_IFRAME_TYPE_STRING)) {
							uploadType = UPLOAD_TYPE.HTML;
					    } else {
							continue;
						}
	
						String filename = cdHeaderMap.get(CD_HEADER_FILENAME);
	
						if (filename != null) {
							 if (filename.toLowerCase().endsWith(BphImportFileType.BDR_CSV.getFileExtension())) {
								InputStream inputStream = parts.get(i).getInputStream();
								BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
								parseSapViewImportFile(reader,sapViewParam);
					        
						        result = composeResult(
										UPLOAD_RESPONSE_CODE.FILE_SUCCESSFULLY_PARSED.getStatusCode(),
										UPLOAD_RESPONSE_CODE.FILE_SUCCESSFULLY_PARSED.getMessage());
							}
							else {
								result = composeResult(
										UPLOAD_RESPONSE_CODE.FILE_NOT_VALID.getStatusCode(),
										UPLOAD_RESPONSE_CODE.FILE_NOT_VALID.getMessage());
							}
						}
						else {
							result = composeResult(
									UPLOAD_RESPONSE_CODE.NO_FILE_UPLOADED.getStatusCode(),
									UPLOAD_RESPONSE_CODE.NO_FILE_UPLOADED.getMessage());
						}
					}
				}
			}
		}
		catch (IOException e) {
			Util.handleBatchException(e);
			try {
				result = composeResult(UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getStatusCode(), e.getStackTrace());
			} catch (JSONException e2) {
				Util.handleBatchException(e2);
			}
		} catch (JSONException e) {
			Util.handleBatchException(e);
		} catch (Exception e) {
			Util.handleBatchException(e);
		}
		return result;
	}
	
	private void parseSapViewImportFile(BufferedReader reader, String sapViewParam) throws IllegalStateException, SecurityException, IOException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, RollbackException
	{
		
        String line;
        boolean headerFlag=true;
    	int tableIndex=0;
    	int fieldIndex=0;
    	int sapViewIndex=0;
    	
        while ((line = reader.readLine()) != null) {
            
        	String[] columnData= line.split(",");
        	//Logic to identify the indexes of Table, Field and SAP View headers
        	if( headerFlag==true){//This will name it run once only
        	for(int j=0;j<columnData.length;j++)
        	{
        		if(columnData[j].equals(Constants.BDR_EXPORT_TABLE_NAME))
        			tableIndex=j;
        		if(columnData[j].equals(Constants.BDR_EXPORT_FIELD_NAME))
        			fieldIndex=j;
        		if(columnData[j].equals(Constants.BDR_EXPORT_FIELD_SAP_VIEW))
        			sapViewIndex=j;
        	}
        	 headerFlag=false;
        	 continue;
        	}
        	
        	
        	if( headerFlag==false){
	        	String tableName=columnData[tableIndex];
	        	String fieldName=columnData[fieldIndex];
	        	String sapViewName=columnData[sapViewIndex];
	        	EntityManager manager = JPAResourceFactory.getEntityManager();
	        	transaction.begin();
	        	//Named query is used to get the value of SAP View for a table column
	        	TypedQuery<Field> query = manager.createNamedQuery("Field.getByNameAndTable",Field.class);
	        	query.setParameter("tableName", tableName);
	        	query.setParameter("fieldName", fieldName);
	        	List<Field> queryResult=query.getResultList();
	        	for(Field qr : queryResult)
	    		{
	        	String tempSapView="";
	        		if(sapViewParam.equalsIgnoreCase("Merge"))
	        			//Merge the importing SAP Views with existing SAP Views based on parameter: sapViewParam
	        			sapViewName=mergeSapViewListStrings(sapViewName,qr.getSapView());
	        	Field updateField=manager.find(Field.class, qr.getFieldId());
	        	//Update the column with the merged/overwritten SAP View
	        	updateField.setSapView(sapViewName);
	        	manager.merge(updateField);
	        	manager.flush();
	        	transaction.commit();
	        	}
        	}
        }	
	}
	
	private String mergeSapViewListStrings(String newSapView,String oldSapView)
	{
	if(oldSapView!=null && !"".equalsIgnoreCase(oldSapView))
	{
		TreeSet<String> sapView = new TreeSet<String>();
		String [] oldSapViewList=oldSapView.split("\\"+Constants.SAPVIEW_SEPARATOR);
		String [] newSapViewList=newSapView.split("\\"+Constants.SAPVIEW_SEPARATOR);
		Collections.addAll(sapView, newSapViewList);
		Collections.addAll(sapView, oldSapViewList);
		String retStr="";
		for(String st: sapView)
		{
			retStr+=st+Constants.SAPVIEW_SEPARATOR;
		}
		newSapView=retStr.substring(0, (retStr.length()-1));
	}
	return newSapView;
	}
	
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public String uploadImportFile(@Context HttpHeaders httpHeaders, @Context HttpServletRequest httpServletRequest,
			BufferedInMultiPart bimp) {

		// standard upload type is supposed to be HTML
		// (e.g. anything send by dojo.io.frame.send)
		uploadType = UPLOAD_TYPE.HTML;

		List<InPart> parts = bimp.getParts();
		String result = null;
		
		try {
			// compare webApp context root with referrer context root to prevent CSRF
			// abort when not the same
			if (!Security.checkRefererURL(httpServletRequest)){
				result = composeResult(
						UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getStatusCode(),
						UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getMessage());
			} else {
				for (int i = 0; i < parts.size(); i++) {
					if (parts.get(i).getHeaders().containsKey(CD_HEADER)) {
						ArrayList<String> list = (ArrayList<String>) parts.get(i).getHeaders().get(CD_HEADER);
						Map<String, String> cdHeaderMap = parseContentDispositionHeader(list.get(0));
						String name = cdHeaderMap.get(CD_HEADER_NAME);
	
						// we need to determine the client type for dojox.form.Uploader
						// so that we are able to give the correct response
						List<String> userAgentHeaders = httpHeaders.getRequestHeader(HttpHeaders.USER_AGENT);
						String userAgent = userAgentHeaders == null ? null : userAgentHeaders.get(0);
						
						if (name != null && name.contains(CD_HTML5_TYPE_STRING)) {
							uploadType = UPLOAD_TYPE.HTML5;
						} else if (name != null && name.equals(CD_FLASH_TYPE_STRING) || CD_FLASH_USERAGENT.equals(userAgent)) {
							uploadType = UPLOAD_TYPE.FLASH;
						} else if (name != null && name.equals(CD_IFRAME_TYPE_STRING)) {
							uploadType = UPLOAD_TYPE.HTML;
					    } else {
							continue;
						}
	
						String filename = cdHeaderMap.get(CD_HEADER_FILENAME);
	
						if (filename != null) {
							if (filename.toLowerCase().endsWith(BphImportFileType.SOLUTION_MANAGER.getFileExtension())) {
								BphImportFileContainer bphImportFile = new BphImportFileContainer();
								bphImportFile.setFileContent(parts.get(i).getInputStream());
								bphImportFile.setFileType(BphImportFileType.SOLUTION_MANAGER);
								httpServletRequest.getSession().setAttribute(Constants.SESSION_ATTRIBUTE_NAME_BPH_IMPORT_FILE, bphImportFile);
								result = composeResult(
										UPLOAD_RESPONSE_CODE.FILE_SUCCESSFULLY_PARSED.getStatusCode(),
										UPLOAD_RESPONSE_CODE.FILE_SUCCESSFULLY_PARSED.getMessage());
							}
							else if (filename.toLowerCase().endsWith(BphImportFileType.BDR_CSV.getFileExtension())) {
								BphImportFileContainer bphImportFile = new BphImportFileContainer();
								bphImportFile.setFileContent(parts.get(i).getInputStream());
								bphImportFile.setFileType(BphImportFileType.BDR_CSV); 
								httpServletRequest.getSession().setAttribute(Constants.SESSION_ATTRIBUTE_NAME_BPH_IMPORT_FILE, bphImportFile);
								result = composeResult(
										UPLOAD_RESPONSE_CODE.FILE_SUCCESSFULLY_PARSED.getStatusCode(),
										UPLOAD_RESPONSE_CODE.FILE_SUCCESSFULLY_PARSED.getMessage());
							}
							else {
								result = composeResult(
										UPLOAD_RESPONSE_CODE.FILE_NOT_VALID.getStatusCode(),
										UPLOAD_RESPONSE_CODE.FILE_NOT_VALID.getMessage());
							}
						}
						else {
							result = composeResult(
									UPLOAD_RESPONSE_CODE.NO_FILE_UPLOADED.getStatusCode(),
									UPLOAD_RESPONSE_CODE.NO_FILE_UPLOADED.getMessage());
						}
					}
				}
			}
		}
		catch (IOException e) {
			Util.handleBatchException(e);
			try {
				result = composeResult(UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getStatusCode(), e.getStackTrace());
			} catch (JSONException e2) {
				Util.handleBatchException(e2);
			}
		} catch (JSONException e) {
			Util.handleBatchException(e);
		}
		return result;
	}

	@DELETE
	@Path("/delete")
	public void deleteImportFile(@Context HttpServletRequest request) {
		request.getSession().removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BPH_IMPORT_FILE);
	}

	private Map<String, String> parseContentDispositionHeader(String value) {
		String[] all = CD_SEMICOLON_PATTERN.split(value);
		int i = 0;
		Matcher m = (CD_NON_WHITESPACE_PATTERN).matcher(all[i]);
		String headerType = "";

		if (m.find()) {
			headerType = m.group(1);
		}

		Map<String, String> mapToReturn = new HashMap<String, String>();

		mapToReturn.put(CD_HEADER, headerType);

		for (i = 1; i < all.length; i++) {

			if (all[i].startsWith(CD_HEADER_NAME)) {
				m = (CD_BETWEEN_QUOTES_PATTERN).matcher(all[i]);

				if (m.find()) {
					mapToReturn.put(CD_HEADER_NAME, m.group(1));
				}
			}
			else if (all[i].startsWith(CD_HEADER_FILENAME)) {
				m = (CD_BETWEEN_QUOTES_PATTERN).matcher(all[i]);

				if (m.find()) {
					mapToReturn.put(CD_HEADER_FILENAME, m.group(1));
				}
			}
		}

		return mapToReturn;
	}

	@SuppressWarnings("unchecked")
	private String composeFlashReturn(JSONArray jsonArray) throws JSONException {
		StringBuffer flashString = new StringBuffer();

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = ((JSONObject) jsonArray.get(i));
			Set<String> set = jsonObject.keySet();

			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String key = it.next();
				flashString.append(key).append("=").append(jsonObject.get(key)).append(",");
			}
		}

		String returnString = flashString.toString();
		returnString = returnString.substring(0, returnString.length() - 1);

		return returnString;
	}
	
	private String composeResult(int statusCode, Object errorObject) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("statusCode", statusCode);
		jsonObject.put("message", errorObject);
		jsonArray.add(jsonObject);

		switch (uploadType) {
			case HTML: {
				return ("<html><body><textarea>" + jsonArray.toString() + "</textarea></body></html>");
			}
			case HTML5: {
				return jsonArray.toString();
			}
			case FLASH: {
				return composeFlashReturn(jsonArray);
			}
			default: {
				return null;
			}
		}
	}
}
