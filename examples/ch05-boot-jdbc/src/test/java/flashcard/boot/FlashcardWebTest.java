package flashcard.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// tag::setup[]
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ch05-web-test;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
class FlashcardWebTest {

    @Autowired
    MockMvc mockMvc;
    // end::setup[]

    @Test
    @DisplayName("홈 화면이 뜬다")
    void home() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Flashcard")));
    }

    // tag::flow[]
    @Test
    @DisplayName("덱을 만들면 덱 상세로 이동한다")
    void createDeck() throws Exception {
        mockMvc.perform(post("/decks").param("name", "영어 단어장"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/decks/*"));
    }
    // end::flow[]
}
