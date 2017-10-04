package com.ibm.is.sappack.cw.app.services.rdm.jaxrs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmMapping;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmSet;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.TranscodingTable;
import com.ibm.is.sappack.cw.app.services.AbstractThreadedService;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.DBOperations;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmMappingClient;
import com.ibm.is.sappack.cw.app.services.rdm.threads.RdmMappingsLoaderThread;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/mapping")
public class RdmMappingsService extends AbstractThreadedService {
	
	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_RDM_TT_IMPORT_THREAD;
	private static final String CLASS_NAME = RdmMappingsService.class.getName();
	
	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}
	
	private static final String STATUS_NEW = "NEW";
	private static final String STATUS_LOADED = "LOADED";
	private static final String STATUS_UPDATE = "UPDATE";
	private static final String STATUS_UNDEFINED = "UNDEFINED";
	private static final String CONFLICTS_ARRAY_NAME = "Conflicts";
	private static final String LOADDATE = "DateLoaded";
	private static final String LVERSION = "LoadedVersion";
	private final EntityManager mgr;

	public RdmMappingsService() {
		super();
		final String METHOD_NAME = "DataMappingService()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		this.mgr = JPAResourceFactory.getEntityManager();
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StreamingOutput getMappings(@Context HttpServletRequest request) {
		final String METHOD_NAME = "getMappings(@Context HttpServletRequest request)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		JSONArray result = null;
		try {
			JSONArray rmdMappings = getClient(request).getMappings();
			logger.fine("Got " + rmdMappings.size() + " mappings from RDM");
			HashMap<String, HashMap<String, JSONObject>> rdmMappingMap = hashMapFromRDMJSON(rmdMappings);
			result = buildMappingsView(rdmMappingMap, request.getSession());
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		return Util.output(result.toString());
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput publishTranscondingTables(@Context HttpServletRequest request,
			@Context ServletContext servletContext, String input) {
		final String METHOD_NAME = "publishTranscodingTables()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		JSONObject response = new JSONObject();
		try {
			JSONArray selectedMappings = new JSONArray(input);
			Map<String, Map<String, JSONObject>> selectedMappingMap = requestJsonToMap(selectedMappings);
			Publisher publisher = Util.getJmsPublisher(servletContext);

			RdmMappingsLoaderThread loaderThread = new RdmMappingsLoaderThread(getClient(request), publisher,
					selectedMappingMap, request.getSession());
			request.getSession().setAttribute(getSessionAttributeName(), loaderThread);

			response.put("maximum", selectedMappingMap.size());
			logger.fine("Processing " + selectedMappingMap.size() + " RDM Mappings");
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(response.toString());
	}

	private RdmMappingClient getClient(HttpServletRequest request) {
		final String METHOD_NAME = "getClient(HttpServletRequest request)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		// take password from session and create the client
		String rdmPassword = (String) request.getSession().getAttribute(
				Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD);
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return new RdmMappingClient(rdmPassword);
	}

	// parses the mapping array from RDM and
	// checks for each mapping the relevance for us(do we know the
	// targetSet?, is the type of src and tgt equal?)
	// the relevant mappings will be returned
	private HashMap<String, HashMap<String, JSONObject>> hashMapFromRDMJSON(JSONArray mappings)
			throws JSONException {
		final String METHOD_NAME = "hashMapFromRDMJSON(JSONArray mappings)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		int i = 0;
		HashMap<String, HashMap<String, JSONObject>> result = new HashMap<String, HashMap<String, JSONObject>>();
		while (i < mappings.size()) {
			JSONObject mapping = mappings.getJSONObject(i);
			// The mapping's target set should be known. Otherwise it is irrelevant for us.
			ReferenceTable rTable = DBOperations.findReferenceTableForRDMSet(
					mapping.getString(Constants.RDM_MAPPING_TGT), mgr);
			if (rTable != null) {
				// get the PK for the Transcoding table
				int newVersion;
				try {
					newVersion = Integer.parseInt(mapping.getString(Constants.RDM_MAPPING_VERSION));
				} catch (NumberFormatException e) {
					logger.fine("Ignoring mapping " + mapping.getString(Constants.RDM_MAPPING_NAME)
							+ " because of version type");
					i++;
					continue;
				}
				String ttName = rTable.getTranscodingTable().getName();
				HashMap<String, JSONObject> mappingsMap = result.get(ttName);
				String mapID = mapping.getString(Constants.RDM_MAPPING_BASE_ID);
				if (mappingsMap == null) {
					// there are now mappings in the result map for this
					// transcodingtable
					// so create an new HashMap
					mappingsMap = new HashMap<String, JSONObject>();
				} else {
					// the HashMap exist already, check if there is already a
					// mapping
					// with the same ID in the HashMap if not put it in the map
					if (mappingsMap.containsKey(mapID)) {
						// there is a second mapping with the same ID
						// check if this mapping has a greater version than the
						// mapping that is already in the hashmap
						JSONObject storedObject = mappingsMap.get(mapID);
						int storedVersion = Integer.parseInt(storedObject.getString(Constants.RDM_MAPPING_VERSION));
						if (storedVersion >= newVersion) {
							// the latest version is already in the map, so do
							// nothing just skip this mapping
							i++;
							continue;
						}

					}
				}
				mappingsMap.put(mapID, mapping);
				result.put(ttName, mappingsMap);
			}
			i++;
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}

	private JSONArray buildMappingsView(HashMap<String, HashMap<String, JSONObject>> rdmMappingsMap, HttpSession httpSession)
			throws JSONException {
		// this Method builds a complete JSONArray of all Transcodingtables and
		// the Mappings that are related to them, this result will be send to
		// the client
		final String METHOD_NAME = "buildMappingsView(HashMap<TranscodingTablePk, HashMap<String, JSONObject>> rdmMappingsMap)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		JSONArray result = new JSONArray();
		List<TranscodingTable> tables = mgr.createNamedQuery("TranscodingTable.retrieveAll", TranscodingTable.class)
				.getResultList();
		for (TranscodingTable table : tables) {
			boolean isInCWDB = DBOperations.transcodingTableExistsInCWDB(table.getName());
			// get all Mappings for this TT form the database
			List<RdmMapping> databaseMappings = DBOperations.getAllMappingsForTT(table.getName(), mgr);
			// create a JSONObject that contains all information to display for
			// this TT
			// this information will be stored in the complete result of this
			// method
			JSONObject rowPreResult = new JSONObject();
			// put first information in rowPreResult there are the same for each
			// mapping of this tt
			String transcodingTableName = table.getName();
			rowPreResult.put(Constants.T_TABLE_NAME, transcodingTableName);
			rowPreResult.put("IsTTInCWDB", isInCWDB);
			rowPreResult.put("TABLECONDITION", table.getCond());
			rowPreResult.put("SELECTED", false);
			rowPreResult.put("DISABLED", false);
			JSONObject finalRowResult;
			// get all Mappings for this TT that came from RDM
			HashMap<String, JSONObject> mappingsFromRDM = rdmMappingsMap.get(table.getName());
			// we need this hashmap to have to the actual name of a mapping in
			// rdm
			HashMap<String, String> mappingsInRDMAndDB = new HashMap<String, String>();
			if ((databaseMappings == null || databaseMappings.size() == 0) && mappingsFromRDM == null) {
				finalRowResult = (JSONObject) rowPreResult.clone();
				// there is a tt without relations to mappings in the database
				finalRowResult.put(Constants.RDM_MAPPING_BASE_ID, "");
				finalRowResult.put(Constants.RDM_MAPPING_NAME, "");
				finalRowResult.put(LVERSION, "");
				finalRowResult.put(Constants.AVAILABLE_RDM_VERSION, "");
				finalRowResult.put(LOADDATE, "");
				finalRowResult.put(Constants.LEGACYID, "");
				finalRowResult.put(Constants.RDM_MAPPING_TGT, "");
				finalRowResult.put(Constants.RDM_MAPPING_SRC, "");
				finalRowResult.put(Constants.NUMBER_OF_MAPPED_VALUES, "");
				finalRowResult.put(Constants.NUMBER_OF_SRC_VALUES, "");
				finalRowResult.put(Constants.STATUS, "");
				// this transcodingtable is finished here loop will continue
				// with next tt
				result.put(finalRowResult);
			} else {
				for (RdmMapping mapping : databaseMappings) {
					finalRowResult = (JSONObject) rowPreResult.clone();
					String mapId = mapping.getRdmId();
					finalRowResult.put(Constants.RDM_MAPPING_BASE_ID, mapId);
					finalRowResult.put(LVERSION, mapping.getVersion());
					finalRowResult.put(LOADDATE, mapping.getLastLoad().getTime());
					finalRowResult.put(Constants.LEGACYID, mapping.getSourceRdmId());
					finalRowResult.put(Constants.RDM_MAPPING_TGT, mapping.getTargetRdmId());
					finalRowResult.put(Constants.RDM_MAPPING_SRC, mapping.getSourceRdmId());
					finalRowResult.put(Constants.NUMBER_OF_SRC_VALUES, mapping.getSourceKeys());
					finalRowResult.put(Constants.NUMBER_OF_MAPPED_VALUES, mapping.getValueMappings());
					finalRowResult.put(Constants.LEGACYID, getLegacyID(mapping.getSourceRdmId()));
					// there can't be any conflicts for stored mappings so just
					// put an empty array
					// for conflicts
					finalRowResult.put(CONFLICTS_ARRAY_NAME, new JSONArray());
					// is this mapping still in RDM Hub?
					if (mappingsFromRDM != null && mappingsFromRDM.containsKey(mapping.getRdmId())) {
						// we got a mapping from RDM Hub that already exists in our
						// database
						// get the JSONObject for this mapping
						JSONObject rdmMap = mappingsFromRDM.get(mapping.getRdmId());
						finalRowResult.put(Constants.RDM_MAPPING_VERSION_ID,
								rdmMap.getString(Constants.RDM_MAPPING_VERSION_ID));
						finalRowResult.put(Constants.RDM_MAPPING_NAME, rdmMap.getString(Constants.RDM_MAPPING_NAME));
						finalRowResult.put(Constants.RDM_MAPPING_TGT_Version_ID,
								rdmMap.getString(Constants.RDM_MAPPING_TGT_Version_ID));
						finalRowResult.put(Constants.RDM_MAPPING_SRC_Version_ID,
								rdmMap.getString(Constants.RDM_MAPPING_SRC_Version_ID));
						finalRowResult.put(Constants.RDM_MAPPING_SRC_NAME,
								rdmMap.getString(Constants.RDM_MAPPING_SRC_NAME));
						finalRowResult.put(Constants.AVAILABLE_RDM_VERSION,
								rdmMap.getString(Constants.RDM_MAPPING_VERSION));
						String status;
						if (Integer.parseInt(mapping.getVersion()) == Integer.parseInt(rdmMap
								.getString(Constants.RDM_MAPPING_VERSION))
								&& mapping.getRdmVersionId().equals(rdmMap.getString(Constants.RDM_MAPPING_VERSION_ID))) {
							status = STATUS_LOADED;
						} else {
							// the version number oder id of the stored mapping
							// is not equal to the number
							// that we got to rdm so a update is available
							status = STATUS_UPDATE;
						}
						finalRowResult.put(Constants.STATUS, status);
						// remove this mapping from the HashMap
						mappingsFromRDM.remove(mapping.getRdmId());
						mappingsInRDMAndDB.put(mapping.getRdmId(), rdmMap.getString(Constants.RDM_MAPPING_NAME));
					} else {
						// we got a Mapping in our database that doesn't exists
						// in RDM anymore
						// put in result with status removed
						finalRowResult.put(Constants.RDM_MAPPING_VERSION_ID, "");
						finalRowResult.put(Constants.RDM_MAPPING_NAME, mapping.getName());
						finalRowResult.put(Constants.RDM_MAPPING_TGT_Version_ID, "");
						finalRowResult.put(Constants.RDM_MAPPING_SRC_Version_ID, "");
						finalRowResult.put(Constants.RDM_MAPPING_SRC_NAME, "");
						finalRowResult.put(Constants.AVAILABLE_RDM_VERSION, "");
						finalRowResult.put(Constants.STATUS, Constants.STATUS_REMOVED);
					}
					result.put(finalRowResult);
				}
				if (mappingsFromRDM != null) {
					// now there should be only new Mappings in mappingsFromRDM
					Set<Entry<String, JSONObject>> mapEntries = mappingsFromRDM.entrySet();
					Iterator<Entry<String, JSONObject>> iter = mapEntries.iterator();
					while (iter.hasNext()) {
						Entry<String, JSONObject> entry = iter.next();
						JSONObject map = entry.getValue();
						finalRowResult = (JSONObject) rowPreResult.clone();
						finalRowResult.put(Constants.RDM_MAPPING_BASE_ID, map.getString(Constants.RDM_MAPPING_BASE_ID));
						finalRowResult.put(Constants.RDM_MAPPING_VERSION_ID,
								map.getString(Constants.RDM_MAPPING_VERSION_ID));
						finalRowResult.put(Constants.RDM_MAPPING_NAME, map.getString(Constants.RDM_MAPPING_NAME));
						finalRowResult.put(Constants.RDM_MAPPING_TGT_Version_ID,
								map.getString(Constants.RDM_MAPPING_TGT_Version_ID));
						finalRowResult.put(Constants.RDM_MAPPING_SRC_Version_ID,
								map.getString(Constants.RDM_MAPPING_SRC_Version_ID));
						finalRowResult.put(Constants.RDM_MAPPING_SRC_NAME,
								map.getString(Constants.RDM_MAPPING_SRC_NAME));
						finalRowResult.put(Constants.AVAILABLE_RDM_VERSION,
								map.getString(Constants.RDM_MAPPING_VERSION));
						finalRowResult.put(Constants.NUMBER_OF_MAPPED_VALUES, "");
						finalRowResult.put(Constants.NUMBER_OF_SRC_VALUES, "");
						finalRowResult.put(LVERSION, "");
						String tgtBaseID = map.getString(Constants.RDM_MAPPING_TGT);
						String srcBaseID = map.getString(Constants.RDM_MAPPING_SRC);
						finalRowResult.put(Constants.RDM_MAPPING_SRC, srcBaseID);
						finalRowResult.put(Constants.RDM_MAPPING_TGT, tgtBaseID);
						// we have to check if there are any conflicts between
						// the new mappings mappingsFromRDM do not contain the
						// mappings
						// that already have been checked against the database
						// but when a conflict was found we want to take the
						// latest
						// mapping information from RDM e.g. the mapping name
						// so we have to pass all mapping for this tt to the
						// method
						JSONArray conflicts = checkForConflicts(mappingsFromRDM, mappingsInRDMAndDB, entry.getKey(),
								tgtBaseID, srcBaseID, table);
						finalRowResult.put(CONFLICTS_ARRAY_NAME, conflicts);
						String status;
						if (conflicts.size() == 0) {
							// if there werent found any conflicts set the
							// status of this mapping to new
							status = STATUS_NEW;
						} else {
							// if there are some conflicts than set the status
							// to conflict
							status = Constants.STATUS_CONFLICT;
						}
						// check if rTable is known, because the user in RDM can
						// create own sets which are unknown to us
						String srcLegacyID = getLegacyID(srcBaseID);
						// we have to check if the srcSet is known
						// we need to display the legacyID which can be found in
						// the rTable or the RdmSet
						if (srcLegacyID != null) {
							// we know the srcSet, that means we published it
							// toRDM
							// if its not known ignore this mapping
							// maybe the ReferenceTable has been removed in the
							// meanwhile
							finalRowResult.put(Constants.LEGACYID, srcLegacyID);
							finalRowResult.put(Constants.STATUS, status);
							result.put(finalRowResult);
						}
						else {
							/* 
							 * Workaround:
							 * The user can use the RefDataClient Tool for adding new Source Sets
							 * manually to an existing Target System.
							 * Entering a Source Set System ID for the mapping in
							 * the Comment field will allow to find and add them at this point.
							 * If the comment field is not empty and the entered ID is 
							 * valid, the Set will appear in RDM-Tab at Data Mappings (CWApp).
							 */
							
							// check if comment exists
							String comment = (String)map.get("Comment");
							if(comment != null && comment.length() > 0) {
								// comment exists. Now checking for valid legacy id
								List<LegacySystem> legacySystemList = com.ibm.is.sappack.cw.app.services.config.DBOperations.getSourceLegacySystem(comment, httpSession);
								if( !(legacySystemList == null || legacySystemList.isEmpty()) ) {
									// valid ID. Add map to result
									finalRowResult.put(Constants.LEGACYID, comment);
									finalRowResult.put(Constants.STATUS, status);
									result.put(finalRowResult);
								}
							}
							
						}
					}
				}
			}
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}

	private JSONArray checkForConflicts(HashMap<String, JSONObject> otherNewMaps, HashMap<String, String> rdmNames,
			String mapID, String srcID, String tgtID, TranscodingTable table) {
		final String METHOD_NAME = "checkForConflicts(HashMap<String,JSONObject> otherNewMaps, String mapID, String srcID, String tgtID, TranscodingTable table)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		// create an array and put there all mapping object that are in conflict
		// to this mapping
		JSONArray result = new JSONArray();
		List<RdmMapping> dbMappings = DBOperations.getAllMappingsForTT(table.getName(), mgr);
		// check for conflicts with Mappings that are already stored in the
		// database
		for (RdmMapping dbMap : dbMappings) {
			if (dbMap.getSourceRdmId().equals(srcID)) {
				if (!dbMap.getRdmId().equals(mapID)) {
					if (dbMap.getTargetRdmId().equals(tgtID)) {
						JSONObject conflict = new JSONObject();
						try {
							// a conflict with the database was found lets first
							// check if this mapping still exist in RDM
							String mappingNameInRDM = rdmNames.get(dbMap.getRdmId());
							if (mappingNameInRDM != null) {
								// this mapping still exist in RDM
								conflict.put(Constants.RDM_MAPPING_NAME, mappingNameInRDM);
							}

							else {
								// mapping is removed in RDM so take the name
								// from db
								// we take only our saved data if the mapping
								// has been deleted in rdm
								// the reason for this, is that the name could
								// have been changed since we saved the entity
								conflict.put(Constants.RDM_MAPPING_NAME, dbMap.getName());
							}
							conflict.put(Constants.RDM_MAPPING_BASE_ID, dbMap.getRdmId());
							conflict.put(Constants.STATUS, STATUS_UNDEFINED);
							result.add(conflict);
						} catch (JSONException e) {
							Util.throwInternalErrorToClient(e);
						}
					}
				}
			}
		}
		// check for conflicts with other new mappings
		Set<Entry<String, JSONObject>> entries = otherNewMaps.entrySet();
		Iterator<Entry<String, JSONObject>> iter = entries.iterator();

		while (iter.hasNext()) {
			Entry<String, JSONObject> entry = iter.next();
			JSONObject otherMap = entry.getValue();
			try {
				String otherSrc = otherMap.getString(Constants.RDM_MAPPING_SRC);
				if (otherSrc.equals(srcID)) {
					String otherId = otherMap.getString(Constants.RDM_MAPPING_BASE_ID);
					if (!otherId.equals(mapID)) {
						String otherTgt = otherMap.getString(Constants.RDM_MAPPING_TGT);
						if (otherTgt.equals(tgtID)) {
							JSONObject conflict = new JSONObject();
							String otherName = otherMap.getString(Constants.RDM_MAPPING_NAME);
							String otherID = otherMap.getString(Constants.RDM_MAPPING_BASE_ID);
							conflict.put(Constants.RDM_MAPPING_NAME, otherName);
							conflict.put(Constants.RDM_MAPPING_BASE_ID, otherID);
							conflict.put(Constants.STATUS, STATUS_NEW);
							result.add(conflict);
						}
					}
				}
			} catch (JSONException e) {
				Util.throwInternalErrorToClient(e);
			}
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}

	private String getLegacyID(String srcID) {
		final String METHOD_NAME = "getLegacyID(String srcID)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		String result = null;
		// the legacyID could be stored in the Referencetable or in the RdmSet.
		// In the 2nd case the Set was unknown to us, so we dont have a
		// Referencetable stored
		ReferenceTable rTable = DBOperations.findReferenceTableForRDMSet(srcID, mgr);
		if (rTable == null) {
			RdmSet srcSet = mgr.find(RdmSet.class, srcID);
			if (srcSet != null) {
				result = srcSet.getLegacyId();
			}
		} else {
			result = rTable.getLegacyId();
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}

	private Map<String, Map<String, JSONObject>> requestJsonToMap(JSONArray requestJsonArray)
			throws JSONException {
		final String METHOD_NAME = "hashClientRequest(JSONArray clientResponse)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		// this function takes the response from the client and builds a treeMap
		// with all TT and the Mappings
		TreeMap<String, Map<String, JSONObject>> result = new TreeMap<String, Map<String, JSONObject>>();
		int i = 0;
		while (i < requestJsonArray.size()) {
			JSONObject row = requestJsonArray.getJSONObject(i);
			String ttName = row.getString(Constants.T_TABLE_NAME);
			Map<String, JSONObject> mappingsMap;
			if (!result.containsKey(ttName)) {
				mappingsMap = new HashMap<String, JSONObject>();
			} else {
				mappingsMap = result.get(ttName);
			}
			mappingsMap.put(row.getString(Constants.RDM_MAPPING_BASE_ID), row);
			result.put(ttName, mappingsMap);
			i++;
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
}
