package org.xandercat.pmdb.form.useradmin;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.xandercat.pmdb.config.PmdbGrantedAuthority;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.util.Pair;
import org.xandercat.pmdb.validation.Password;
import org.xandercat.pmdb.validation.Username;
import org.xandercat.pmdb.validation.ValuesMatch;

/**
 * Form for user details.
 * 
 * @author Scott Arnold
 */
@Validated
public class UserForm {

	@Username
	@Length(min=4, max=50)
	private String username;
	
	@Password
	@ValuesMatch
	private Pair<String> passwordPair = new Pair<String>();
	
	@Length(max=50)
	private String firstName;
	
	@Length(max=50)
	private String lastName;
	
	@Email
	@Length(max=200)
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
	public PmdbUser toUser() {
		PmdbUser user = new PmdbUser(username);
		user.setEmail(email);
		user.setEnabled(enabled);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_USER);
		if (administrator) {
			user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_ADMIN);
		}
		return user;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public Pair<String> getPasswordPair() {
		return passwordPair;
	}
	public void setPasswordPair(Pair<String> passwordPair) {
		this.passwordPair = passwordPair;
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
