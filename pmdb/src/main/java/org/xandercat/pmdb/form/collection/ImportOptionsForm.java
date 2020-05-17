package org.xandercat.pmdb.form.collection;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

@Validated
public class ImportOptionsForm {

	@NotBlank
	private String collectionName;
	@NotEmpty
	private List<String> sheetNames;
	@NotEmpty
	private List<String> columnNames;
	
	public ImportOptionsForm() {
	}
	public ImportOptionsForm(List<String> sheetNames, List<String> columnNames) {
		this.sheetNames = sheetNames;
		this.columnNames = columnNames;
	}
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
