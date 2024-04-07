package antifraud.common.datastore;

import antifraud.common.Enum;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface IConfigRepo extends CrudRepository<ConfigEntity, Long> {

    @Query("SELECT c FROM ConfigEntity c WHERE c.id.configCategory = :configCategory")
    Collection<ConfigEntity> findByConfigCategory(Enum.ConfigCategory configCategory);

    @Modifying
    @Query("UPDATE ConfigEntity c SET c.propertyValue = :propertyValue WHERE c.id.configCategory = :configCategory " +
            "AND c.id.propertyName = :propertyName")
    void updatePropertyValue(Enum.ConfigCategory configCategory, String propertyName, String propertyValue);
    default void updatePropertyValue(Enum.ConfigCategory configCategory, String propertyName, long propertyValue) {
        updatePropertyValue(configCategory, propertyName, String.valueOf(propertyValue));
    };
}
