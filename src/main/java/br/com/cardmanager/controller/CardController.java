package br.com.cardmanager.controller;

import br.com.cardmanager.model.dto.CardDTO;
import br.com.cardmanager.model.dto.CardRecoverDTO;
import br.com.cardmanager.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<Void> saveCard(@RequestBody @Valid CardDTO cardNumber) {
        cardService.saveCard(cardNumber);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveCardFile(@RequestParam("file") @NotNull MultipartFile file) {
        cardService.saveCardFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<CardRecoverDTO> getCard(@RequestParam("cardNumber") String cardNumber) {
        CardRecoverDTO cardRecoverDTO = cardService.getCard(cardNumber);
        if (Objects.isNull(cardRecoverDTO)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(cardRecoverDTO);
    }
}
