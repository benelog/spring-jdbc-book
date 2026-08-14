package flashcard.plus.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flashcard.plus.domain.SmartCondition;
import flashcard.plus.service.SmartDeckService;

@Controller
public class SmartDeckController {

    private final SmartDeckService smartDeckService;

    public SmartDeckController(SmartDeckService smartDeckService) {
        this.smartDeckService = smartDeckService;
    }

    @PostMapping("/smart-decks")
    public String create(@RequestParam String name, @RequestParam SmartCondition condition,
                         @RequestParam(defaultValue = "") String param) {
        smartDeckService.create(name, condition, param);
        return "redirect:/";
    }

    @PostMapping("/smart-decks/{id}/delete")
    public String delete(@PathVariable Long id) {
        smartDeckService.delete(id);
        return "redirect:/";
    }
}
