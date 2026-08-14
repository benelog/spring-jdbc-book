package flashcard.plus.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.Deck;
import flashcard.plus.domain.DeckSummary;
import flashcard.plus.repository.CardRepository;
import flashcard.plus.repository.DeckRepository;
import flashcard.plus.service.CsvCodec.CsvCard;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final CardService cardService;

    public DeckService(DeckRepository deckRepository, CardRepository cardRepository,
                       CardService cardService) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.cardService = cardService;
    }

    @Transactional
    public Deck createDeck(String name) {
        return deckRepository.insert(Deck.create(name, LocalDateTime.now()));
    }

    @Transactional
    public void renameDeck(Long deckId, String newName) {
        deckRepository.update(getDeck(deckId).rename(newName));
    }

    @Transactional
    public void deleteDeck(Long deckId) {
        deckRepository.deleteById(deckId);
    }

    @Transactional(readOnly = true)
    public Deck getDeck(Long deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("덱이 없습니다: " + deckId));
    }

    @Transactional(readOnly = true)
    public List<DeckSummary> deckSummaries() {
        return deckRepository.findAllSummaries(LocalDate.now());
    }

    // tag::import[]
    /**
     * CSV 전체를 한 트랜잭션으로 들여온다.
     * 중간에 형식이 잘못된 줄이 있으면 전부 되돌린다.
     */
    @Transactional
    public int importCsv(Long deckId, String csvContent) {
        List<CsvCard> parsed = CsvCodec.parse(csvContent);

        LocalDateTime now = LocalDateTime.now();
        for (CsvCard csvCard : parsed) {
            Card card = cardRepository.insert(
                    Card.create(deckId, csvCard.text(), csvCard.meaning(), now));
            cardService.attachTags(card.id(), String.join(",", csvCard.tags()));
        }
        return parsed.size();
    }
    // end::import[]

    // tag::export[]
    @Transactional(readOnly = true)
    public String exportCsv(Long deckId) {
        return CsvCodec.format(cardService.cardsWithTags(deckId));
    }
    // end::export[]
}
