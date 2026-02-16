package br.com.cardmanager.model.repository;

import br.com.cardmanager.model.entity.Card;
import br.com.cardmanager.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {
    Optional<Card> findByCardHashAndUser(String cardHash, User user);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT IGNORE INTO card_db.cards (user_id, card_hash) VALUES (:#{#card.user.id}, :#{#card.cardHash})
        """, nativeQuery = true)
    void saveIgnoreConflict(@Param("card") Card card);
}