package org.xandercat.pmdb.form.collection;

import org.springframework.web.multipart.MultipartFile;

/**
 * Form for selecting file for import.
 * 
 * @author Scott Arnold
 */
public class ImportForm {

	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}
