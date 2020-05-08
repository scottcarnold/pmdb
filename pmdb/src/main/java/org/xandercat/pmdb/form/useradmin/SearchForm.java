package org.xandercat.pmdb.form.useradmin;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Validated
public class SearchForm {

	@Length(max=10)  // just setting this as a demonstration
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
