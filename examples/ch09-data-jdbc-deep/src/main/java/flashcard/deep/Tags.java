package flashcard.deep;

import java.util.List;

// tag::class[]
/**
 * 카드에 붙는 태그 묶음.
 * DB에는 "java,spring"처럼 쉼표로 이은 문자열 한 칼럼으로 저장된다.
 * 문자열 변환은 TagsConverters가 맡는다.
 */
public record Tags(List<String> values) {

    public static Tags of(String... values) {
        return new Tags(List.of(values));
    }

    public static Tags none() {
        return new Tags(List.of());
    }

    public boolean contains(String tag) {
        return values.contains(tag);
    }
}
// end::class[]
