package antifraud.transactionvalidation.datastore;

import antifraud.error.ErrorEnum;
import antifraud.error.FailedPreconditionException;
import antifraud.error.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static antifraud.error.ErrorEnum.*;

@Repository
@RequiredArgsConstructor
public class StolenCardEntityDatastore implements IStolenCardEntityDatastore {

    private final IStolenCardEntityRepo repo;

    @Override
    public Result<ErrorEnum, StolenCardEntity> createStolenCard(String cardNumber) {
        if (repo.existsByCardNumber(cardNumber)) {
            return Result.error(ENTITY_ALREADY_EXISTS);
        }
        StolenCardEntity saved = repo.save(StolenCardEntity.withCardNumber(cardNumber));
        return Result.success(saved);
    }

    @Override
    public long countByCardNumber(String cardNumber) {
        return repo.countByCardNumber(cardNumber);
    }

    @Override
    public boolean existsByCardNumber(String cardNumber) {
        return repo.existsByCardNumber(cardNumber);
    }

    @Override
    public List<StolenCardEntity> getAllStolenCardsOrderById() {
        return repo.findAllByOrderByIdAsc();
    }

    @Override
    public Result<ErrorEnum, StolenCardEntity> deleteStolenCard(String cardNumber) {
        long countByCardNumber = repo.countByCardNumber(cardNumber);
        if (countByCardNumber > 1) {
            throw new FailedPreconditionException(MULTIPLE_ENTITIES_FOUND);
        }
        if (countByCardNumber == 0) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        StolenCardEntity deleted = repo.deleteByCardNumber(cardNumber).get(0);
        return Result.success(deleted);
    }
}
