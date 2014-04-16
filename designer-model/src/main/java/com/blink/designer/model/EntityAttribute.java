package com.blink.designer.model;



public class EntityAttribute extends BaseBlinkModel{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -5098212742045206213L;
	
	
	private Type type;
	
	private boolean searchable;
	
	private boolean primarykey;

	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	public boolean isPrimarykey() {
		return primarykey;
	}
	public void setPrimarykey(boolean primarykey) {
		this.primarykey = primarykey;
	}
}
