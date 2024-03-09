package antifraud.domain.datastore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class IpAddressEntity {

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
}