package flashcard.plus.repository;

import java.time.LocalDate;
import java.util.List;

import flashcard.plus.domain.DeckStat;
import flashcard.plus.domain.DeckSummary;

public interface DeckRepositoryCustom {

    List<DeckSummary> findAllSummaries(LocalDate today);

    List<DeckStat> findAllStats();
}
