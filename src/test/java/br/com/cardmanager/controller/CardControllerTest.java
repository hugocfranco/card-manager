package br.com.cardmanager.controller;

import br.com.cardmanager.model.dto.CardDTO;
import br.com.cardmanager.model.dto.CardRecoverDTO;
import br.com.cardmanager.service.CardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @InjectMocks
    private CardController cardController;

    @Mock
    private CardService cardService;

    @Mock
    private MultipartFile multipartFile;

    @Test
    void shouldSaveCardSuccessfully() {
        CardDTO cardDTO = new CardDTO("1234567890123456");

        ResponseEntity<Void> response = cardController.saveCard(cardDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(cardService, times(1)).saveCard(cardDTO);
    }

    @Test
    void shouldSaveCardFileSuccessfully() {
        ResponseEntity<Void> response = cardController.saveCardFile(multipartFile);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(cardService, times(1)).saveCardFile(multipartFile);
    }

    @Test
    void shouldGetCardSuccessfully() {
        String uuid = UUID.randomUUID().toString();
        CardRecoverDTO recoverDTO = new CardRecoverDTO(uuid);
        when(cardService.getCard("1234")).thenReturn(recoverDTO);

        ResponseEntity<CardRecoverDTO> response = cardController.getCard("1234");

        Assertions.assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(uuid, response.getBody().identifier());
    }

    @Test
    void shouldReturnNotFoundWhenCardDoesNotExist() {
        when(cardService.getCard("9999")).thenReturn(null);

        ResponseEntity<CardRecoverDTO> response = cardController.getCard("9999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}