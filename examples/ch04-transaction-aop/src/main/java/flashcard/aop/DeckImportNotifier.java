package flashcard.aop;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/** 덱 가져오기 완료를 알리는 리스너. 예제에서는 발송 대신 메시지를 모아 둔다. */
@Component
public class DeckImportNotifier {

    private final List<String> sentMessages = new CopyOnWriteArrayList<>();

    // tag::listener[]
    /** 트랜잭션이 커밋된 뒤에만 호출된다. 롤백되면 호출되지 않는다. */
    @TransactionalEventListener
    public void onDeckImported(DeckImported event) {
        // 실전이라면 여기서 메일이나 메신저 API를 호출한다
        sentMessages.add("덱 '%s'에 카드 %d장이 등록되었습니다"
                .formatted(event.deckName(), event.cardCount()));
    }
    // end::listener[]

    public List<String> sentMessages() {
        return List.copyOf(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}
