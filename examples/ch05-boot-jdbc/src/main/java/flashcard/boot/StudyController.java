package flashcard.boot;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 가장 단순한 학습 화면.
 * 몇 번째 카드를 어느 면으로 보고 있는지는 쿼리 파라미터로만 관리한다.
 */
// tag::class[]
@Controller
public class StudyController {

    private final DeckService deckService;

    public StudyController(DeckService deckService) {
        this.deckService = deckService;
    }

    @GetMapping("/decks/{deckId}/study")
    public String study(@PathVariable long deckId,
                        @RequestParam(defaultValue = "0") int index,
                        @RequestParam(defaultValue = "false") boolean flipped,
                        Model model) {
        List<Card> cards = deckService.cardsOf(deckId);
        model.addAttribute("deck", deckService.getDeck(deckId));

        if (cards.isEmpty() || index >= cards.size()) {
            return "study-done";
        }

        model.addAttribute("card", cards.get(index));
        model.addAttribute("index", index);
        model.addAttribute("total", cards.size());
        model.addAttribute("flipped", flipped);
        return "study";
    }
}
// end::class[]
