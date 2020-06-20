package org.xandercat.pmdb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.xandercat.pmdb.config.PmdbGrantedAuthority;
import org.xandercat.pmdb.dao.AuthDao;
import org.xandercat.pmdb.dao.UserDao;
import org.xandercat.pmdb.dao.repository.DynamoUserCredentialsRepository;
import org.xandercat.pmdb.dto.CloudUserSearchResults;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.dto.PmdbUserCredentials;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.util.ApplicationProperties;

public class UserServiceImplTest {

	private static final String VALID_PASSWORD = "ThDFkerhtoi474$#dk5h";
	
	@Mock
	UserDao userDao;
	
	@Mock
	AuthDao authDao;
	
	@Mock
	private DynamoUserCredentialsRepository dynamoUserCredentialsRepository;
	
	@Mock
	private ApplicationService applicationService;
	
	@InjectMocks
	private UserServiceImpl service;
	
	@BeforeEach
	public void beforeEach() {
		ApplicationProperties props = new ApplicationProperties();
		props.setAwsEnabled(true);
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(service, "regMax", 3);
		ReflectionTestUtils.setField(service, "applicationProperties", props);
	}

	@Test
	public void testBlankUsername() {
		PmdbUser user = new PmdbUser();
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.saveUser(user, null, false);
		});
		assertNotNull(exception);
	}
	
	@Test
	public void testNewUserNoPassword() {
		PmdbUser user = new PmdbUser("User");
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.saveUser(user, null, true);
		});
		assertNotNull(exception);		
	}
	
	@Test
	public void testNewUserDuplicateUsername() {
		PmdbUser user = new PmdbUser("User");
		when(userDao.getUser("User")).thenReturn(Optional.of(user));
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.saveUser(user, VALID_PASSWORD, true);
		});
		assertNotNull(exception);			
	}
	
	@Test
	public void testNewUserDuplicateCloudUsername() {
		PmdbUserCredentials creds = new PmdbUserCredentials("User", VALID_PASSWORD.getBytes());
		PmdbUser user = new PmdbUser("User");
		when(userDao.getUser("User")).thenReturn(Optional.empty());
		when(dynamoUserCredentialsRepository.findById("User")).thenReturn(Optional.of(creds));
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.saveUser(user, VALID_PASSWORD, true);
		});
		assertNotNull(exception);		
	}
	
	@Test
	public void testExistingUserNotFound() {
		PmdbUser user = new PmdbUser("User");
		when(userDao.getUser("User")).thenReturn(Optional.empty());
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.saveUser(user, VALID_PASSWORD, false);
		});
		assertNotNull(exception);		
	}
	
	@Test
	public void testNewUserSave() {
		PmdbUser user = new PmdbUser("User");
		user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_USER);
		user.setPassword("Dummy"); // should actually be the encrypted form of the password, but a dummy string will work for testing
		service.saveUser(user, VALID_PASSWORD, true);
		verify(userDao, times(1)).addUser(user, VALID_PASSWORD);
		verify(authDao, times(1)).grant(user.getUsername(), user.getGrantedAuthorities());
	}
	
	@Test
	public void testExistingUserSave() {
		PmdbUser user = new PmdbUser("User");
		user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_USER);
		when(userDao.getUser("User")).thenReturn(Optional.of(user));
		when(authDao.getAuthorities("User")).thenReturn(Collections.singletonList(PmdbGrantedAuthority.ROLE_USER));
		service.saveUser(user, null, false);
		verify(userDao, times(1)).saveUser(user);
	}
	
	@Test
	public void testExistingUserSaveGrantAdmin() {
		PmdbUser user = new PmdbUser("User");
		user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_USER);
		user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_ADMIN);
		when(userDao.getUser("User")).thenReturn(Optional.of(user));
		when(authDao.getAuthorities("User")).thenReturn(Collections.singletonList(PmdbGrantedAuthority.ROLE_USER));
		service.saveUser(user, null, false);
		verify(userDao, times(1)).saveUser(user);
		verify(authDao, times(1)).grant("User", PmdbGrantedAuthority.ROLE_ADMIN);
	}
	
	@Test
	public void testSyncCloudUsers() {
		List<PmdbUser> users = new ArrayList<PmdbUser>();
		users.add(new PmdbUser("User"));
		users.add(new PmdbUser("Jeff"));
		List<PmdbUserCredentials> creds = new ArrayList<PmdbUserCredentials>();
		creds.add(new PmdbUserCredentials("User", "Dummy".getBytes()));
		creds.add(new PmdbUserCredentials("Frank", "Dummy".getBytes()));
		when(dynamoUserCredentialsRepository.findAll()).thenReturn(creds);
		CloudUserSearchResults results = service.syncCloudUsers(users, "");
		assertEquals(1, results.getUsernamesNotInCloud().size());
		assertEquals(1, results.getUsernamesOnlyInCloud().size());
		assertEquals("Jeff", results.getUsernamesNotInCloud().iterator().next());
		assertEquals("Frank", results.getUsernamesOnlyInCloud().iterator().next());
	}
	
	@Test
	public void testSyncUserFromCloud() throws Exception {
		PmdbUser user = new PmdbUser("User");
		user.setPassword("Dummy");
		PmdbUserCredentials cred = new PmdbUserCredentials("User", "Dummy".getBytes());
		when(userDao.getUser("User")).thenReturn(Optional.empty());
		when(dynamoUserCredentialsRepository.findById("User")).thenReturn(Optional.of(cred));
		service.syncUserFromCloud("User");
		verify(userDao, times(1)).readdUser(any());
		verify(authDao, times(1)).grant("User", PmdbGrantedAuthority.ROLE_USER);
	}
	
	@Test
	public void testSyncUserToCloud() throws Exception {
		PmdbUser user = new PmdbUser("User");
		user.setPassword("Dummy");
		when(userDao.getUser("User")).thenReturn(Optional.of(user));
		service.syncUserToCloud("User");
		verify(dynamoUserCredentialsRepository, times(1)).save(any());
	}
	
	@Test
	public void testSaveMyAccountUser() {
		PmdbUser user = new PmdbUser("User");
		user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_USER);
		when(userDao.getUser("User")).thenReturn(Optional.of(user));
		when(authDao.getAuthorities("User")).thenReturn(Collections.singletonList(PmdbGrantedAuthority.ROLE_USER));
		service.saveMyAccountUser(user, null, "User");
		verify(userDao, times(1)).saveUser(user);
	}
	
	@Test
	public void testSaveMyAccountUserWrongUser() {
		PmdbUser user = new PmdbUser("User");
		user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_USER);
		when(userDao.getUser("User")).thenReturn(Optional.of(user));
		when(authDao.getAuthorities("User")).thenReturn(Collections.singletonList(PmdbGrantedAuthority.ROLE_USER));
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.saveMyAccountUser(user, null, "DifferentUser");	
		});
		assertNotNull(exception);
	}
	
	@Test
	public void testRegisterUser() throws Exception {
		PmdbUser user = new PmdbUser("User");
		user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_USER);
		user.setPassword("Dummy"); // should actually be the encrypted form of the password, but a dummy string will work for testing
		service.registerUser(user, VALID_PASSWORD);
		verify(userDao, times(1)).addUser(user, VALID_PASSWORD);
		verify(authDao, times(1)).grant(user.getUsername(), user.getGrantedAuthorities());
	}
	
	@Test
	public void testRegisterUserServiceLimitExceeded() throws Exception {
		PmdbUser user = new PmdbUser("User");
		user.addGrantedAuthority(PmdbGrantedAuthority.ROLE_USER);
		user.setPassword("Dummy"); // should actually be the encrypted form of the password, but a dummy string will work for testing
		when(applicationService.incrementRegistrationsTriggerCount()).thenReturn(20);
		Exception exception = assertThrows(ServiceLimitExceededException.class, () -> {
			service.registerUser(user, VALID_PASSWORD);
		});
		assertNotNull(exception);
	}
}
