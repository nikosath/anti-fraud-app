package antifraud.transactionvalidation.service;

import antifraud.common.Enum;
import antifraud.common.IConfigChangedListener;
import antifraud.common.datastore.ConfigEntity;
import antifraud.common.datastore.IConfigRepo;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static antifraud.common.Enum.ConfigCategory;
import static antifraud.common.Enum.ConfigCategory.TRANSACTION_VALIDATION;
import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_ALLOWED;
import static antifraud.transactionvalidation.service.TransactionValidationConfig.PropertyNames.AMOUNT_LIMIT_FOR_MANUAL_PROCESSING;

@RequiredArgsConstructor
@Service
// TODO: ConfigService is work in progress
public class ConfigService implements IConfigProvider, IConfigChangedListener {
    private final IConfigRepo repo;
    @Getter
    private TransactionValidationConfig transactionValidationConfig;

    @PostConstruct
    private void init() {
        // TODO: persist default config in a different way, e.g. with data.sql
        persistDefaultConfig();
        loadTransactionValidationConfig();
    }

    private void persistDefaultConfig() {
        repo.save(ConfigEntity.create(TRANSACTION_VALIDATION, AMOUNT_LIMIT_FOR_ALLOWED, "200"));
        repo.save(ConfigEntity.create(TRANSACTION_VALIDATION, AMOUNT_LIMIT_FOR_MANUAL_PROCESSING, "1500"));
    }

    private void loadTransactionValidationConfig() {
        transactionValidationConfig = TransactionValidationConfig.from(
                repo.findByConfigCategory(ConfigCategory.TRANSACTION_VALIDATION));
    }

    @Override
    public void onConfigChanged(Enum.ConfigCategory configCategory) {
        if (configCategory == ConfigCategory.TRANSACTION_VALIDATION) {
            loadTransactionValidationConfig();
        }
    }

}
