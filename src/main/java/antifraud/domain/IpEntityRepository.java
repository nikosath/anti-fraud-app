package antifraud.domain;

import org.springframework.data.repository.ListCrudRepository;

public interface IpEntityRepository extends ListCrudRepository<IpEntity, Long> {
}