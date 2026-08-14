package flashcard.plus.web;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.SmartCondition;
import flashcard.plus.service.SmartDeckService;
import flashcard.plus.service.StudyDirection;
import flashcard.plus.service.StudySession;
import flashcard.plus.service.StudyService;

// tag::class[]
@Controller
public class StudyController {

    private static final String SESSION_KEY = "studySession";

    private final StudyService studyService;
    private final SmartDeckService smartDeckService;

    public StudyController(StudyService studyService, SmartDeckService smartDeckService) {
        this.studyService = studyService;
        this.smartDeckService = smartDeckService;
    }

    // tag::start[]
    @PostMapping("/study/deck/{deckId}")
    public String startDeckStudy(@PathVariable Long deckId,
                                 @RequestParam StudyDirection direction, HttpSession session) {
        session.setAttribute(SESSION_KEY, studyService.startDeckSession(deckId, direction));
        return "redirect:/study";
    }

    @PostMapping("/study/today")
    public String startTodayStudy(@RequestParam StudyDirection direction, HttpSession session) {
        session.setAttribute(SESSION_KEY, studyService.startTodaySession(direction));
        return "redirect:/study";
    }

    @PostMapping("/study/smart/{smartDeckId}")
    public String startSmartStudy(@PathVariable Long smartDeckId,
                                  @RequestParam StudyDirection direction, HttpSession session) {
        session.setAttribute(SESSION_KEY, studyService.startSmartSession(
                smartDeckService.getSmartDeck(smartDeckId), direction));
        return "redirect:/study";
    }

    @PostMapping("/study/preset/{condition}")
    public String startPresetStudy(@PathVariable SmartCondition condition,
                                   @RequestParam StudyDirection direction, HttpSession session) {
        session.setAttribute(SESSION_KEY, studyService.startPresetSession(condition, direction));
        return "redirect:/study";
    }
    // end::start[]

    // tag::show[]
    @GetMapping("/study")
    public String study(@RequestParam(defaultValue = "false") boolean flipped,
                        HttpSession session, Model model) {
        StudySession studySession = current(session);
        if (studySession == null || studySession.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("study", studySession);

        if (studySession.isRoundFinished()) {
            return studySession.hasWrongCards() ? "study-round-end" : "study-done";
        }

        Card card = studyService.currentCard(studySession);
        boolean textFirst = studySession.getDirection() == StudyDirection.TEXT_TO_MEANING;
        model.addAttribute("question", textFirst ? card.text() : card.meaning());
        model.addAttribute("answer", textFirst ? card.meaning() : card.text());
        model.addAttribute("flipped", flipped);
        return "study";
    }
    // end::show[]

    // tag::answer[]
    @PostMapping("/study/answer")
    public String answer(@RequestParam boolean correct, HttpSession session) {
        StudySession studySession = current(session);
        if (studySession == null) {
            return "redirect:/";
        }
        studyService.answer(studySession, correct);
        return "redirect:/study";
    }

    @PostMapping("/study/retry")
    public String retryRound(HttpSession session) {
        StudySession studySession = current(session);
        if (studySession != null) {
            studySession.startRetryRound();
        }
        return "redirect:/study";
    }
    // end::answer[]

    @PostMapping("/study/finish")
    public String finish(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        return "redirect:/";
    }

    private StudySession current(HttpSession session) {
        return (StudySession) session.getAttribute(SESSION_KEY);
    }
}
// end::class[]
