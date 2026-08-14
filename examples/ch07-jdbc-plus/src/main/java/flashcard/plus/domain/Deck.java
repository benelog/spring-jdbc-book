package flashcard.plus.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
/**
 * 여러 Spring Data 모듈이 클래스패스에 함께 있으면 strict 모드가 되어,
 * @Table이 붙은 엔티티만 리포지토리 대상으로 인정된다.
 * 이름은 H2가 저장하는 형태(대문자)에 맞췄다.
 */
@Table("DECK")
public record Deck(@Id Long id, String name, LocalDateTime createdAt) {

    public static Deck create(String name, LocalDateTime now) {
        return new Deck(null, name, now);
    }

    public Deck rename(String newName) {
        return new Deck(id, newName, createdAt);
    }
}
// end::class[]
