package antifraud.domain.datastore;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;

import java.util.List;

public interface IStolenCardEntityDatastore {
    Result<ErrorEnum, StolenCardEntity> createStolenCard(String cardNumber);

    long countByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    List<StolenCardEntity> getAllStolenCardsOrderById();

    Result<ErrorEnum, StolenCardEntity> deleteStolenCard(String cardNumber);

}