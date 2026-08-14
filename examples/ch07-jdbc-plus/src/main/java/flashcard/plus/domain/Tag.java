package flashcard.plus.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("TAG")
public record Tag(@Id Long id, String name) {

    public static Tag create(String name) {
        return new Tag(null, name);
    }
}
