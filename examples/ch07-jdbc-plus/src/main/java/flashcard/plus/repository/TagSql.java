package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.sql.support.SqlGeneratorSupport;

import flashcard.plus.domain.Tag;

public class TagSql extends SqlGeneratorSupport {

    public String selectByName() {
        return """
                select %s
                from tag
                where name = :name
                """.formatted(sql.columns(Tag.class));
    }

    public String selectTagNamesByDeckId() {
        return """
                select card_tag.card_id, tag.name as tag_name
                from card_tag
                    join card on card.id = card_tag.card_id
                    join tag on tag.id = card_tag.tag_id
                where card.deck_id = :deckId
                order by tag.name
                """;
    }

    public String selectTagNamesByCardId() {
        return """
                select tag.name
                from card_tag
                    join tag on tag.id = card_tag.tag_id
                where card_tag.card_id = :cardId
                order by tag.name
                """;
    }

    public String selectAllNames() {
        return """
                select name
                from tag
                order by name
                """;
    }

    // tag::attach[]
    /** 이미 붙어 있으면 그대로 두는 upsert. H2의 MERGE 문법을 쓴다. */
    public String attach() {
        return """
                merge into card_tag (card_id, tag_id)
                key (card_id, tag_id)
                values (:cardId, :tagId)
                """;
    }
    // end::attach[]

    public String detachAll() {
        return """
                delete from card_tag
                where card_id = :cardId
                """;
    }
}
