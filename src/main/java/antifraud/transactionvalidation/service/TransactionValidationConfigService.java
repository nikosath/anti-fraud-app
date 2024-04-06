package antifraud.transactionvalidation.service;

import antifraud.common.datastore.ConfigEntity;
import antifraud.common.datastore.IConfigRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static antifraud.common.Enum.ConfigCategory;
import static antifraud.common.Enum.ConfigCategory.TRANSACTION_VALIDATION;
import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_ALLOWED;
import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_MANUAL_PROCESSING;

/**
 * Holds configuration used by the {@link TransactionValidationService}. Loads up the config state from {@link IConfigRepo}
 * upon bean initialization
 */
@RequiredArgsConstructor
@Service
public class TransactionValidationConfigService implements ITransactionValidationConfigService {
    private final IConfigRepo repo;
    private TransactionValidationConfig transactionValidationConfig;

    @Override
    public void updateTransactionValidationConfig(TransactionValidationConfig newConfig) {
        updatePropertyValue(TRANSACTION_VALIDATION, AMOUNT_LIMIT_FOR_ALLOWED, newConfig.amountLimitForAllowed());
        updatePropertyValue(TRANSACTION_VALIDATION, AMOUNT_LIMIT_FOR_MANUAL_PROCESSING,
                newConfig.amountLimitForManualProcessing());
        this.transactionValidationConfig = newConfig;
    }

    private void updatePropertyValue(ConfigCategory configCategory, String propertyName, long propertyValue) {
        repo.updatePropertyValue(configCategory, propertyName, propertyValue);
    }

    @Override
    public TransactionValidationConfig getTransactionValidationConfig() {
        return transactionValidationConfig;
    }

    @PostConstruct
    private void init() {
        // TODO: persist default config in a different way, e.g. with data.sql
        persistDefaultConfig();
        loadTransactionValidationConfig();
    }

    private void persistDefaultConfig() {
        repo.save(ConfigEntity.create(TRANSACTION_VALIDATION, AMOUNT_LIMIT_FOR_ALLOWED, 200));
        repo.save(ConfigEntity.create(TRANSACTION_VALIDATION, AMOUNT_LIMIT_FOR_MANUAL_PROCESSING, 1500));
    }

    private void loadTransactionValidationConfig() {
        transactionValidationConfig = entitiesToConfig(
                repo.findByConfigCategory(ConfigCategory.TRANSACTION_VALIDATION));
    }

    private TransactionValidationConfig entitiesToConfig(Collection<ConfigEntity> entities) {
        Map<String, String> propertyNameToValue = entities.stream().collect(
                Collectors.toMap(entity -> entity.getPropertyName(), entity -> entity.getPropertyValue()));

        return new TransactionValidationConfig(
                Long.parseLong(propertyNameToValue.get(AMOUNT_LIMIT_FOR_ALLOWED)),
                Long.parseLong(propertyNameToValue.get(AMOUNT_LIMIT_FOR_MANUAL_PROCESSING))
        );

    }
}
