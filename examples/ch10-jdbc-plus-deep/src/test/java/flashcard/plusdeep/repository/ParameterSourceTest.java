package flashcard.plusdeep.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.navercorp.spring.jdbc.plus.support.parametersource.CompositeSqlParameterSource;
import com.navercorp.spring.jdbc.plus.support.parametersource.ConvertibleParameterSourceFactory;
import com.navercorp.spring.jdbc.plus.support.parametersource.converter.DefaultJdbcParameterSourceConverter;
import com.navercorp.spring.jdbc.plus.support.parametersource.fallback.NoneFallbackParameterSource;

import static org.assertj.core.api.Assertions.assertThat;

/** Spring 컨텍스트 없이 파라미터 소스 확장만 검증한다. */
class ParameterSourceTest {

    // tag::padding[]
    @Test
    @DisplayName("IN 절 컬렉션 파라미터를 정해진 크기로 패딩한다")
    void iterablePadding() {
        ConvertibleParameterSourceFactory factory = new ConvertibleParameterSourceFactory();
        factory.setPaddingIterableParam(true);

        MapSqlParameterSource source =
                factory.mapParameterSource(Map.of("ids", List.of(1L, 2L, 3L, 4L, 5L)));

        // 기본 경계값(1, 2, 3, 4, 8, 16, ...)에 맞춰 5개가 8개로 늘어난다.
        // 마지막 값이 반복될 뿐이므로 쿼리 결과는 달라지지 않는다.
        assertThat(source.getValue("ids"))
                .isEqualTo(List.of(1L, 2L, 3L, 4L, 5L, 5L, 5L, 5L));
    }
    // end::padding[]

    // tag::custom-converter[]
    @Test
    @DisplayName("파라미터 타입 변환기를 등록해 쓴다")
    void customConverter() {
        var converter = new DefaultJdbcParameterSourceConverter(List.of(new YearMonthConverter()));
        var factory = new ConvertibleParameterSourceFactory(converter, new NoneFallbackParameterSource());

        MapSqlParameterSource source =
                factory.mapParameterSource(Map.of("month", YearMonth.of(2026, 8)));

        assertThat(source.getValue("month")).isEqualTo("2026-08");
    }

    static class YearMonthConverter implements Converter<YearMonth, String> {
        @Override
        public String convert(YearMonth source) {
            return source.toString();
        }
    }
    // end::custom-converter[]

    // tag::composite[]
    @Test
    @DisplayName("여러 파라미터 소스를 하나로 합친다")
    void composite() {
        CompositeSqlParameterSource composite = new CompositeSqlParameterSource(
                new MapSqlParameterSource("deckId", 1L),
                new MapSqlParameterSource("keyword", "word"));

        assertThat(composite.getValue("deckId")).isEqualTo(1L);
        assertThat(composite.getValue("keyword")).isEqualTo("word");
    }
    // end::composite[]
}
