package com.blink.designer.model;

public class LDAPConfig extends BaseBlinkModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7912547451296520236L;
	private String userProvider;
	private String userFilter;
	private String authzIdentity;
	
	
	public String getUserProvider() {
		return userProvider;
	}
	public void setUserProvider(String userProvider) {
		this.userProvider = userProvider;
	}
	public String getUserFilter() {
		return userFilter;
	}
	public void setUserFilter(String userFilter) {
		this.userFilter = userFilter;
	}
	public String getAuthzIdentity() {
		return authzIdentity;
	}
	public void setAuthzIdentity(String authzIdentity) {
		this.authzIdentity = authzIdentity;
	}

}
