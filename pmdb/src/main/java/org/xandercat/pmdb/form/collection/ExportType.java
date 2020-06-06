package org.xandercat.pmdb.form.collection;

/**
 * Enum to represent what format to export movie collections in.
 * 
 * @author Scott Arnold
 */
public enum ExportType {

	XLS("XLS Format"), XLSX("XLSX Format");
	
	private String text;
	
	private ExportType(String text) {
		this.text = text;
	}
	
	public String toString() {
		return text;
	}
}
