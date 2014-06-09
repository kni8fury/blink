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
	
	private String multiType;
	
	private boolean isRequired;
	
	private Validations validations;
	
	private String updateActionAttr;

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
	public void setMultiType(String multiType){
		this.multiType=multiType;
	}
	public String getMultiType(){
		return multiType;
	}
	public boolean isRequired() {
		return isRequired;
	}
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	public Validations getValidations() {
		return validations;
	}
	public void setValidations(Validations validations) {
		this.validations = validations;
	}
	public String getUpdateActionAttr() {
		return updateActionAttr;
	}
	public void setUpdateActionAttr(String updateActionAttr) {
		this.updateActionAttr = updateActionAttr;
	}
	
}
