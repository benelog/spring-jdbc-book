package flashcard.kotlin

import org.springframework.jdbc.core.simple.JdbcClient

// tag::extension[]
/**
 * query(T::class.java) 호출을 query<T>()로 줄여 주는 reified 확장 함수.
 * Spring의 널 표기는 결과 값이 null일 수 있다는 보수적 선언(MappedQuerySpec<T?>)이라,
 * non-null 타입으로 매핑하는 이 확장에서는 T로 좁혀서 돌려준다.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> JdbcClient.StatementSpec.query(): JdbcClient.MappedQuerySpec<T> =
    query(T::class.java) as JdbcClient.MappedQuerySpec<T>
// end::extension[]
