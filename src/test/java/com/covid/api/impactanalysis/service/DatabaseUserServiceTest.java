package com.covid.api.impactanalysis.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.api.impactanalysis.entity.User;
import com.api.impactanalysis.entity.UserRole;
import com.api.impactanalysis.repository.UserRepository;
import com.api.impactanalysis.service.DatabaseUserService;

class DatabaseUserServiceTest {
	private DatabaseUserService databaseUserService;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void doSetup() {
		MockitoAnnotations.initMocks(this);
		databaseUserService = new DatabaseUserService(userRepository);
	}

	@Test
	@DisplayName("Test that user exists in then it is retreived by service from repository.")
	void testUserGetsRetreived() {
		Optional<User> user = Optional.of(new User(1L, "mockuser", "mockpass", Arrays.asList(new UserRole())));
		when(userRepository.findByUsername("mockuser")).thenReturn(user);
		Optional<User> byUsername = databaseUserService.getByUsername("mockuser");
		assertNotNull(byUsername.get());
		assertEquals("mockuser", byUsername.get().getUsername());
		verify(userRepository,times(1)).findByUsername("mockuser");
	}

	
	@Test
	@DisplayName("Test that user doens't exist then it is not returned by service.")
	void testUnavailableUser() {
		Optional<User> user = Optional.empty();
		when(userRepository.findByUsername("mockuser")).thenReturn(user);
		Optional<User> byUsername = databaseUserService.getByUsername("mockuser");
		assertFalse(byUsername.isPresent(), "User should be null as it doens't exists in repository.");
		verify(userRepository,times(1)).findByUsername("mockuser");
	}

}
