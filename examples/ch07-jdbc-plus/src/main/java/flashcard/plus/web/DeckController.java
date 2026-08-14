package flashcard.plus.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import flashcard.plus.service.CsvFormatException;
import flashcard.plus.service.CardService;
import flashcard.plus.service.DeckService;
import flashcard.plus.service.StudyDirection;

@Controller
public class DeckController {

    private final DeckService deckService;
    private final CardService cardService;

    public DeckController(DeckService deckService, CardService cardService) {
        this.deckService = deckService;
        this.cardService = cardService;
    }

    @PostMapping("/decks")
    public String createDeck(@RequestParam String name) {
        return "redirect:/decks/" + deckService.createDeck(name).id();
    }

    @GetMapping("/decks/{deckId}")
    public String deckDetail(@PathVariable Long deckId, Model model) {
        model.addAttribute("deck", deckService.getDeck(deckId));
        model.addAttribute("cards", cardService.cardsWithTags(deckId));
        model.addAttribute("directions", StudyDirection.values());
        return "deck";
    }

    @PostMapping("/decks/{deckId}/rename")
    public String renameDeck(@PathVariable Long deckId, @RequestParam String name) {
        deckService.renameDeck(deckId, name);
        return "redirect:/decks/" + deckId;
    }

    @PostMapping("/decks/{deckId}/delete")
    public String deleteDeck(@PathVariable Long deckId) {
        deckService.deleteDeck(deckId);
        return "redirect:/";
    }

    @PostMapping("/decks/{deckId}/cards")
    public String addCard(@PathVariable Long deckId, @RequestParam String text,
                          @RequestParam String meaning,
                          @RequestParam(defaultValue = "") String tags) {
        cardService.addCard(deckId, text, meaning, tags);
        return "redirect:/decks/" + deckId;
    }

    @GetMapping("/decks/{deckId}/cards/{cardId}/edit")
    public String editCardForm(@PathVariable Long deckId, @PathVariable Long cardId,
                               Model model) {
        model.addAttribute("deck", deckService.getDeck(deckId));
        model.addAttribute("card", cardService.getCard(cardId));
        model.addAttribute("tags", String.join(", ", cardService.tagsOf(cardId)));
        return "card-edit";
    }

    @PostMapping("/decks/{deckId}/cards/{cardId}/edit")
    public String editCard(@PathVariable Long deckId, @PathVariable Long cardId,
                           @RequestParam String text, @RequestParam String meaning,
                           @RequestParam(defaultValue = "") String tags) {
        cardService.editCard(cardId, text, meaning, tags);
        return "redirect:/decks/" + deckId;
    }

    @PostMapping("/decks/{deckId}/cards/{cardId}/delete")
    public String deleteCard(@PathVariable Long deckId, @PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return "redirect:/decks/" + deckId;
    }

    // tag::csv[]
    @GetMapping("/decks/{deckId}/export.csv")
    public ResponseEntity<byte[]> exportCsv(@PathVariable Long deckId) {
        String csv = deckService.exportCsv(deckId);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"deck-%d.csv\"".formatted(deckId))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/decks/{deckId}/import")
    public String importCsv(@PathVariable Long deckId, @RequestParam MultipartFile file,
                            RedirectAttributes redirect) throws IOException {
        int imported = deckService.importCsv(deckId,
                new String(file.getBytes(), StandardCharsets.UTF_8));
        redirect.addFlashAttribute("message", imported + "장의 카드를 들여왔습니다.");
        return "redirect:/decks/" + deckId;
    }

    /** CSV 형식 오류: 전체가 롤백된 뒤 이 핸들러로 온다. */
    @ExceptionHandler(CsvFormatException.class)
    public String handleCsvError(CsvFormatException e, RedirectAttributes redirect) {
        redirect.addFlashAttribute("error", e.getMessage());
        return "redirect:/";
    }
    // end::csv[]
}
