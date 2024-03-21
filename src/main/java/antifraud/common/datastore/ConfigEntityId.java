package antifraud.common.datastore;

import antifraud.common.Enum;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigEntityId {
    @Enumerated(EnumType.STRING)
    private Enum.ConfigCategory configCategory;
    @NotBlank
    private String propertyName;
}
