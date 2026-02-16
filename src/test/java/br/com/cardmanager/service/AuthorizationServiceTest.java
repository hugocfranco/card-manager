package br.com.cardmanager.service;

import br.com.cardmanager.model.entity.User;
import br.com.cardmanager.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authorizationService;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        User mockUser = User.builder().login("authuser").password("pass").build();
        when(userRepository.findByLogin("authuser")).thenReturn(mockUser);

        UserDetails userDetails = authorizationService.loadUserByUsername("authuser");

        assertNotNull(userDetails);
        assertEquals("authuser", userDetails.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByLogin("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            authorizationService.loadUserByUsername("unknown");
        });
    }
}