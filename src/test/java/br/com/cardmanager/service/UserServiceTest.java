package br.com.cardmanager.service;

import br.com.cardmanager.model.dto.RegisterDTO;
import br.com.cardmanager.model.entity.User;
import br.com.cardmanager.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterDTO registerDTO = new RegisterDTO("newuser", "password123");
        
        when(userRepository.findByLogin("newuser")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        boolean result = userService.register(registerDTO);

        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldNotRegisterUserWhenLoginAlreadyExists() {
        RegisterDTO registerDTO = new RegisterDTO("existinguser", "password123");
        
        when(userRepository.findByLogin("existinguser")).thenReturn(new User());

        boolean result = userService.register(registerDTO);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }
}