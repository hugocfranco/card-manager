package br.com.cardmanager.service;

import br.com.cardmanager.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", "mySuperSecretKey123");
    }

    @Test
    void shouldGenerateAndValidateToken() {
        User user = User.builder().login("testuser").build();
        String token = tokenService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String subject = tokenService.validateToken(token);
        assertEquals("testuser", subject);
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        assertNull(tokenService.validateToken("invalid.token.here"));
    }
}