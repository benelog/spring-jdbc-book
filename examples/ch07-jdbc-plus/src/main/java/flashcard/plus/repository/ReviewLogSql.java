package flashcard.plus.repository;

import com.navercorp.spring.data.jdbc.plus.sql.support.SqlGeneratorSupport;

public class ReviewLogSql extends SqlGeneratorSupport {

    // tag::daily[]
    public String selectDailyStats() {
        return """
                select study_date,
                       sum(case when correct then 1 else 0 end)     as correct_count,
                       sum(case when not correct then 1 else 0 end) as wrong_count
                from review_log
                where study_date >= :since
                group by study_date
                order by study_date
                """;
    }
    // end::daily[]

    public String selectStudyDates() {
        return """
                select distinct study_date
                from review_log
                order by study_date desc
                """;
    }

    public String countAll() {
        return "select count(*) from review_log";
    }

    public String countCorrect() {
        return "select count(*) from review_log where correct";
    }
}
