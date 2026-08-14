package flashcard.aop;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애너테이션 한 줄로 트랜잭션 경계를 선언하는 서비스.
 * 트랜잭션 코드가 본문에서 완전히 사라졌다.
 */
// tag::class[]
@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    public DeckService(DeckRepository deckRepository, CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
    }

    // tag::import-deck[]
    @Transactional
    public long importDeck(String deckName, List<Card> cards) {
        long deckId = deckRepository.insert(deckName);
        for (Card card : cards) {
            cardRepository.insert(Card.of(deckId, card.text(), card.meaning()));
        }
        return deckId;
    }
    // end::import-deck[]
    // end::class[]

    // tag::checked-pitfall[]
    /**
     * 주의: checked 예외는 기본적으로 롤백을 일으키지 않는다.
     * CSV 중간에 형식 오류가 있으면, 예외는 던져지지만
     * 그 전까지 저장된 덱과 카드는 그대로 커밋된다.
     */
    @Transactional
    public long importCsv(String deckName, String csv) throws CsvFormatException {
        long deckId = deckRepository.insert(deckName);
        for (String line : csv.lines().toList()) {
            cardRepository.insert(parseLine(deckId, line));
        }
        return deckId;
    }
    // end::checked-pitfall[]

    // tag::rollback-for[]
    /** rollbackFor를 지정해야 checked 예외에도 롤백된다. */
    @Transactional(rollbackFor = CsvFormatException.class)
    public long importCsvStrictly(String deckName, String csv) throws CsvFormatException {
        long deckId = deckRepository.insert(deckName);
        for (String line : csv.lines().toList()) {
            cardRepository.insert(parseLine(deckId, line));
        }
        return deckId;
    }
    // end::rollback-for[]

    private Card parseLine(long deckId, String line) throws CsvFormatException {
        String[] columns = line.split(",");
        if (columns.length != 2) {
            throw new CsvFormatException("잘못된 CSV 행: " + line);
        }
        return Card.of(deckId, columns[0].trim(), columns[1].trim());
    }

    // tag::read-only[]
    @Transactional(readOnly = true)
    public List<Card> cardsOf(long deckId) {
        return cardRepository.findByDeckId(deckId);
    }
    // end::read-only[]
}
