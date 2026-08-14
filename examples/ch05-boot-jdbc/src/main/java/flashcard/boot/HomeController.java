package flashcard.boot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// tag::class[]
@Controller
public class HomeController {

    private final DeckService deckService;

    public HomeController(DeckService deckService) {
        this.deckService = deckService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("decks", deckService.deckSummaries());
        return "home";
    }
}
// end::class[]
