package antifraud.transactionvalidation.datastore;

import antifraud.common.Regexp;
import antifraud.transactionvalidation.RegionCodeEnum;
import antifraud.transactionvalidation.service.TransactionValidationCalculations;
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
    RegionCodeEnum regionCode;
    LocalDateTime transactionDateTime;
    TransactionValidationCalculations.TransactionStatusEnum transactionStatus;
    String statusJustification;

    public TransactionValidationEntity(long amount, String ipAddress, String creditCardNumber, RegionCodeEnum regionCode, LocalDateTime transactionDateTime,
                                       TransactionValidationCalculations.TransactionStatusEnum transactionStatus,
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
