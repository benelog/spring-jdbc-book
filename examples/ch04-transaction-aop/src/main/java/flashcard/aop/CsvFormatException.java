package flashcard.aop;

// tag::class[]
/** CSV의 형식이 잘못됐을 때 던지는 checked 예외. */
public class CsvFormatException extends Exception {

    public CsvFormatException(String message) {
        super(message);
    }
}
// end::class[]
