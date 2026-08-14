package flashcard.plus.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import flashcard.plus.service.StatsService;

@Controller
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("stats", statsService.overview());
        return "stats";
    }
}
