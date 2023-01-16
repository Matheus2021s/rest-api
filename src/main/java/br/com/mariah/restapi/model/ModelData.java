package br.com.mariah.restapi.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ModelData {

    private String name;

    @Builder.Default
    private List<ParameterData> parameters = new ArrayList<>();

    private Boolean isComposePrimaryKey;

    public Boolean getIsComposePrimaryKey() {
        if (Objects.isNull(this.isComposePrimaryKey)) {
            this.isComposePrimaryKey = getParameters().stream()
                    .filter(ParameterData::getIsPrimaryKey).count() > 1;
        }
        return this.isComposePrimaryKey;
    }

}
