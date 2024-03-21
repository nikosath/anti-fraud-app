package antifraud.common.datastore;

import antifraud.common.Enum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConfigEntity {

    @EmbeddedId
    private ConfigEntityId id;
    @NotBlank
    private String propertyValue;

    public static ConfigEntity create(Enum.ConfigCategory configCategory, String propertyName, String propertyValue) {
        return new ConfigEntity(new ConfigEntityId(configCategory, propertyName), propertyValue);
    }

    public Enum.ConfigCategory getConfigCategory() {
        return id.getConfigCategory();
    }

    public String getPropertyName() {
        return id.getPropertyName();
    }

}
