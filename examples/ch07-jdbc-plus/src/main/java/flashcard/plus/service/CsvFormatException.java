package flashcard.plus.service;

/** 잘못된 CSV 행을 만나면 던진다. runtime 예외라서 트랜잭션도 함께 롤백된다. */
public class CsvFormatException extends RuntimeException {

    public CsvFormatException(int lineNumber, String line) {
        super("%d번째 줄의 형식이 잘못됐습니다: %s".formatted(lineNumber, line));
    }
}
