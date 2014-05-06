package com.blink.designer.model;



public class EntityAttribute extends BaseBlinkModel{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -5098212742045206213L;
	
	
	private Type primitiveType;
	
	private boolean searchable;
	
	private boolean primarykey;
	
	private Entity compositeType;

	public Type getPrimitiveType() {
		return primitiveType;
	}
	public void setPrimitiveType(Type primitiveType) {
		this.primitiveType = primitiveType;
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
	public Entity getCompositeType(){
		return compositeType;
	}
	public void setCompositeType(Entity compositeType){
		this.compositeType=compositeType;
	}
}
