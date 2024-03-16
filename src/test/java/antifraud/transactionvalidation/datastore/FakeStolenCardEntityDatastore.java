package antifraud.transactionvalidation.datastore;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeStolenCardEntityDatastore implements IStolenCardEntityDatastore {

    long idSequence = 0L;
    private Map<Long, StolenCardEntity> idToStolenCardEntity = new HashMap<>();
    private Map<String, StolenCardEntity> cardNumberToStolenCardEntity = new HashMap<>();

    @Override
    public synchronized Result<ErrorEnum, StolenCardEntity> createStolenCard(String cardNumber) {
        StolenCardEntity entity = StolenCardEntity.withCardNumber(cardNumber);
        idSequence += 1;
        entity.setId(idSequence);
        idToStolenCardEntity.put(entity.getId(), entity);
        cardNumberToStolenCardEntity.put(entity.getCardNumber(), entity);
        return Result.success(entity);
    }

    @Override
    public synchronized long countByCardNumber(String cardNumber) {
        return existsByCardNumber(cardNumber) ? 0 : 1;
    }

    @Override
    public synchronized boolean existsByCardNumber(String cardNumber) {
        return cardNumberToStolenCardEntity.containsKey(cardNumber);
    }

    @Override
    public synchronized List<StolenCardEntity> getAllStolenCardsOrderById() {
        return cardNumberToStolenCardEntity.values().stream().sorted().toList();
    }

    @Override
    public synchronized Result<ErrorEnum, StolenCardEntity> deleteStolenCard(String cardNumber) {
        StolenCardEntity entity = cardNumberToStolenCardEntity.get(cardNumber);
        if (entity == null) {
            return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
        }
        cardNumberToStolenCardEntity.remove(cardNumber);
        idToStolenCardEntity.remove(entity.getId());
        return Result.success(entity);
    }

}
