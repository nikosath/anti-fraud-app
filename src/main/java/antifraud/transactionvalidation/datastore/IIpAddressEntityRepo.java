package antifraud.transactionvalidation.datastore;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface IIpAddressEntityRepo extends ListCrudRepository<IpAddressEntity, Long> {

    boolean existsByIp(String ip);

    @Transactional
    List<IpAddressEntity> deleteByIp(String ip);

    long countByIp(String ip);

    List<IpAddressEntity> findAllByOrderByIdAsc();

}