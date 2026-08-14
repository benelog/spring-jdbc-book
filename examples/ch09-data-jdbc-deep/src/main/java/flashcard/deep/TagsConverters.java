package flashcard.deep;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

// tag::class[]
/** Tags ↔ varchar 변환기 쌍. DataJdbcDeepConfig에서 등록한다. */
public final class TagsConverters {

    @WritingConverter
    public enum TagsToString implements Converter<Tags, String> {
        INSTANCE;

        @Override
        public String convert(Tags source) {
            return String.join(",", source.values());
        }
    }

    @ReadingConverter
    public enum StringToTags implements Converter<String, Tags> {
        INSTANCE;

        @Override
        public Tags convert(String source) {
            return source.isBlank() ? Tags.none() : new Tags(List.of(source.split(",")));
        }
    }

    private TagsConverters() {
    }
}
// end::class[]
