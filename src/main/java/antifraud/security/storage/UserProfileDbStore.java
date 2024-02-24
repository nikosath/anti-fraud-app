package antifraud.security.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProfileDbStore implements IUserProfileStore {

    private final UserProfileJpaRepo repo;

    @Override
    public UserProfile save(UserProfile userProfile) {
        return repo.save(userProfile);
    }

    @Override
    public UserProfile save(String name, String username, String encodedPassword) {
        return repo.save(UserProfile.with(name, username, encodedPassword));
    }

    @Override
    public Optional<UserProfile> findByUsernameIgnoreCase(String username) {
        return repo.findByUsernameIgnoreCase(username);
    }

    @Override
    public boolean existsByUsernameIgnoreCase(String username) {
        return repo.existsByUsernameIgnoreCase(username);
    }

    @Override
    public List<UserProfile> findAllByOrderByIdAsc() {
        return repo.findAllByOrderByIdAsc();
    }

    @Override
    public List<UserProfile> deleteByUsernameIgnoreCase(String username) {
        return repo.deleteByUsernameIgnoreCase(username);
    }
}
