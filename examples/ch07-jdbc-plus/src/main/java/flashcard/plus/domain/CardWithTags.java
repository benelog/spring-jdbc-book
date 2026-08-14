package flashcard.plus.domain;

import java.util.List;

/** 덱 상세 화면용: 카드와 그 카드에 붙은 태그 이름들. */
public record CardWithTags(Card card, List<String> tags) {

    public String tagsAsText() {
        return String.join(", ", tags);
    }
}
