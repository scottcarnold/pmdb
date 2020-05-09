package org.xandercat.pmdb.form.useradmin;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.xandercat.pmdb.dto.PmdbUser;

@Validated
public class UserForm {

	@Length(min=4, max=50)
	private String username;
	
	private String password;
	
	private String passwordConfirm;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private boolean administrator;
	
	private boolean enabled;
	
	public UserForm() {
	}
	public UserForm(PmdbUser user, boolean administrator) {
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.enabled = user.isEnabled();
		this.administrator = administrator;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordConfirm() {
		return passwordConfirm;
	}
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isAdministrator() {
		return administrator;
	}
	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
