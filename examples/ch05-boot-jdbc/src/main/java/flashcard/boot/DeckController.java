package flashcard.boot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    // tag::create[]
    @PostMapping("/decks")
    public String createDeck(@RequestParam String name) {
        long deckId = deckService.createDeck(name);
        return "redirect:/decks/" + deckId;
    }
    // end::create[]

    // tag::detail[]
    @GetMapping("/decks/{deckId}")
    public String deckDetail(@PathVariable long deckId, Model model) {
        model.addAttribute("deck", deckService.getDeck(deckId));
        model.addAttribute("cards", deckService.cardsOf(deckId));
        return "deck";
    }
    // end::detail[]

    @PostMapping("/decks/{deckId}/cards")
    public String addCard(@PathVariable long deckId,
                          @RequestParam String text, @RequestParam String meaning) {
        deckService.addCard(deckId, text, meaning);
        return "redirect:/decks/" + deckId;
    }

    @PostMapping("/decks/{deckId}/cards/{cardId}/delete")
    public String deleteCard(@PathVariable long deckId, @PathVariable long cardId) {
        deckService.deleteCard(cardId);
        return "redirect:/decks/" + deckId;
    }

    @PostMapping("/decks/{deckId}/delete")
    public String deleteDeck(@PathVariable long deckId) {
        deckService.deleteDeck(deckId);
        return "redirect:/";
    }
}
