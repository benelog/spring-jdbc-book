package flashcard.plus.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.Deck;
import flashcard.plus.service.CardService;
import flashcard.plus.service.DeckService;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:plus-web-test;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
@Transactional
class FlashcardPlusWebTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    DeckService deckService;
    @Autowired
    CardService cardService;

    @Test
    void 홈_화면에_오늘_복습과_추천_복습이_보인다() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("오늘 복습")))
                .andExpect(content().string(containsString("추천 복습")));
    }

    @Test
    void 덱을_만들면_덱_상세로_이동한다() throws Exception {
        mockMvc.perform(post("/decks").param("name", "영어 단어장"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/decks/*"));
    }

    // tag::study-flow[]
    @Test
    void 카드를_뒤집고_판정하는_학습_흐름이_동작한다() throws Exception {
        Deck deck = deckService.createDeck("영어 단어장");
        cardService.addCard(deck.id(), "resilient", "회복력 있는", "");
        MockHttpSession httpSession = new MockHttpSession();

        // 학습 시작
        mockMvc.perform(post("/study/deck/" + deck.id())
                        .param("direction", "TEXT_TO_MEANING")
                        .session(httpSession))
                .andExpect(redirectedUrl("/study"));

        // 질문 면: 원문이 보인다
        mockMvc.perform(get("/study").session(httpSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("resilient")));

        // 뒤집으면 뜻이 보인다
        mockMvc.perform(get("/study").param("flipped", "true").session(httpSession))
                .andExpect(content().string(containsString("회복력 있는")));

        // 맞았다고 판정하면 다음 카드로 넘어간다 (마지막 카드였으므로 학습 완료)
        mockMvc.perform(post("/study/answer").param("correct", "true").session(httpSession))
                .andExpect(redirectedUrl("/study"));
        mockMvc.perform(get("/study").session(httpSession))
                .andExpect(content().string(containsString("학습을 마쳤습니다")));
    }
    // end::study-flow[]

    @Test
    void CSV_내보내기가_동작한다() throws Exception {
        Deck deck = deckService.createDeck("영어 단어장");
        cardService.addCard(deck.id(), "resilient", "회복력 있는", "형용사");

        mockMvc.perform(get("/decks/" + deck.id() + "/export.csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("resilient,회복력 있는,형용사")));
    }

    @Test
    void 통계_화면이_뜬다() throws Exception {
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("연속 학습일")));
    }
}
