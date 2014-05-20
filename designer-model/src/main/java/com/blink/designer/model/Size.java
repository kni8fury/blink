package com.blink.designer.model;

import java.io.Serializable;

import javax.persistence.Id;

public class Size implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959916313486230779L;

	private long id;
	
	private int max;
	
	private int min;
	
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id=id;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}
	
	
	

}
