package flashcard.plus.domain;

// tag::class[]
/** 스마트 덱이 카드를 골라 오는 네 가지 조건. */
public enum SmartCondition {

    OFTEN_WRONG("자주 틀린 카드"),
    STALE("오래 안 본 카드"),
    TAGGED("특정 태그가 붙은 카드"),
    RECENT("최근에 추가한 카드");

    private final String label;

    SmartCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
// end::class[]
