package antifraud.transactionvalidation.datastore;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface IStolenCardEntityRepo extends ListCrudRepository<StolenCardEntity, Long> {

    boolean existsByCardNumber(String cardNumber);

    @Transactional
    List<StolenCardEntity> deleteByCardNumber(String cardNumber);

    long countByCardNumber(String cardNumber);

    List<StolenCardEntity> findAllByOrderByIdAsc();

}