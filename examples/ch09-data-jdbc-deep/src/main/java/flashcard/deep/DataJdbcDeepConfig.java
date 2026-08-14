package flashcard.deep;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;

// tag::class[]
/**
 * Spring Data JDBC 커스터마이징이 모이는 설정.
 * AbstractJdbcConfiguration을 상속하면 Boot의 기본 설정이 물러나고 이 설정이 그 자리를 대신한다.
 */
@Configuration
@EnableJdbcAuditing
public class DataJdbcDeepConfig extends AbstractJdbcConfiguration {

    /** 커스텀 변환기 등록 지점. */
    @Override
    protected List<?> userConverters() {
        return List.of(TagsConverters.TagsToString.INSTANCE, TagsConverters.StringToTags.INSTANCE);
    }

    /** 저장 직전에 카드 텍스트의 앞뒤 공백을 정리하는 엔티티 콜백. */
    @Bean
    BeforeConvertCallback<Card> cardTrimCallback() {
        return Card::trimmed;
    }
}
// end::class[]
