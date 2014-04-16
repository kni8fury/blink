package com.blink.designer.model;

public class PersistenceAPIConfig extends BaseBlinkModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8056398693850351277L;
	private String inheritance;
	private String idgeneration;
	private String association;
	
	public String getInheritance(){
		return inheritance;
	}
	public void setInheritance(String inheritance) {
		this.inheritance = inheritance;
	}
	public String getIdGeneration(){
		return idgeneration;
	}
	public void setIdGeneration(String idgeneration) {
		this.idgeneration = idgeneration;
	}
	public String getAssociation(){
		return association;
	}
	public void setAssociation(String association) {
		this.association = association;
	}
	
	
	
	

}
