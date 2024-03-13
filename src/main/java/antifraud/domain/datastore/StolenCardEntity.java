package antifraud.domain.datastore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StolenCardEntity implements Comparable<StolenCardEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String cardNumber;

    private StolenCardEntity(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public static StolenCardEntity withCardNumber(String ip) {
        return new StolenCardEntity(ip);
    }

    @Override
    public int compareTo(StolenCardEntity other) {
        return Long.compare(this.getId(), other.getId());
    }
}
