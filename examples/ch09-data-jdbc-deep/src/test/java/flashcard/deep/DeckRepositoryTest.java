package flashcard.deep;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

// tag::setup[]
@DataJdbcTest
@Import(DataJdbcDeepConfig.class)   // 슬라이스 테스트는 일반 @Configuration을 줍지 않으므로 직접 가져온다
class DeckRepositoryTest {

    @Autowired
    DeckRepository deckRepository;
    // end::setup[]

    // tag::embedded[]
    @Test
    @DisplayName("값 객체가 POLICY_ 접두사 컬럼들로 저장되고 다시 조립된다")
    void embeddedRoundTrip() {
        Deck saved = deckRepository.save(new Deck("영어 단어장", new ReviewPolicy(10, 50)));

        Deck found = deckRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getPolicy()).isEqualTo(new ReviewPolicy(10, 50));
    }
    // end::embedded[]

    // tag::auditing[]
    @Test
    @DisplayName("생성·수정 시각을 프레임워크가 채운다")
    void auditing() {
        Deck saved = deckRepository.save(new Deck("영어 단어장", ReviewPolicy.standard()));
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        saved.rename("TOEIC 단어장");
        Deck updated = deckRepository.save(saved);

        assertThat(updated.getCreatedAt()).isEqualTo(saved.getCreatedAt());   // 생성 시각은 그대로
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updated.getCreatedAt());
    }
    // end::auditing[]
}
