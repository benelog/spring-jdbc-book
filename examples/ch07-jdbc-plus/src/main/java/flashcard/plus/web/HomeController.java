package flashcard.plus.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import flashcard.plus.service.DeckService;
import flashcard.plus.service.SmartDeckService;
import flashcard.plus.service.StudyDirection;
import flashcard.plus.service.StudyService;

// tag::class[]
@Controller
public class HomeController {

    private final DeckService deckService;
    private final StudyService studyService;
    private final SmartDeckService smartDeckService;

    public HomeController(DeckService deckService, StudyService studyService,
                          SmartDeckService smartDeckService) {
        this.deckService = deckService;
        this.studyService = studyService;
        this.smartDeckService = smartDeckService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("todayCount", studyService.todayCount());
        model.addAttribute("oftenWrongCount", smartDeckService.oftenWrongCount());
        model.addAttribute("staleCount", smartDeckService.staleCount());
        model.addAttribute("decks", deckService.deckSummaries());
        model.addAttribute("smartDecks", smartDeckService.smartDecks());
        model.addAttribute("directions", StudyDirection.values());
        return "home";
    }
}
// end::class[]
