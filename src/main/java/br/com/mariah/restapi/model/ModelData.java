package br.com.mariah.restapi.model;

import br.com.mariah.restapi.utils.StringUtils;
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


    public String getCamelNameFirstLetterUpper() {
        return StringUtils.getCamelCaseFirstLetterUpper(this.name);
    }

    public String getCamelNameFirstLetterLower() {
        return StringUtils.getCamelCaseFirstLetterLower(this.name);
    }

    public String getNameUpperSeparatedByUnderscore() {
        return StringUtils.getUpperSeparatedByUnderscore(this.name);
    }

    public String getNameLowerSeparatedByUnderscore() {
        return StringUtils.getLowerSeparatedByUnderscore(this.name);
    }

    public String getNameUpperSeparatedByDash() {
        return StringUtils.getUpperSeparatedByDash(this.name);
    }

    public String getNameLowerSeparatedByDash() {
        return StringUtils.getLowerSeparatedByDash(this.name);
    }
}
