package br.com.cardmanager.controller;

import br.com.cardmanager.model.dto.AuthenticationDTO;
import br.com.cardmanager.model.dto.LoginResponseDTO;
import br.com.cardmanager.model.dto.RegisterDTO;
import br.com.cardmanager.model.entity.User;
import br.com.cardmanager.service.TokenService;
import br.com.cardmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @Test
    void shouldLoginSuccessfully() {
        AuthenticationDTO authDTO = new AuthenticationDTO("user", "pass");
        User userMock = User.builder().login("user").build();
        
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(userMock);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        
        when(tokenService.generateToken(userMock)).thenReturn("token123");

        ResponseEntity<LoginResponseDTO> response = authController.login(authDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("token123", response.getBody().token());
    }

    @Test
    void shouldRegisterSuccessfully() {
        RegisterDTO registerDTO = new RegisterDTO("user", "pass");
        when(userService.register(registerDTO)).thenReturn(true);

        ResponseEntity<Void> response = authController.register(registerDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnBadRequestWhenRegisterFails() {
        RegisterDTO registerDTO = new RegisterDTO("user", "pass");
        when(userService.register(registerDTO)).thenReturn(false);

        ResponseEntity<Void> response = authController.register(registerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}