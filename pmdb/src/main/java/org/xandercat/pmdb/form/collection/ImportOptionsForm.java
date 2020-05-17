package org.xandercat.pmdb.form.collection;

import java.util.List;

public class ImportOptionsForm {

	private String collectionName;
	private List<String> sheetNames;
	private List<String> columnNames;
	
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public List<String> getSheetNames() {
		return sheetNames;
	}
	public void setSheetNames(List<String> sheetNames) {
		this.sheetNames = sheetNames;
	}
	public List<String> getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}
}
