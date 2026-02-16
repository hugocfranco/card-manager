package br.com.cardmanager.service;

import br.com.cardmanager.model.dto.CardDTO;
import br.com.cardmanager.model.dto.CardRecoverDTO;
import br.com.cardmanager.model.entity.Card;
import br.com.cardmanager.model.entity.User;
import br.com.cardmanager.model.layout.LayoutFile;
import br.com.cardmanager.model.repository.BulkCardRepository;
import br.com.cardmanager.model.repository.CardRepository;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.cardmanager.utils.CardUtil.generateEncryptedCardNumber;
import static br.com.cardmanager.utils.CardUtil.parseHeader;
import static br.com.cardmanager.utils.CardUtil.parseInformation;
import static br.com.cardmanager.utils.CardUtil.parseTrailer;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    @Value("${api.security.salt}")
    private String salt;

    private final CardRepository cardRepository;
    private final BulkCardRepository bulkCardRepository;

    public CardRecoverDTO getCard(String cardNumber) {
        var userAuth = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        String encryptedCardNumber = generateEncryptedCardNumber(cardNumber, salt);
        Optional<Card> cardOptional = cardRepository.findByCardHashAndUser(encryptedCardNumber, userAuth);
        return cardOptional.map(card -> new CardRecoverDTO(card.getId())).orElse(null);
    }

    public void saveCard(CardDTO cardNumber) {
        var userAuth = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        String encryptedCardNumber = generateEncryptedCardNumber(cardNumber.cardNumber(), salt);
        Card card = Card.builder().cardHash(encryptedCardNumber).user(userAuth).build();
        cardRepository.saveIgnoreConflict(card);
    }

    public void saveCardFile(@NotEmpty MultipartFile fileCard) {
        LayoutFile.Header header = null;
        List<LayoutFile.Information> informationList = new ArrayList<>();
        LayoutFile.Trailer trailer = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileCard.getInputStream()))) {
            String line = reader.readLine();
            header = parseHeader(line);
            log.info("Header: {} {} {} {}", header.name(), header.batchId(), header.generationDate(), header.recordCount());
            while (Objects.nonNull(line = reader.readLine())) {
                if (line.trim().isEmpty()) continue;
                if (!isInformationLine(line)) {
                    trailer = parseTrailer(line);
                    break;
                }

                informationList.add(parseInformation(line));
            }
            validateFile(header, trailer, informationList.size());
            saveCardBatch(informationList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveCardBatch(List<LayoutFile.Information> informationList) {
        List<Card> cardList = new ArrayList<>();
        var userAuth = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        informationList.forEach(information -> {
            String encryptedCardNumber = generateEncryptedCardNumber(information.cardNumber(), salt);
            cardList.add(Card.builder().cardHash(encryptedCardNumber).user(userAuth).build());
        });
        bulkCardRepository.saveAllBatch(cardList);
    }

    private void validateFile(LayoutFile.Header header, LayoutFile.Trailer trailer, int size) {
        if (Objects.nonNull(header) || Objects.nonNull(trailer) || size > 0) {
            if (header.recordCount() == size || trailer.recordCount() == size) {
                return;
            }
        }
        throw new RuntimeException("The file is invalid or corrupted.");
    }

    private boolean isInformationLine(String line) {
        String identifier = line.substring(0,1);
        return identifier.equalsIgnoreCase("C");
    }
}
