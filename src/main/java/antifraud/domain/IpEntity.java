package antifraud.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class IpEntity {

    @Id
    @GeneratedValue
    Long id;
    String ip;
}
