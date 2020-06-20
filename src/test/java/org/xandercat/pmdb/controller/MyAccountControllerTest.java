package org.xandercat.pmdb.controller;

import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

public class MyAccountControllerTest {

	private MockMvc mockMvc;
	
	@Mock
	private Principal principal;
	
	@Mock
	private UserService userService;
	
	@InjectMocks
	private MyAccountController controller;

	@BeforeEach
	public void setup() {
		PmdbUser user = new PmdbUser();
		user.setUsername("User");
		user.setEmail("Email");
		user.setFirstName("First");
		user.setLastName("Last");
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		when(principal.getName()).thenReturn("User");
		when(userService.getUser("User")).thenReturn(Optional.of(user));
		when(userService.isAdministrator("User")).thenReturn(Boolean.TRUE);
	}
	
	@Test
	public void editUserTest() throws Exception {
		mockMvc.perform(get("/myaccount")
				.principal(principal)
		)
				.andExpect(model().attributeExists("userForm"))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("username", Matchers.equalTo("User"))))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("email", Matchers.equalTo("Email"))))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("firstName", Matchers.equalTo("First"))))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("lastName", Matchers.equalTo("Last"))))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("administrator", Matchers.equalTo(Boolean.TRUE))));
	}
	
	@Test
	public void submitEditUserTest() throws Exception {	
		mockMvc.perform(get("/myaccount/editUserSubmit")
				.principal(principal)
				.param("username", "User")
				.param("firstName", "First")
				.param("lastName", "Last")
		)
				.andExpect(model().attributeExists("userForm"))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("username", Matchers.equalTo("User"))))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("email", Matchers.equalTo("Email"))))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("firstName", Matchers.equalTo("First"))))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("lastName", Matchers.equalTo("Last"))))
				.andExpect(model().attribute("userForm", Matchers.hasProperty("administrator", Matchers.equalTo(Boolean.TRUE))));
	}
}
