package antifraud.security.storage;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface IUserProfileJpaRepo extends ListCrudRepository<UserProfile, Long> {
    Optional<UserProfile> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    List<UserProfile> findAllByOrderByIdAsc();

    @Transactional
    List<UserProfile> deleteByUsernameIgnoreCase(String username);
}
