package flashcard.plus.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flashcard.plus.domain.Card;
import flashcard.plus.domain.CardTagName;
import flashcard.plus.domain.CardWithTags;
import flashcard.plus.domain.Tag;
import flashcard.plus.repository.CardRepository;
import flashcard.plus.repository.TagRepository;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TagRepository tagRepository;

    public CardService(CardRepository cardRepository, TagRepository tagRepository) {
        this.cardRepository = cardRepository;
        this.tagRepository = tagRepository;
    }

    // tag::add[]
    @Transactional
    public Card addCard(Long deckId, String text, String meaning, String tagsText) {
        Card card = cardRepository.insert(
                Card.create(deckId, text, meaning, LocalDateTime.now()));
        attachTags(card.id(), tagsText);
        return card;
    }
    // end::add[]

    @Transactional
    public void editCard(Long cardId, String text, String meaning, String tagsText) {
        Card card = getCard(cardId);
        cardRepository.update(card.edit(text, meaning));
        tagRepository.detachAll(cardId);
        attachTags(cardId, tagsText);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    @Transactional(readOnly = true)
    public Card getCard(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("카드가 없습니다: " + cardId));
    }

    // tag::with-tags[]
    /** 카드 목록과 태그를 각각 한 번씩 조회해 묶는다. N+1 조회가 없다. */
    @Transactional(readOnly = true)
    public List<CardWithTags> cardsWithTags(Long deckId) {
        List<Card> cards = cardRepository.findByDeckId(deckId);
        Map<Long, List<String>> tagsByCard = tagRepository.findTagNamesByDeckId(deckId).stream()
                .collect(Collectors.groupingBy(CardTagName::cardId,
                        Collectors.mapping(CardTagName::tagName, Collectors.toList())));

        return cards.stream()
                .map(card -> new CardWithTags(card, tagsByCard.getOrDefault(card.id(), List.of())))
                .toList();
    }
    // end::with-tags[]

    @Transactional(readOnly = true)
    public List<String> tagsOf(Long cardId) {
        return tagRepository.findTagNamesByCardId(cardId);
    }

    void attachTags(Long cardId, String tagsText) {
        parseTags(tagsText).forEach(tagName -> {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.insert(Tag.create(tagName)));
            tagRepository.attach(cardId, tag.id());
        });
    }

    static List<String> parseTags(String tagsText) {
        if (tagsText == null || tagsText.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tagsText.split("[,;]"))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toList();
    }
}
