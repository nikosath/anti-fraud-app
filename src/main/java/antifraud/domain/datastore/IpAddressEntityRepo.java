package antifraud.domain.datastore;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface IpAddressEntityRepo extends ListCrudRepository<IpAddressEntity, Long> {

    boolean existsByIp(String ip);

    List<IpAddressEntity> deleteByIp(String ip);

    long countByIp(String ip);

    List<IpAddressEntity> findAllByOrderByIdAsc();

}