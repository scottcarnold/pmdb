package org.xandercat.pmdb.form.collection;

import org.springframework.web.multipart.MultipartFile;

public class ImportForm {

	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}
