package com.blink;

public enum PackageType {

	DTO("DTO"),
	BIZ("BIZ"),
	DO("JPA");

	private String text ;
	private PackageType(String text) {
		this.text = text; 
	}

	public String toString() {
		return text; 
	}
}
