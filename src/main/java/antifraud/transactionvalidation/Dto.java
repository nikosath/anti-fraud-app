package antifraud.transactionvalidation;

public class Dto {
    public record TransactionApprovalVerdict(Enum.TransactionStatus transactionStatus, String statusJustification) {
    }

}
