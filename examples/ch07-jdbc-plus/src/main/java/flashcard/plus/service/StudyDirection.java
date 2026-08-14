package flashcard.plus.service;

// tag::class[]
/** 학습 방향: 어느 면을 보고 어느 면을 떠올릴 것인가. */
public enum StudyDirection {

    TEXT_TO_MEANING("원문 보고 뜻 떠올리기"),
    MEANING_TO_TEXT("뜻 보고 원문 떠올리기");

    private final String label;

    StudyDirection(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
// end::class[]
