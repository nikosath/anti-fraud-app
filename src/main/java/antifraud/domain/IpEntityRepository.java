package antifraud.domain;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "suspicious-ip", path = "suspicious-ip")
public interface IpEntityRepository extends ListCrudRepository<IpEntity, Long> {
}