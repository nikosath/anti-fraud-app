package antifraud.transactionvalidation.service;

public interface ITransactionValidationConfigService {
    void updateTransactionValidationConfig(TransactionValidationConfig newConfig);

    TransactionValidationConfig getTransactionValidationConfig();
}
