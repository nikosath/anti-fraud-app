package antifraud.security.datastore;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

// TODO: change id type
public interface IUserProfileJpaRepo extends ListCrudRepository<UserProfileEntity, Long> {
    Optional<UserProfileEntity> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    List<UserProfileEntity> findAllByOrderByIdAsc();

    @Transactional
    List<UserProfileEntity> deleteByUsernameIgnoreCase(String username);
}
