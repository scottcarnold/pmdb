package org.xandercat.pmdb.form.collection;

import java.util.ArrayList;
import java.util.List;

public class ExportForm {

	private List<String> collections;
	private String type;

	public ExportForm() {
	}
	
	public ExportForm(int defaultCollectionId, ExportType defaultExportType) {
		this.collections = new ArrayList<String>();
		this.collections.add(String.valueOf(defaultCollectionId));
		this.type = defaultExportType.name();
	}
	
	public List<String> getCollections() {
		return collections;
	}

	public void setCollections(List<String> collections) {
		this.collections = collections;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
