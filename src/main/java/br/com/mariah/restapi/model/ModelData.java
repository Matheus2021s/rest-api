package br.com.mariah.restapi.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private List<ParameterData> primaryKeys;

    private List<ParameterData> nonPrimaryKeys;


    public Boolean getIsComposePrimaryKey() {
        if (Objects.isNull(this.isComposePrimaryKey)) {
            this.isComposePrimaryKey = getPrimaryKeys().size() > 1;
        }
        return this.isComposePrimaryKey;
    }

    public List<ParameterData> getPrimaryKeys() {
        if (Objects.isNull(this.primaryKeys)) {
            this.primaryKeys = getParameters().stream()
                    .filter(ParameterData::getIsPrimaryKey)
                    .toList();
        }
        return this.primaryKeys;
    }

    public List<ParameterData> getNonPrimaryKeys() {
        if (Objects.isNull(this.nonPrimaryKeys)) {
            this.nonPrimaryKeys = getParameters().stream()
                    .filter(parameterData -> !parameterData.getIsPrimaryKey())
                    .toList();
        }
        return this.nonPrimaryKeys;
    }

}
