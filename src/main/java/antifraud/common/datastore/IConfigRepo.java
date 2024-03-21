package antifraud.common.datastore;

import antifraud.common.Enum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface IConfigRepo extends CrudRepository<ConfigEntity, Long> {

    @Query("SELECT c FROM ConfigEntity c WHERE c.id.configCategory = :configCategory")
    Collection<ConfigEntity> findByConfigCategory(Enum.ConfigCategory configCategory);
}
