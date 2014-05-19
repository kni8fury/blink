package com.blink.designer.model;


public class Validations {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4805767139343319467L;
    
	private long id;
	
	private boolean assertTrue;
	
	private boolean assertFalse;
	
	private Size size;
	
	private boolean valid;

	public boolean isAssertTrue() {
		return assertTrue;
	}

	public void setAssertTrue(boolean assertTrue) {
		this.assertTrue = assertTrue;
	}

	public boolean isAssertFalse() {
		return assertFalse;
	}

	public void setAssertFalse(boolean assertFalse) {
		this.assertFalse = assertFalse;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	

}
