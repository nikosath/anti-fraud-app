package antifraud.transactionvalidation.datastore;

import antifraud.common.Regexp;
import antifraud.transactionvalidation.Enum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionValidationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue
    Long id;
    @Min(1) long amount;
    @NotBlank @Pattern(regexp = Regexp.IP_ADDRESS) String ipAddress;
    @NotBlank @CreditCardNumber String creditCardNumber;
    Enum.RegionCode regionCode;
    LocalDateTime transactionDateTime;
    Enum.TransactionStatus transactionStatus;
    String statusJustification;
    Enum.TransactionStatus feedback;

    public TransactionValidationEntity(long amount, String ipAddress, String creditCardNumber, Enum.RegionCode regionCode, LocalDateTime transactionDateTime,
                                       Enum.TransactionStatus transactionStatus,
                                       String statusJustification) {
        this.amount = amount;
        this.ipAddress = ipAddress;
        this.creditCardNumber = creditCardNumber;
        this.regionCode = regionCode;
        this.transactionDateTime = transactionDateTime;
        this.transactionStatus = transactionStatus;
        this.statusJustification = statusJustification;
    }
}
