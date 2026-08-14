package flashcard.deep;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

// tag::class[]
@Table("DECK")
public class Deck {

    @Id
    private Long id;

    private String name;

    /** 값 객체를 같은 테이블의 POLICY_ 접두사 컬럼들로 펼친다. */
    @Embedded.Nullable(prefix = "POLICY_")
    private ReviewPolicy policy;

    /** 처음 저장될 때 프레임워크가 채운다. */
    @CreatedDate
    private LocalDateTime createdAt;

    /** 저장할 때마다 프레임워크가 갱신한다. */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Deck(String name, ReviewPolicy policy) {
        this.name = name;
        this.policy = policy;
    }
    // end::class[]

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public ReviewPolicy getPolicy() {
        return policy;
    }

    public void changePolicy(ReviewPolicy newPolicy) {
        this.policy = newPolicy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Deck{id=%d, name='%s', policy=%s}".formatted(id, name, policy);
    }
}
