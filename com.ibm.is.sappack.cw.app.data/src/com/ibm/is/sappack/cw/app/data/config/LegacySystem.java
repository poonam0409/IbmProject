package com.ibm.is.sappack.cw.app.data.config;

import com.ibm.is.sappack.cw.app.data.Resources;

public class LegacySystem {
	
	private String legacyId;
	private String lob;
	private String description;
	private boolean isTargetSystem;
	private boolean isSapSystem;
	private boolean isActive;
	
	private String sapHost;
	private String sapClient;
	private String sapGroupName;
	private String sapLanguage;
	private String sapMessageServer;
	private String sapRouterString;
	private String sapSystemId;
	private String sapSystemNumber;
	private boolean sapUseLoadBalancing;
	private String sapUser;
	private String sapPassword;
	
	public LegacySystem() {
		this.isTargetSystem = false;
		this.isSapSystem = false;
		this.isActive = false;
	}

	public String getLegacyId() {
		return legacyId;
	}

	public void setLegacyId(String legacyId) {
		if (legacyId != null && legacyId.length() > Resources.BDR_LENGTH_CW_LEGACY_ID) {
			legacyId = legacyId.substring(0, Resources.BDR_LENGTH_CW_LEGACY_ID);
		} else {
			this.legacyId = legacyId;
		}
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean getIsActive() {
		return isActive;
	}
	
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean getIsTargetSystem() {
		return isTargetSystem;
	}

	public void setIsTargetSystem(boolean isTargetSystem) {
		this.isTargetSystem = isTargetSystem;
	}
	
	public boolean getIsSapSystem() {
		return isSapSystem;
	}
	
	public void setIsSapSystem(boolean isSapSystem) {
		this.isSapSystem = isSapSystem;
	}

	public String getSapHost() {
		return sapHost;
	}

	public void setSapHost(String sapHost) {
		this.sapHost = sapHost;
	}

	public String getSapClient() {
		return sapClient;
	}

	public void setSapClient(String sapClient) {
		this.sapClient = sapClient;
	}

	public String getSapGroupName() {
		return sapGroupName;
	}

	public void setSapGroupName(String sapGroupName) {
		this.sapGroupName = sapGroupName;
	}

	public String getSapLanguage() {
		return sapLanguage;
	}

	public void setSapLanguage(String sapLanguage) {
		this.sapLanguage = sapLanguage;
	}

	public String getSapMessageServer() {
		return sapMessageServer;
	}

	public void setSapMessageServer(String sapMessageServer) {
		this.sapMessageServer = sapMessageServer;
	}

	public String getSapRouterString() {
		return sapRouterString;
	}

	public void setSapRouterString(String sapRouterString) {
		this.sapRouterString = sapRouterString;
	}

	public String getSapSystemId() {
		return sapSystemId;
	}

	public void setSapSystemId(String sapSystemId) {
		this.sapSystemId = sapSystemId;
	}

	public String getSapSystemNumber() {
		return sapSystemNumber;
	}

	public void setSapSystemNumber(String sapSystemNumber) {
		this.sapSystemNumber = sapSystemNumber;
	}

	public boolean getSapUseLoadBalancing() {
		return sapUseLoadBalancing;
	}

	public void setSapUseLoadBalancing(boolean sapUseLoadBalancing) {
		this.sapUseLoadBalancing = sapUseLoadBalancing;
	}

	public String getSapUser() {
		return sapUser;
	}

	public void setSapUser(String sapUser) {
		this.sapUser = sapUser;
	}

	public String getSapPassword() {
		return sapPassword;
	}

	public void setSapPassword(String sapPassword) {
		this.sapPassword = sapPassword;
	}
}
