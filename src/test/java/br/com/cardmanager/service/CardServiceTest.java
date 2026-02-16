package br.com.cardmanager.service;

import br.com.cardmanager.model.dto.CardDTO;
import br.com.cardmanager.model.dto.CardRecoverDTO;
import br.com.cardmanager.model.entity.Card;
import br.com.cardmanager.model.entity.User;
import br.com.cardmanager.model.layout.LayoutFile;
import br.com.cardmanager.model.repository.BulkCardRepository;
import br.com.cardmanager.model.repository.CardRepository;
import br.com.cardmanager.utils.CardUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private BulkCardRepository bulkCardRepository;

    @Mock
    private MultipartFile multipartFile;

    private MockedStatic<CardUtil> cardUtilMockedStatic;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cardService, "salt", "testSalt");

        // Mock do Security Context para simular usuário logado
        User user = User.builder().id("user-id-1").login("testuser").build();
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        lenient().when(auth.getPrincipal()).thenReturn(user);
        lenient().when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Mock dos métodos estáticos do CardUtil
        cardUtilMockedStatic = Mockito.mockStatic(CardUtil.class);
    }

    @AfterEach
    void tearDown() {
        // Fecha o mock estático após cada teste para não afetar os outros
        cardUtilMockedStatic.close();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldGetCardSuccessfully() {
        String uuid = UUID.randomUUID().toString();
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Card card = Card.builder().id(uuid).user(user).build();

        cardUtilMockedStatic.when(() -> CardUtil.generateEncryptedCardNumber("1234", "testSalt"))
                .thenReturn("encrypted-hash");

        when(cardRepository.findByCardHashAndUser("encrypted-hash", user))
                .thenReturn(Optional.of(card));

        CardRecoverDTO result = cardService.getCard("1234");

        assertNotNull(result);
        assertEquals(uuid, result.identifier());
    }

    @Test
    void shouldSaveCardSuccessfully() {
        CardDTO cardDTO = new CardDTO("123456");
        
        cardUtilMockedStatic.when(() -> CardUtil.generateEncryptedCardNumber("123456", "testSalt"))
                .thenReturn("encrypted-hash");

        cardService.saveCard(cardDTO);

        verify(cardRepository, times(1)).saveIgnoreConflict(any(Card.class));
    }

    @Test
    void shouldSaveCardFileSuccessfully() throws IOException {
        String fileContent = "HEADER\nC001123456\nLOTE";
        when(multipartFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));

        LayoutFile.Header headerMock = new LayoutFile.Header("Test", LocalDate.now(), "001", 1);
        LayoutFile.Trailer trailerMock = new LayoutFile.Trailer("001", 1);
        LayoutFile.Information infoMock = new LayoutFile.Information(1, "123456");

        cardUtilMockedStatic.when(() -> CardUtil.parseHeader(anyString())).thenReturn(headerMock);
        cardUtilMockedStatic.when(() -> CardUtil.parseInformation(anyString())).thenReturn(infoMock);
        cardUtilMockedStatic.when(() -> CardUtil.parseTrailer(anyString())).thenReturn(trailerMock);
        cardUtilMockedStatic.when(() -> CardUtil.generateEncryptedCardNumber(anyString(), anyString())).thenReturn("hash");

        cardService.saveCardFile(multipartFile);

        verify(bulkCardRepository, times(1)).saveAllBatch(any());
    }
}