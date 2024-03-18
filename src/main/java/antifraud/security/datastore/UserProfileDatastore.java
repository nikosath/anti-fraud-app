package antifraud.security.datastore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileDatastore implements IUserProfileStore {

    private final IUserProfileJpaRepo repo;

    @Override
    public UserProfileEntity save(UserProfileEntity userProfile) {
        return repo.save(userProfile);
    }

//    @Override
//    public UserProfileEntity save(String name, String username, String encodedPassword) {
//        return repo.save(new UserProfileFactory().admin(name, username, encodedPassword));
//    }

    @Override
    public Optional<UserProfileEntity> findByUsernameIgnoreCase(String username) {
        return repo.findByUsernameIgnoreCase(username);
    }

    @Override
    public boolean existsByUsernameIgnoreCase(String username) {
        return repo.existsByUsernameIgnoreCase(username);
    }

    @Override
    public List<UserProfileEntity> findAllByOrderByIdAsc() {
        return repo.findAllByOrderByIdAsc();
    }

    @Override
    public Optional<UserProfileEntity> deleteByUsernameIgnoreCase(String username) {
        List<UserProfileEntity> deleted = repo.deleteByUsernameIgnoreCase(username);
        if (deleted.size() > 1) {
            throw new RuntimeException(
                    String.format("Deleted %s number of entities. The expectation was to delete only one.", deleted.size()));
        }
        if (deleted.size() == 1) {
            return Optional.of(deleted.get(0));
        }
        return Optional.empty();
    }

    @Override
    public long count() {
        return repo.count();
    }
}
