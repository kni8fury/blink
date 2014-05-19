package com.blink.designer.model;

import javax.persistence.Id;

public class Size{
	
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
