package com.blink.designer.model;

public class SecurityStoreConfig extends BaseBlinkModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9038361829321486427L;
	
	private LDAPConfig lDAPConfig;
	private FacebookConfig facebookConfig;
	
	public LDAPConfig getlDAPConfig() {
		return lDAPConfig;
	}

	public void setlDAPConfig(LDAPConfig lDAPConfig) {
		this.lDAPConfig = lDAPConfig;
	}
	
	public FacebookConfig getfacebookConfig() {
		return facebookConfig;
	}

	public void setfacebookConfig(FacebookConfig facebookConfig) {
		this.facebookConfig = facebookConfig;
	}

}
