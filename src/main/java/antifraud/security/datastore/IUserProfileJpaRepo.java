package antifraud.security.datastore;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

// TODO: change id type
public interface IUserProfileJpaRepo extends ListCrudRepository<UserProfile, Long> {
    Optional<UserProfile> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    List<UserProfile> findAllByOrderByIdAsc();

    @Transactional
    List<UserProfile> deleteByUsernameIgnoreCase(String username);
}
