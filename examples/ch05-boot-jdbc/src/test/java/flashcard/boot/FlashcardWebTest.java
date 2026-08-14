package flashcard.boot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

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
    void 홈_화면이_뜬다() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Flashcard")));
    }

    // tag::flow[]
    @Test
    void 덱을_만들면_덱_상세로_이동한다() throws Exception {
        mockMvc.perform(post("/decks").param("name", "영어 단어장"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/decks/*"));
    }
    // end::flow[]
}
