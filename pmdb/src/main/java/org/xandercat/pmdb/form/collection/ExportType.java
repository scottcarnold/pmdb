package org.xandercat.pmdb.form.collection;

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
