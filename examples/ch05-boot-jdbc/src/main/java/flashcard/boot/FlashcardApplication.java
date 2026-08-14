package flashcard.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// tag::class[]
@SpringBootApplication
public class FlashcardApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashcardApplication.class, args);
    }
}
// end::class[]
