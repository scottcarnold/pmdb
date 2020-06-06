package org.xandercat.pmdb.dto;

import java.time.LocalDate;

/**
 * Class to represent a generic dated application attribute.
 * 
 * @author Scott Arnold
 */
public class ApplicationAttribute {

	private String name;
	private String value;
	private LocalDate date;
	
	public ApplicationAttribute() {
	}
	public ApplicationAttribute(String name, String value, LocalDate date) {
		this.name = name;
		this.value = value;
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
}
