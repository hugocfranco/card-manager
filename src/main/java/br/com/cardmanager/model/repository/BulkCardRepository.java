package br.com.cardmanager.model.repository;

import br.com.cardmanager.model.entity.Card;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BulkCardRepository {

    private final EntityManager entityManager;

    @Transactional
    public void saveAllBatch(List<Card> cards) {
        entityManager.unwrap(Session.class).doWork(connection -> {

            try (PreparedStatement ps = connection.prepareStatement(
                """
                    INSERT IGNORE INTO cards (id, user_id, card_hash) VALUES (?, ?, ?)
                    """)) {

                for (Card card : cards) {

                    if (card.getId() == null) {
                        card.setId(UUID.randomUUID().toString());
                    }

                    ps.setString(1, card.getId());
                    ps.setString(2, card.getUser().getId());
                    ps.setString(3, card.getCardHash());

                    ps.addBatch();
                }

                ps.executeBatch();
            }
        });
    }
}