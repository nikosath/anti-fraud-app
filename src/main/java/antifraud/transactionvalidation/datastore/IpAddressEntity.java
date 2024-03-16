package antifraud.transactionvalidation.datastore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class IpAddressEntity implements Comparable<IpAddressEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String ip;

    private IpAddressEntity(String ip) {
        this.ip = ip;
    }

    public static IpAddressEntity withIp(String ip) {
        return new IpAddressEntity(ip);
    }

    @Override
    public int compareTo(IpAddressEntity other) {
        return Long.compare(this.getId(), other.getId());
    }
}
